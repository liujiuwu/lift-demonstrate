package learn.model

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.util.Helpers
import net.liftweb.json.JsonDSL._

object Account {
  val cookieName = "learn.mongodb"

  def create(): Account = {
    new Account(AccountRecord.createRecord)
  }

  def apply(cookie: Box[HTTPCookie]): Box[Account] = {
    def unpackToken(token: String): Box[(String, String)] = token.split(":").toList match {
      case username :: token :: Nil => Full(username -> token)
      case _ => Empty
    }

    def tokenEq(token1: String, token2: String) =
      if (token1 == token2) Full(true)
      else Empty

    for (
      HTTPCookie(cookieName, Full(token), _, _, _, _, _, _) <- cookie ?~ "Cookie不存在";
      (username, _) <- unpackToken(token) ?~ "Cookie读取错误";
      record <- AccountRecord.find("username" -> new String(Helpers.base64Decode(username))) ?~ "用户不存在";
      _ <- tokenEq(record.token.is, token) ?~ "Token不匹配"
    ) yield {
      new Account(record, true)
    }

  }

  def apply(username: String, password: String): Box[Account] = {
    println("username: %s\npassword: %s" format (username, password))

    def passwordEq(record: AccountRecord) =
      if (record.password.isMatch(password)) Full(true)
      else Failure("密码错误")

    for (
      record <- AccountRecord.find("username" -> username) ?~ "用户不存在";
      _ <- passwordEq(record)
    ) yield {
      new Account(record)
    }
  }

  def find(accountId: String): Box[Account] = AccountRecord.find(accountId).map(new Account(_))

  def findAll: List[Account] = AccountRecord.findAll.map(new Account(_))
}

import org.apache.commons.codec.digest.DigestUtils
import org.bson.types.ObjectId

case class CaseAccount(id: String, email: String, username: String, age: Int)

class Account private (record: AccountRecord, var remember: Boolean = false) {
  def is = record

  def httpCookie: HTTPCookie = {
    val maxAge = 60 * 60 * 24 * 14 // 保存两周

    // 更新token。将value值做为token保存下来，用户下次使用自动登陆功能时用以进行验证
    // TODO 应判断maxAge过期时间来决定是否需要更新db中的token值
    if (record.token.is == "") {
      val value = Helpers.base64Encode(record.username.is.getBytes) + ":" + DigestUtils.shaHex(new java.util.Date().toString)
      record.token(value).save
    }

    new HTTPCookie(Account.cookieName, Full(record.token.is), Empty, Full("/"), Full(maxAge), Empty, Empty)
  }

  def save = {
    record.save
    this
  }

  val id: String = record._id.is.toString
  val _id: ObjectId = record._id.is

  def username: String = record.username.is
  def username_(u: String) {
    record.username(u)
  }

  def email: String = record.email.is
  def email_(e: String) {
    record.email(e)
  }

  def age: Int = record.age.is
  def age_(a: Int) {
    record.age(a)
  }
}
