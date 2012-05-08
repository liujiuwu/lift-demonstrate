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

object AccountSnippet {
  private object reqAccount extends RequestVar[Box[AccountRecord]](findRecord)
  private object reqHref extends RequestVar[Box[String]](findReqHref)

  def render: CssSel = {
    S.appendJs(hrefHash("yj-a-temp1" -> "temp1", "yj-a-temp2" -> "temp2", "yj-a-edit" -> "edit", "yj-a-send_email" -> "send_email"))

    "#yj-sidebar *" #> sidebar &
      "#yj-container-main *" #> reqHref.is.dmap(index) {
        case "temp1" =>
          temp1
        case "temp2" =>
          temp2
        case "edit" =>
          edit
        case "send_mail" =>
          sendEmail
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

    Y.template("account/sidebar").map { nodeSeq =>
      val links = sidebarLi("yj-a-temp1", "Temp1", "href=temp1", temp1) ++
        sidebarLi("yj-a-temp2", "Temp2", "href=temp2", temp2) ++
        sidebarLi("yj-a-edit", "编辑", "href=edit", edit) ++
        sidebarLi("yj-a-send_email", "发送邮件", "href=send_email", sendEmail)

      (".nav *" #> links)(nodeSeq)
    } openOr Text("模板: account/sidebar 未找到")
  }

  private def index: NodeSeq = Y.template("account/index").map { nodeSeq =>
    val cssSel =
      "data-yj=username" #> SessionManager.theAccount.open_!.username

    cssSel(nodeSeq)
  } openOr Text("模板: account/index 未找到")

  private def temp1: NodeSeq = Y.template("account/temp1").map { nodeSeq =>
    nodeSeq
  } openOr Text("模板: account/temp1 未找到")

  private def temp2: NodeSeq = Y.template("account/temp2").map { nodeSeq =>
    nodeSeq
  } openOr Text("模板: account/temp2 未找到")

  private def edit: NodeSeq = Y.template("account/edit").map { nodeSeq =>
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
  } openOr Text("模板: account/edit 未找到")


  /**
   * TODO 文件上传的Ajax功能还没实现
   */
  private object reqEmail extends RequestVar[EmailContent](EmailContent.create)
  private object reqUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private def sendEmail: NodeSeq = {
    Y.template("account/send_email").map { nodeSeq =>
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
        println("email: " + reqEmail.is)
	println(reqUpload.is.open_!.toString)
        S notice MailService.send(reqEmail.is)
      }) ++ SHtml.button("发送", () => ()))

      SHtml.ajaxForm(cssSel(nodeSeq))
    } openOr Text("模板: account/send_mail 未找到")
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
      username <- SessionManager.theAccount.is.map(_.username) ?~ "session不存在";
      record <- AccountRecord.find("username" -> username) ?~ "用户:%s".format("不存在")
    ) yield record
  }

  private def findReqHref: Box[String] = {
    Empty
  }

  /**
   * private def ready = JsCmds.Run(
   * """
   * var hrefHash = window.location.hash.split('=')[1];
   * if (hrefHash === 'temp1')
   * $('#yj-a-temp1').click();
   * else if(hrefHash === 'temp2')
   * $('#yj-a-temp2').click();
   * else if (hrefHash === 'edit')
   * $('#yj-a-edit').click();
   * else if (hrefHash === 'send_mail')
   * $('#yj-a-send_mail).click();
   * """)
   */

  /**
   * (id, hash)
   */
  private def hrefHash(idHashPair: (String, String)*) = JsCmds.Run(idHashPair.map(pair =>
    "if (hrefHash === '%s')  $('#%s').click(); ".format(pair._2, pair._1))
    .mkString("var hrefHash = window.location.hash.split('=')[1]; ", " else ", ""))

}
