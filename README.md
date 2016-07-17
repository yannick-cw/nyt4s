### nyt4s - New York times scala api

nyt4s is a non blocking, type safe DSL for the New York Times rest api.  

You need to get an nyt api key [here](https://developer.nytimes.com/).

#### Example usage

This is a basic search with the future based execution
```scala
import nytSearchDsl._

val basicSearch = search query "scala"
val futureDocs: Future[Docs] = basicSearch execute "your nyt api key here"

futureDocs.onSuccess {
   case listOfDocs => listOfDocs.foreach(println) 
}
```

If you'd prefer to work with streams, you can execute the stream based search
```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import nytSearchDsl._
import scala.concurrent.ExecutionContext.Implicits.global

implicit val system = ActorSystem()
implicit val materializer = ActorMaterializer()
  
val basicSearch = search query "scala"
val futureDocs: Source[Doc, Any] = basicSearch executeAsStream "your nyt api key here"

futureDocs.runForeach(println)
```


#### Additional search parameters

There are some parameters to specify the search:

 * `filter` Filtered search query using standard Lucene syntax.
 * `startDate` Restricts responses to results with publication dates of the date specified or later.
 * `endDate` Restricts responses to results with publication dates of the date specified or earlier.
 * `sort` By default, search results are sorted by their relevance to the query term (q). Use the sort parameter to sort by pub_date.
 * `highlight` Enables highlighting in search results. When set to true, the query term (q) is highlighted in the headline and lead_paragraph fields.

##### More examples

search with all parameters
```scala
import org.joda.time.LocalDate

val date = LocalDate.now
search query "New York Times" endDate date startDate date.minusDays(100) filter "filter" highlight true sort newest

```