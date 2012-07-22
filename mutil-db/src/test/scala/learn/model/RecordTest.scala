package learn.model

import bootstrap.liftweb.NNNNNMongoIdentifier

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class RecordTest extends FlatSpec with ShouldMatchers {

  new bootstrap.liftweb.Boot().boot

  it should ("Test TestRecord") in {

    val defaultDoc = MTestRecord().createRecord
    println("default doc: " + defaultDoc)

    val nnnnnDoc = MTestRecord(NNNNNMongoIdentifier).createRecord
    println("nnnnn doc: " + nnnnnDoc)
    
    defaultDoc.save
    
    nnnnnDoc.save
  }

}

