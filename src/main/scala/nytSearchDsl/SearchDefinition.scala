package nytSearchDsl

import akka.stream.scaladsl.Source
import models.docs.{Doc, Docs}
import org.joda.time.LocalDate
import http_handling.Execution

import scala.concurrent.Future

/**
  * The nyt4s search dsl
  * see `https://developer.nytimes.com/` for more
  */
case class SearchDefinition(
                             query: String,
                             opFilter: Option[String] = None,
                             opStartDate: Option[String] = None,
                             opEndDate: Option[String] = None,
                             opSort: Option[Sort] = None,
                             opHighlight: Option[Boolean] = None
                           ) {

  /**
    * Filtered search query using standard Lucene syntax.
    *
    * @param filter
    * @return
    */
  def filter(filter: String): SearchDefinition =
    SearchDefinition(
      query, Some(filter), opStartDate, opEndDate, opSort, opHighlight)

  /**
    * Restricts responses to results with publication dates of the date specified or later.
    *
    * @param startDate
    * @return
    */
  def startDate(startDate: LocalDate): SearchDefinition =
    SearchDefinition(
      query, opFilter, Some(startDate), opEndDate, opSort, opHighlight)

  /**
    * Restricts responses to results with publication dates of the date specified or earlier.
    *
    * @param endDate
    * @return
    */
  def endDate(endDate: LocalDate): SearchDefinition =
    SearchDefinition(
      query, opFilter, opStartDate, Some(endDate), opSort, opHighlight)

  /**
    * By default, search results are sorted by their relevance to the query term (q). Use the sort parameter to sort by pub_date.
    * Allowed values are:
    * - newest
    * - oldest
    *
    * @param sort
    * @return
    */
  def sort(sort: Sort): SearchDefinition =
    SearchDefinition(
      query, opFilter, opStartDate, opEndDate, Some(sort), opHighlight)

  /**
    * Enables highlighting in search results. When set to true, the query term (q) is highlighted in the headline and lead_paragraph fields.
    *
    * @param highlight
    * @return
    */
  def highlight(highlight: Boolean): SearchDefinition =
    SearchDefinition(
      query, opFilter, opStartDate, opEndDate, opSort, Some(highlight))

  /**
    * Execute async
    *
    * @param key put in your nytimes api key here
    * @return Future[Docs] containing a list of the result docs
    * if the api key is wrong or search term is invalid the future fails with IllegalArgumentException
    */
  def execute(key: String): Future[Seq[Doc]] = Execution.execute(this, key)

  /**
    * Executs search an returns a stream of docs
    *
    * @param key put in your nytimes api key here
    * @return
    */
  def executeAsStream(key: String): Source[Doc, Any] = Execution.executeAsStream(this, key)
}


/**
  * entrypoint to search, call `search query "searchterm"`
  */
case object search {
  /**
    * Search query term. Search is performed on the article body, headline and byline.
    *
    * @param query
    * @return
    */
  def query(query: String): SearchDefinition = SearchDefinition(query)
}
