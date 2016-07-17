package nytSearchDsl

/**
  * sorting of the response
  */
sealed trait Sort

case object newest extends Sort {
  override def toString: String = "newest"
}

case object oldest extends Sort {
  override def toString: String = "oldest"
}
