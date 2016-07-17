package nytSearchDsl

import org.joda.time.LocalDate
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by 437580 on 17/07/16.
  */
class NytSearchDslSpec extends FunSpec with Matchers {

  describe("The nytSearch dsl") {

    it("should create valid basic SearchDefinition") {
      val searchDefinition = search query "abc"
      searchDefinition should be(SearchDefinition("abc"))
    }

    it("should create valid SearchDefinition with all features") {
      val date = LocalDate.now

      val searchDefinition = search query "abc" endDate date startDate date filter "filter" highlight true sort newest
      searchDefinition should be(SearchDefinition("abc", Some("filter"), Some(date), Some(date), Some(newest), Some(true)))
    }
  }

}
