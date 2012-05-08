package markdown.markwrap

import java.io.File

import org.scalatest.FunSuite
import org.clapper.markwrap._

class MarkWrapSpec extends FunSuite {
  test("MarkWrap.praserFor") {
    val fData = List(
      (MarkupType.Markdown, "foo.md"),
      (MarkupType.Markdown, "foo.markdown"),
      (MarkupType.Textile, "foo.textile"),
      (MarkupType.XHTML, "foo.xhtml"),
      (MarkupType.XHTML, "foo.xhtm"),
      (MarkupType.XHTML, "foo.htm"),
      (MarkupType.XHTML, "foo.html"),
      (MarkupType.PlainText, "foo.text"),
      (MarkupType.PlainText, "foo.txt"),
      (MarkupType.PlainText, "foo.properties"),
      (MarkupType.PlainText, "foo.cfg"),
      (MarkupType.PlainText, "foo.conf"))

    val mtData = List(
      (MarkupType.Markdown, "text/markdown"),
      (MarkupType.Textile, "text/textile"),
      (MarkupType.XHTML, "text/html"),
      (MarkupType.XHTML, "text/xhtml"),
      (MarkupType.PlainText, "text/plain"))

    val typeData = List(
      MarkupType.Markdown,
      MarkupType.Textile,
      MarkupType.XHTML,
      MarkupType.XHTML,
      MarkupType.PlainText)

    println(MarkWrap.parserFor(new File("foo.conf")).markupType)

    for ((expected, fn) <- fData) {
      expect(expected, "MarkWrap.parserFor(new File(\"" + fn + "\"))") {
        MarkWrap.parserFor(new File(fn)).markupType
      }
    }

    for ((expected, mimeType) <- mtData) {
      expect(expected, "MarkWrap.parserFor(\"" + mimeType + "\")") {
        MarkWrap.parserFor(mimeType).markupType
      }
    }

    for (parserType <- typeData) {
      expect(parserType, "MarkWrap.parserFor(" + parserType + ")") {
        MarkWrap.parserFor(parserType).markupType
      }
    }
  }
}
