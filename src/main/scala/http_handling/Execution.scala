package http_handling

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.Source
import models.docs.Doc
import nytSearchDsl.SearchDefinition

import scala.concurrent.Future


/**
  * Executes the search to nytimes
  */
object Execution extends RequestToResponse {

  val decider: Supervision.Decider = {
    case ex: spray.json.DeserializationException =>
      ex.printStackTrace()
      Supervision.Resume
  }

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))

  val host = """api.nytimes.com"""
  val url = """https://api.nytimes.com/svc/search/v2/articlesearch.json"""

  def searchDefToURI(searchDef: SearchDefinition, key: String): Seq[Uri] = {

    val params = Map("q" -> searchDef.query, "api_key" -> key) ++
      searchDef.opFilter.map("qf" -> _) ++
      searchDef.opStartDate.map("begin_date" -> _) ++
      searchDef.opEndDate.map("end_date" -> _) ++
      searchDef.opSort.map("sort" -> _.toString) ++
      searchDef.opHighlight.map("hl" -> _.toString)

    (0 to 9).map { page =>
      Uri(url).withQuery(Query(params + ("page" -> page.toString)))
    }
  }

  def execute(searchDefinition: SearchDefinition, key: String): Future[Seq[Doc]] = {

    val listOfRequests = searchDefToURI(searchDefinition, key).map(uri => HttpRequest(uri = uri))

    responseToDocs(listOfRequests, host)
  }

  def executeAsStream(searchDefinition: SearchDefinition, key: String): Source[Doc, Any] = {

    val listOfRequests = searchDefToURI(searchDefinition, key).map(uri => HttpRequest(uri = uri))

    responseAsStream(listOfRequests, host)
  }

}
