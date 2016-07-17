import org.joda.time.LocalDate

/**
  * implicit date conversion
  */
package object nytSearchDsl {
  implicit def dateToString(localDate: LocalDate): String = localDate.toString("yyyyMMdd")
}
