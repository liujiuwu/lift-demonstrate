package learn.model

import java.io._

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import net.liftweb.json.JsonDSL._

import me.yangbajing.KV
import me.yangbajing.util.Utils._

class InfomationSpec extends FunSuite with ShouldMatchers {
  test("Insert Record") {
    val info = Infomation()

    info.setAuthors("111111111", "bbbbbbbbbbbbbb", "444444444444")
    info.setMain("关于生产质量的若干决议", KV("副标题", "kkkk"), KV("内容", "laksjdfla;skd fj9q8ew7 fsjdbh al;jkg -q347fd "))
    info.addGroup("附件1", JsonDataLine("附件1，标题", KV("内容", "slajf892 lsdkjf lsakdfj q89e dkj kjd l;sakjf 98 ldskj ")))

    info.save

    val infomation = info.immutable
    println(infomation)
  }
}
