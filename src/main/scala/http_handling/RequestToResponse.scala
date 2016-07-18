package http_handling

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Sink, Source}
import akka.util.ByteString
import models.docs.Doc

import scala.concurrent.Future

/**
  * transforms http response to future or to stream
  */
trait RequestToResponse extends Protocols with Flows {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  def responseToDocs(httpReq: Seq[HttpRequest], host: String): Future[Seq[Doc]] = {
    responseAsStream(httpReq, host)
      .runWith(Sink.seq)
  }

  def responseAsStream(httpReq: Seq[HttpRequest], host: String): Source[Doc, Any] =
    Source(httpReq.toList.map(_ -> 1))
      .via(connecFlow(host))
      .via(dropBrokenRequests)
      .via(checkHttpStatus)
      .via(Framing.delimiter(ByteString(delimiter), 80000, allowTruncation = true))
      .drop(1)
      .map(_.utf8String)
      .via(readdDelimiter)
      .via(deserialize)
      .mapConcat(_.response.docs)
  }
