package models.docs

/**
  * models for nyt4s
  */
case class Headline(main: Option[String], kicker: Option[String])

case class Keywords(rank: Option[String], name: Option[String], value: Option[String])

case class Person(organization: Option[String], role: Option[String], firstname: Option[String], rank: Option[Int], lastname: Option[String])

case class Byline(original: Option[String], person: List[Person])

case class Multimedia(url: Option[String],
                      format: Option[String],
                      height: Option[Int],
                      width: Option[Int],
                      `type`: Option[String],
                      subtype: Option[String],
                      caption: Option[String],
                      copyright: Option[String])

case class Doc(web_url: Option[String],
               snippet: Option[String],
               lead_paragraph: Option[String],
               `abstract`: Option[String],
               print_page: Option[String],
               blog: List[String],
               source: Option[String],
               headline: Option[Headline],
               keywords: List[Keywords],
               pub_date: Option[String],
               document_type: Option[String],
               news_desk: Option[String],
               section_name: Option[String],
               subsection_name: Option[String],
               byline: Option[Byline],
               type_of_material: Option[String],
               _id: Option[String],
//               word_count: Option[String],
               slideshow_credits: Option[String],
               multimedia: List[Multimedia]
              )

case class Outter(response: Docs)
case class Docs(docs: List[Doc])