package learn.snippet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, SessionVar, RequestVar, S }
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds
import JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._

import learn.model.AccountRecord
import learn.service.SessionManager._

import me.yangbajing.util.Utils._

private class AR2 { self =>
  var username: String = ""
  var email: String = ""
  var password: String = ""
  var password2: String = ""
  var age: Int = 0

  def validate: Boolean = {
    username.length > 1 && password == password2 && password.length > 5 && age > 12
  }
}

/**
 * 纯Ajax版的用户注册
 */
object Register2Snippet extends me.yangbajing.log.Loggable {
  private object reqAccount extends RequestVar[AR2](new AR2)

  private val SUCCESS = "success"
  private val ERROR = "error"
  private val WARNING = "warning"

  def render(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      "#username" #> SHtml.ajaxText(reqAccount.username, validateUsername(_)) &
        "#email" #> SHtml.ajaxText(reqAccount.email, validateEmail(_)) &
        "#password1" #> SHtml.ajaxText("", validatePassword1(_), "type" -> "password") &
        "#password2" #> SHtml.ajaxText("", validatePassword2(_), "type" -> "password") &
        "#age" #> SHtml.ajaxText(if (reqAccount.age < 1) "" else reqAccount.age.toString, validateAge(_)) &
        "type=submit" #> SHtml.ajaxButton("注册", () => register)

    (".form-horizontal" #> SHtml.ajaxForm(cssSel(nodeSeq)))(nodeSeq)
  }

  import net.liftweb.mongodb.record.field.Password
  private def register() = {
    logger.debug(reqAccount.is)

    if (reqAccount.is.validate) {
      val account = AccountRecord.createRecord
        .username(reqAccount.is.username)
        .password(Password(reqAccount.is.password))
        .email(reqAccount.is.email)
        .age(reqAccount.is.age)
        .save

      logger.info("用户: %s 注册成功，请登陆" format account.username.is)
      JsCmds.Run("window.location='/session/login'")
    } else {
      logger.error("注册失败")
      validateUsername(reqAccount.is.username) &
        validateEmail(reqAccount.is.email) &
        validatePassword1(reqAccount.is.password) &
        validatePassword2(reqAccount.is.password2) &
        validateAge(reqAccount.is.age.toString)
    }
  }

  private def jscmd(idFix: String, state: String, msg: String) = {
    JsCmds.Run("$('#group_%s').removeClass('success warning error');" format idFix) &
      JsCmds.Run("$('#group_%s').addClass('%s');" format (idFix, state))
  }

  import net.liftweb.json.JsonDSL._
  private def validateUsername(username: String) = {
    val (state, msg) =
      if (username.length < 1 || username.length > 32) {
        (ERROR, "用户长度需在1 ~ 32个字符之间")
      } else if (AccountRecord.count("username" -> username) > 0) {
        (WARNING, "用户名已存在，请换一个试试")
      } else {
        reqAccount.is.username = username
        (SUCCESS, "")
      }

    jscmd("username", state, msg)
  }

  private def validateEmail(email: String) = {
    val (state, msg) =
      if (emailValidate(email)) {
        reqAccount.is.email = email
        (SUCCESS, "")
      } else {
        (ERROR, "邮件地址不合法")
      }

    jscmd("email", state, msg)
  }

  private def validatePassword1(password: String) = {
    val (state, msg) =
      if (password.length > 5 && password.length < 65) {
        reqAccount.is.password = password
        (SUCCESS, "")
      } else {
        (ERROR, "密码长度范围在[6, 64]")
      }

    jscmd("password1", state, msg)
  }

  private def validatePassword2(password: String) = {
    reqAccount.is.password2 = password

    val (state, msg) =
      if (reqAccount.password != "" && reqAccount.is.password2 == reqAccount.password) {
        (SUCCESS, "")
      } else {
        (ERROR, "密码不匹配")
      }

    jscmd("password2", state, msg)
  }

  private def validateAge(ageStr: String) = {
    val (state, msg) = asInt(ageStr) match {
      case Full(age) if age > 13 && age < 200 =>
        reqAccount.is.age = age
        (SUCCESS, "")
      case _ =>
        (ERROR, "年龄需要在[14, 199]范围内")
    }

    jscmd("age", state, msg)
  }

}
