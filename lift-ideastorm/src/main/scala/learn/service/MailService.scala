package learn.service

import org.apache.commons.mail._

case class EmailContent(
  var hostName: String,
  var smtpPort: Int,
  var username: String,
  var password: String,
  var ssl: Boolean,
  var from: String,
  var subject: String,
  var msg: String,
  var to: List[String])

object EmailContent {
  def create = EmailContent("", -1, "", "", false, "", "", "", Nil)
  val empty = EmailContent("", -1, "", "", false, "", "", "", Nil)
}

import scala.collection.JavaConverters._

object MailService {

  def send(ec: EmailContent): String = {
    val email = new SimpleEmail
    email.setHostName(ec.hostName)
    email.setSmtpPort(ec.smtpPort)
    email.setAuthenticator(new DefaultAuthenticator(ec.username, ec.password))
    email.setSSL(ec.ssl)
    email.setFrom(ec.from)
    email.setSubject(ec.subject)
    email.setMsg(ec.msg)
    ec.to.foreach(email addTo _)
    email.send()
  }
}
