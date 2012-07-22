package learn.snippet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel
import net.liftweb.http.{ RequestVar, SHtml }

class HelloWorld {
  private object name extends RequestVar[String]("")
  private object age extends RequestVar[String]("")

  def time = "*" #> new java.util.Date().toString

  def render(html: NodeSeq): NodeSeq = {
    val cs =
      "@name" #> SHtml.text(name.is, name(_), "placeholder" -> "在此输入姓名") &
        ".age" #> SHtml.text(age.is, parm => age(parm), "placeholder" -> "在此输入年龄") &
        "type=submit" #> SHtml.submit("提交", () => {
          println("name: " + name.is)
          println("age: " + age.is)
        })

    cs(html)
  }
}

// vim: set ts=2 sw=2 et:
