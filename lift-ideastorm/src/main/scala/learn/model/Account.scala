package learn.model

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.util.Helpers
import net.liftweb.json.JsonDSL._

object Account extends me.yangbajing.log.Loggable {
  val cookieName = "learn.mongodb"

  def create(): AccountImpl = {
    new AccountImpl(AccountRecord.createRecord)
  }

  def apply(cookie: Box[HTTPCookie]): Box[AccountImpl] = {
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
      new AccountImpl(record, true)
    }

  }

  def apply(username: String, password: String): Box[AccountImpl] = {
    def passwordEq(record: AccountRecord) =
      if (record.password.isMatch(password)) Full(true)
      else Failure("密码错误")

    for (
      record <- AccountRecord.find("username" -> username) ?~ "用户不存在";
      _ <- passwordEq(record)
    ) yield {
      new AccountImpl(record)
    }
  }

  def httpCookie(accountId: String): Box[HTTPCookie] = {
    find(accountId).map(_.httpCookie)
  }

  /**
   * 对于find 方法，可缓存
   */
  def find(accountId: String): Box[AccountImpl] = AccountRecord.find(accountId).map(new AccountImpl(_))
  def findByUsername(username: String): Box[AccountImpl] = AccountRecord.find("username" -> username).map(new AccountImpl(_))
  def findAll: List[AccountImpl] = AccountRecord.findAll.map(new AccountImpl(_))

  // 基于casbah的原子操作，避免每次都聚会整个文档
  def addUnreadInfomationId(id: String*) {
  }
  def removeUnreadInfomationId(id: String*) {
  }
}

import org.apache.commons.codec.digest.DigestUtils
import org.bson.types.ObjectId

case class Account(id: String, email: String, username: String, age: Int, unreadInfomationIds: List[String])

class AccountImpl(record: AccountRecord, var remember: Boolean = false) {
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

  @throws(classOf[IllegalArgumentException])
  def save = {
    if (AccountRecord.count("username" -> username) > 0)
      throw new IllegalArgumentException("用户名: %s 已存在" format username)

    record.save
    this
  }

  def immutable = {
    Account(id, email, username, age, unreadInfomationIds)
  }

  def unreadInfomationIds: List[String] = record.unreadInfomationIds.is.list
  def setUnreadInfomationIds(list: List[String]) = {
    record.unreadInfomationIds(JsonDataList(list))
    this
  }

  val id: String = record._id.is.toString
  val _id: ObjectId = record._id.is

  def username: String = record.username.is
  def setUsername(u: String) = {
    record.username(u)
    this
  }

  def email: String = record.email.is
  def setEmail(e: String) = {
    record.email(e)
    this
  }

  def age: Int = record.age.is
  def setAge(a: Int) = {
    record.age(a)
    this
  }

  override def toString() = "[id: %s, username: %s]" format (id, username)
}
