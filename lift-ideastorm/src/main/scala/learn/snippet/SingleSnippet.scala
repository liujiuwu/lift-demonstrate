package learn.snippet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery.{ JqJsCmds }
import net.liftweb.json.JsonDSL._

import learn.model._
import learn.web.Y
import learn.service._

class SingleSnippet extends me.yangbajing.log.Loggable {
  private object reqAccount extends RequestVar[Box[AccountRecord]](findRecord)
  private object reqHref extends RequestVar[Box[String]](findReqHref)

  def render: CssSel = {
    //    S.appendJs(hrefHash("yj-a-message" -> "message", "yj-a-temp2" -> "temp2", "yj-a-edit" -> "edit", "yj-a-send_email" -> "send_email"))
    S.appendJs(ready)

    "#yj-sidebar *" #> sidebar &
      "#yj-container-main *" #> reqHref.is.dmap(index) {
        /*        case "message" =>
          message
        case "temp2" =>
          temp2
        case "edit" =>
          edit
        case "send_mail" =>
          sendEmail */
        case _ =>
          index
      }
  }

  def sidebar: NodeSeq = {
    def sidebarLi(id: String, text: String, locationHash: String, template: NodeSeq) =
      <li>{
        Y.ajaxA(text, Full("#" + locationHash), () => {
          JsCmds.SetHtml("yj-container-main", template) &
            JE.JsRaw("window.location.hash='%s'" format locationHash).cmd
        }, "id" -> id)
      }</li>

    Y.template("single/sidebar").map { nodeSeq =>
      val links = sidebarLi("yj-a-message", "Message", "href=message", message) ++
        sidebarLi("yj-a-temp2", "Temp2", "href=temp2", temp2) ++
        sidebarLi("yj-a-edit", "编辑", "href=edit", edit) ++
        sidebarLi("yj-a-send_email", "发送邮件", "href=send_email", sendEmail)

      (".nav *" #> links)(nodeSeq)
    } openOr Text("模板: single/sidebar 未找到")
  }

  private def index: NodeSeq = Y.template("single/index").map { nodeSeq =>
    val account = Account.find(SessionManager.theAccountId.open_!).open_!
    val cssSel =
      "data-yj=username" #> account.username

    cssSel(nodeSeq)
  } openOr Text("模板: single/index 未找到")

  private def message: NodeSeq = Y.template("single/message").map { nodeSeq =>
    nodeSeq
  } openOr Text("模板: single/message 未找到")

  private def temp2: NodeSeq = Y.template("single/temp2").map { nodeSeq =>
    nodeSeq
  } openOr Text("模板: single/temp2 未找到")

  private def edit: NodeSeq = Y.template("single/edit").map { nodeSeq =>
    val cssSel = reqAccount.is match {
      case Failure(msg, _, _) =>
        "*" #> msg
      case Full(record) => // edit account
        "#form-title" #> "编辑" &
          form(record) &
          formSubmit(record)
      case Empty =>
        "*" #> "系统错误"
    }
    SHtml.ajaxForm(cssSel(nodeSeq))
  } openOr Text("模板: single/edit 未找到")

  /**
   * TODO 文件上传的Ajax功能还没实现
   */
  private object reqEmail extends RequestVar[EmailContent](EmailContent.create)
  private object reqUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private def sendEmail: NodeSeq = {
    Y.template("single/send_email").map { nodeSeq =>
      val cssSel = "@hostName" #> SHtml.text("smtp.qq.com", reqEmail.is.hostName = _) &
        "@smtpPort" #> SHtml.text("465", v => reqEmail.is.smtpPort = asInt(v).openOr(0)) &
        "@username" #> SHtml.text("yang.xunjing", reqEmail.is.username = _) &
        "@password" #> SHtml.password("", reqEmail.is.password = _) &
        "@ssl" #> SHtml.checkbox(false, reqEmail.is.ssl = _) &
        "@from" #> SHtml.text("yang.xunjing@qq.com", reqEmail.is.from = _) &
        "@subject" #> SHtml.text("Test", reqEmail.is.subject = _) &
        "@msg" #> SHtml.textarea("Apache commons email", reqEmail.is.msg = _) &
        "@to" #> SHtml.text("yangbajing@gmail.com", v => reqEmail.is.to = v.split(";").toList) &
        "@attachment" #> SHtml.fileUpload(v => reqUpload(Full(v))) // TODO Ajax的文件怎样上专?
      "#form_submit" #> (SHtml.hidden(() => {
        logger.debug("email: " + reqEmail.is)
        logger.debug(reqUpload.is.open_!.toString)
        S notice MailService.send(reqEmail.is)
      }) ++ SHtml.button("发送", () => ()))

      SHtml.ajaxForm(cssSel(nodeSeq))
    } openOr Text("模板: single/send_mail 未找到")
  }

  def delete(nodeSeq: NodeSeq): NodeSeq = {
    val cssSel = "" #> ""

    cssSel(nodeSeq)
  }

  private def form(account: AccountRecord, isReg: Boolean = false): CssSel = {
    "@username" #> (if (isReg) account.username.toForm.open_! else Text(account.username.is)) &
      "@email" #> account.email.toForm.open_! &
      "@age" #> account.age.toForm.open_! &
      "@locale" #> account.locale.toForm.open_! &
      "@timeZone" #> account.timeZone.toForm.open_!
  }

  private def formSubmit(account: AccountRecord, submitName: String = "保存"): CssSel = {
    "#form_submit" #> (SHtml.hidden(() => {
      account.save
      reqAccount(Full(account))
      S.notice("保存成功")
    }) ++ SHtml.button(submitName, () => ()))
  }

  private def findRecord: Box[AccountRecord] = {
    for (
      accountId <- SessionManager.theAccountId ?~ "session不存在";
      record <- AccountRecord.find(accountId) ?~ "用户:%s".format("不存在")
    ) yield record
  }

  private def findReqHref: Box[String] = {
    Empty
  }

  private def ready = JsCmds.Run(
    """
    var hrefHash = window.location.hash.split('=')[1];
    $('#yj-a-' + hrefHash).click();
    """)

  /**
   * (id, hash)
   */
  private def hrefHash(idHashPair: (String, String)*) = JsCmds.Run(idHashPair.map(pair =>
    "if (hrefHash == '%s')  $('#%s').click(); ".format(pair._2, pair._1))
    .mkString("var hrefHash = window.location.hash.split('=')[1]; ", " else ", ""))

}
