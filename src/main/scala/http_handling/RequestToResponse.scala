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

  def responseToDocs(httpReq: HttpRequest, host: String): Future[Seq[Doc]] = {
    responseAsStream(httpReq, host)
      .runWith(Sink.seq)
  }

  def responseAsStream(httpReq: HttpRequest, host: String): Source[Doc, Any] =
    Source.single(httpReq)
      .via(connecFlow(host))
      .via(checkHttpStatus)
      .via(Framing.delimiter(ByteString(delimeter), 80000, allowTruncation = true))
      .map(_.utf8String)
      .drop(1)
      .via(readdDelimiter)
      .via(removeTrailingComma)
      .via(cutGarbage)
      .via(deserialize)
}
