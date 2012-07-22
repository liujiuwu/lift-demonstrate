package learn.model

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class RecordTest extends FlatSpec with ShouldMatchers {

  new bootstrap.liftweb.Boot().boot

  it should ("Test TestRecord") in {
    val defaultDoc = MTestRecord("xxxxx").createRecord
    println("default doc: " + defaultDoc)
    defaultDoc.save

    val nnnnnDoc = MTestRecord("nnnnn").createRecord
    println("nnnnn doc: " + nnnnnDoc)
    nnnnnDoc.save
  }
}

