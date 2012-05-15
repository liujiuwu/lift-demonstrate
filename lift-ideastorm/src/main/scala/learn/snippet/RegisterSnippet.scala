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

private case class AR(
  var username: String = "",
  var email: String = "",
  var password: String = "",
  var password2: String = "",
  var age: Int = 0) {

  def validate: Boolean = {
    username.length > 1 && password == password2 && password.length > 5 && age > 12
  }
}

object RegisterSnippet {
  private object reqAccount extends RequestVar[AR](new AR)

  private val SUCCESS = "success"
  private val ERROR = "error"
  private val WARNING = "warning"

  private def jscmd(idFix: String, state: String, msg: String, value: Box[String] = Empty) = {
    val cmds =
      JsCmds.Run("$('#group_%s').removeClass('success,error,warning');" format idFix) &
        JsCmds.Run("$('#group_%s').addClass('%s');" format (idFix, state)) &
        JqSetHtml("group_%s .help-inline" format idFix, Text(msg))

    value.foldLeft(cmds)((c, v) => c & SetValById("hidden_" + idFix, v))
  }

  import net.liftweb.json.JsonDSL._
  private def validateUsername(username: String) = {
    var state = ""
    var msg = ""
    var value: Box[String] = None

    if (username.length < 1 || username.length > 32) {
      state = ERROR
      msg = "用户长度需在1 ~ 32个字符之间"
    } else if (AccountRecord.count("username" -> username) > 0) {
      state = WARNING
      msg = "用户名已存在，请换一个试试"
    } else {
      state = SUCCESS
      value = Full(username)
    }

    jscmd("username", state, msg, value)
  }

  private def validateEmail(email: String) = {
    var state = ERROR
    var msg = "邮件地址不合法"
    var value: Box[String] = None

    if (emailValidate(email)) {
      state = SUCCESS
      value = Full(email)
      msg = ""
    }

    jscmd("email", state, msg, value)
  }

  private def validatePassword1(password: String) = {
    var state = ""
    var msg = ""
    var value: Box[String] = None

    if (password.length > 5 && password.length < 65) {
      state = SUCCESS
      value = Full(password)
      reqAccount.is.password = password
    } else {
      msg = "密码长度范围在[6, 64]"
      state = ERROR
    }

    jscmd("password1", state, msg, value)
  }

  private def validatePassword2(password: String) = {
    var state = ""
    var msg = ""
    var value: Box[String] = None

    if (reqAccount.password != "" && password == reqAccount.password) {
      state = SUCCESS
      value = Full(password)
    } else {
      msg = "密码不匹配"
      state = ERROR
    }

    jscmd("password2", state, msg, value)
  }

  private def validateAge(ageStr: String) = {
    var state = ""
    var msg = ""
    var value: Box[String] = None

    asInt(ageStr) match {
      case Full(age) if age > 13 && age < 200 =>
        state = SUCCESS
        value = Full(ageStr)
      case _ =>
        state = ERROR
        msg = "年龄需要在[14, 199]范围内"
    }

    jscmd("age", state, msg, value)
  }

  def render(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel =
      "#username" #> (SHtml.ajaxText(reqAccount.username, validateUsername(_)) ++
        SHtml.hidden(reqAccount.is.username = _, "", "id" -> "hidden_username")) &
        "#email" #> (SHtml.ajaxText(reqAccount.email, validateEmail(_)) ++
          SHtml.hidden(reqAccount.email = _, "", "id" -> "hidden_email")) &
          "#password1" #> (SHtml.ajaxText("", validatePassword1(_), "type" -> "password") ++
            SHtml.hidden(reqAccount.password = _, "", "id" -> "hidden_password1")) &
            "#password2" #> (SHtml.ajaxText("", validatePassword2(_), "type" -> "password") ++
              SHtml.hidden(reqAccount.password2 = _, "", "id" -> "hidden_password2")) &
              "#age" #> (SHtml.ajaxText(if (reqAccount.age < 1) "" else reqAccount.age.toString, validateAge(_)) ++
                SHtml.hidden(v => reqAccount.age = asInt(v).openOr(-1), "", "id" -> "hidden_age")) &
                "type=submit" #> SHtml.submit("注册", register)

    cssSel(nodeSeq)
  }

  import net.liftweb.mongodb.record.field.Password
  private def register() = {
    val account = AccountRecord.createRecord
      .username(reqAccount.is.username)
      .password(Password(reqAccount.is.password))
      .email(reqAccount.is.email)
      .age(reqAccount.is.age)
      .save

    S.notice("用户: %s 注册成功，请登陆" format account.username.is)
    S.redirectTo("/session/login")
  }
}
