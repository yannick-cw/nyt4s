package http_handling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.docs._
import spray.json.DefaultJsonProtocol

/**
  * protocols for implicit conversion of models
  */
trait Protocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val headlineFormat = jsonFormat2(Headline)
  implicit val keywodsFormat = jsonFormat3(Keywords)
  implicit val personFormat = jsonFormat5(Person)
  implicit val bylineFormat = jsonFormat2(Byline)
  implicit val multimediaFormat = jsonFormat8(Multimedia)
  implicit val docFormat = jsonFormat19(Doc)
  implicit val docsFormat = jsonFormat1(Docs)
  implicit val outterFormat = jsonFormat1(Outter)
}
