package http_handling

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HostConnectionPool
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import models.docs.Outter
import spray.json.JsonParser

import scala.util.{Failure, Success, Try}

/**
  * The flows used for handling the http response
  */
trait Flows extends Protocols {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  val delimiter: String = """{"response"""

  val log = Flow[ByteString].map{ bytes =>
    println(bytes.utf8String)
    println("-------------------------")
    bytes
  }

  val readdDelimiter = Flow[String].map(str => delimiter + str)

  val deserialize = Flow[String].mapConcat{ str =>
    Try(JsonParser(str).convertTo[Outter]) match {
      case Success(outter) => outter :: Nil
      case Failure(ex) =>
        println(s"One deserialization failed with: ${ex.getMessage}")
        Nil
    }
  }

  val dropBrokenRequests = Flow[(Try[HttpResponse], Int)]
    .map(_._1)
    .collect{ case Success(httpRes) => httpRes }

  val connecFlow: String => Flow[(HttpRequest, Int), (Try[HttpResponse], Int), HostConnectionPool] = host => Http().cachedHostConnectionPool[Int](host)

  val checkHttpStatus =
    Flow[HttpResponse].flatMapConcat { response =>
      response.status match {
        case StatusCodes.OK => response.entity.dataBytes

        case StatusCodes.Forbidden =>
          response.entity.dataBytes.runWith(Sink.ignore)
          Source.failed(new IllegalArgumentException("Please check your api key, seems to be invalid"))

        case _ =>
          response.entity.dataBytes.map(_.utf8String).runForeach(println)
          Source.failed(new IllegalArgumentException("Something went wrong, check your query"))
      }
    }

}
