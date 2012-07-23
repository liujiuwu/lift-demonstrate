package learn.model

trait MTest {
  def createdAt: java.util.Date
  def updatedAt: java.util.Date
}

object MTest {
  def create(jndiName: String) =  MTestRecord(jndiName).createRecord
}
