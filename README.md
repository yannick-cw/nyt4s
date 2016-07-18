## nyt4s - New York times scala api

nyt4s is a non blocking, type safe DSL for the New York Times rest api.  

You need to get an nyt api key [here](https://developer.nytimes.com/signup).

For more information on the new york times api visit: https://developer.nytimes.com/

### Quickstart

Get it from  [![](https://jitpack.io/v/yannick-cw/nyt4s.svg)](https://jitpack.io/#yannick-cw/nyt4s)  
sbt:
```scala
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.yannick-cw" % "nyt4s" % "0.01"	
```

 1. To get started you need to add the import `import nytSearchDsl._` to your class.
 2. define your search: `val toSearch = search query "what to search" highlighting true` and add additional parameters like highlighting
 3. execute your search either as streaming result or future result `toSearch execute "api key"` or `toSearch executeAsStream "api key"`


### Examples

This is a basic search with the future based execution
```scala
import nytSearchDsl._
import scala.concurrent.ExecutionContext.Implicits.global

val basicSearch = search query "scala"
val futureDocs: Future[Seq[Doc]] = basicSearch execute "your nyt api key here"

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

#### More examples

search with all parameters
```scala
import org.joda.time.LocalDate

val date = LocalDate.now
search query "New York Times" endDate date startDate date.minusDays(100) filter "filter" highlight true sort newest

```

### the result entity

As a result you either get a Stream of `Doc` or a List of `Doc`.  
<details>
 <summary>The Doc case class consists of the following fields:</summary>
 
```scala
 case class Doc(web_url: Option[String],
                snippet: Option[String],
                lead_paragraph: Option[String],
                `abstract`: Option[String],
                print_page: Option[String],
                blog: List[String],
                source: Option[String],
                headline: Headline,
                keywords: List[Keywords],
                pub_date: Option[String],
                document_type: Option[String],
                news_desk: Option[String],
                section_name: Option[String],
                subsection_name: Option[String],
                byline: Byline,
                type_of_material: Option[String],
                _id: Option[String],
                word_count: Option[String],
                slideshow_credits: Option[String],
                multimedia: List[Multimedia]
               )
```
 
 Where:
 
```scala
 case class Headline(main: Option[String], kicker: Option[String])
 
 case class Keywords(rank: Option[String], name: Option[String], value: Option[String])
 
 case class Person(organization: Option[String], role: Option[String], firstname: Option[String], rankt: Option[String], lastname: Option[String])
 
 case class Byline(original: Option[String], person: List[Person])
 
 case class Multimedia(url: Option[String],
                       format: Option[String],
                       height: Int,
                       width: Int,
                       `type`: Option[String],
                       subtype: Option[String],
                       caption: Option[String],
                       copyright: Option[String])
```
 </details>