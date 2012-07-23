package learn.model

class MTestImpl(record: MTestRecordWrapper#Record) extends MTest {
  def createdAt: java.util.Date = record.createdAt.is.getTime

  def updatedAt: java.util.Date = record.updatedAt.is.getTime
}







