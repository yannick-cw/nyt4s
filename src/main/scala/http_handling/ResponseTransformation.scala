package http_handling

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Sink, Source}
import akka.util.ByteString
import models.docs.{Doc, Docs, Outter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * transforms http response to future or to stream
  */
trait ResponseTransformation extends Protocols with Flows {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  def responseToDocs(futureRes: Future[HttpResponse]): Future[Docs] = {
    futureRes.flatMap(response => response.status match {
      case OK => Unmarshal(response.entity).to[Outter].map(_.response)
      case Forbidden => Future.failed(new IllegalArgumentException("Please check your api key, seems to be invalid"))
      case BadRequest =>
        response.entity.dataBytes.runWith(Sink.head).map(_.utf8String).flatMap { str =>
          Future.failed(new IllegalArgumentException(str))
        }
    })
  }

  def reponseAsStream(source: Source[HttpResponse, Any]): Source[Doc, Any] =
    source
      .via(checkHttpStatus)
      .via(Framing.delimiter(ByteString(delimeter), 80000, true))
      .map(_.utf8String)
      .drop(1)
      .via(readdDelimiter)
      .via(removeTrailingComma)
      .via(cutGarbage)
      .via(deserialize)
}
