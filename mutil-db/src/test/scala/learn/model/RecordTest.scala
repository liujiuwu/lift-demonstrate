package learn.model

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class RecordTest extends FlatSpec with ShouldMatchers {

  new bootstrap.liftweb.Boot().boot

  it should ("Test TestRecord") in {
    val defaultDoc: MTestRecordWrapper#Record = MTestRecord("xxxxx").createRecord
    println("default doc: " + defaultDoc)
    defaultDoc.save

    val nnnnnDoc: MTestRecordWrapper#Record = MTestRecord("nnnnn").createRecord
    println("nnnnn doc: " + nnnnnDoc)
    nnnnnDoc.save
  }

  it should ("Test TestImpl") in {
    val defaultDoc: MTestRecordWrapper#Record = MTest.create("xxxxx")
    println("default doc: " + defaultDoc)
    defaultDoc.save

    val nnnnnDoc: MTestRecordWrapper#Record = MTest.create("nnnnn")
    println("nnnnn doc: " + nnnnnDoc)
    nnnnnDoc.save
  }
}
