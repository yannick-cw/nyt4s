package http_handling

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import models.docs.Doc
import spray.json.JsonParser

import scala.concurrent.Future

/**
  * The flows used for handling the http response
  */
trait Flows extends Protocols {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  val jsonEnding: String = """]},"status":"OK","copyright":"Copyright (c) 2013 The New York Times Company.  All Rights Reserved."}"""
  val delimeter: String = """{"web_url":"""

  val checkHttpStatus =
    Flow[HttpResponse].flatMapConcat { response =>
      response.status match {
        case OK => response.entity.dataBytes

        case Forbidden =>
          response.entity.dataBytes.runWith(Sink.ignore)
          Source.failed(new IllegalArgumentException("Please check your api key, seems to be invalid"))

        case _ =>
          response.entity.dataBytes.runWith(Sink.ignore)
          Source.failed(new IllegalArgumentException("Something went wrong, check your query"))
      }
    }

  val cutGarbage =
    Flow[String].map(s => if (s.endsWith(jsonEnding)) s.dropRight(jsonEnding.length) else s)

  val readdDelimiter = Flow[String].map(str => delimeter + str)

  val removeTrailingComma = Flow[String].map(str => if (str.last == ',') str.dropRight(1) else str)

  val deserialize = Flow[String].map(str => JsonParser(str).convertTo[Doc])

  val connecFlow: String => Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = host => Http().outgoingConnection(host)

}
