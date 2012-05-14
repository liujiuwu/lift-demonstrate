package learn.web

import scala.xml.{ NodeSeq, Elem, Text, Node, XML }

import net.liftweb.common.{ Box, Empty, Full, Failure }
import net.liftweb.http.js.{ JE, JsCmd }
import net.liftweb.http.{ S, SHtml, LiftRules }

import SHtml.ElemAttr

/**
 * why Y
 * Yangbajing => Y
 *
 * 一些与HTML、WEB相关的工具方法
 */
object Y {

  /**
   * 以"/"开头的文件从网站根目录寻找，否则从"/WEB-INF/templates/"目录下寻找。
   */
  object resource {
    def apply(template: String): Box[NodeSeq] = {
      val name = if (template(0) == '/') template else "/WEB-INF/templates/" + template
      val xml = try {
        LiftRules.doWithResource(name)(XML.load(_)) or
          LiftRules.doWithResource(name + ".xml")(XML.load(_)) or
          LiftRules.doWithResource(name + ".html")(XML.load(_))
      } catch {
        case e =>
          e.printStackTrace
          Empty
      }
      xml ?~ "template: [%s] not exists!".format(name)
    }

    // TODO 考虑将NodeSeq缓存
  }

  /**
   * 强制从/WEB-INF/templates/ 目录下寻找模板
   */
  def template(template: String): Box[NodeSeq] = {
    val name = "/WEB-INF/templates/" + (if (template(0) == '/') template.tail else template)
    resource(name)
  }

  def ajaxA(text: String, func: () => JsCmd, attrs: ElemAttr*): Elem = ajaxA(text, Empty, func, attrs: _*)

  def ajaxA(text: String, href: Box[String], func: () => JsCmd, attrs: ElemAttr*): Elem = ajaxA(Text(text), href, func, attrs: _*)

  def ajaxA(text: NodeSeq, href: Box[String], func: () => JsCmd, attrs: ElemAttr*): Elem = {
    val elem = S.fmapFunc(S.contextFuncBuilder(func))(name => <a onclick={ SHtml.makeAjaxCall(JE.Str(name + "=true")).toJsCmd + "; return false;" }>{ text }</a>)
    val attrsElem = attrs.foldLeft(elem)((elem, attr) => attr(elem))
    href.map(link => ElemAttr.pairToBasic("href" -> link)(attrsElem)) openOr attrsElem
  }

}
