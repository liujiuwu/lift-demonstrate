package me.yangbajing
package util

import java.io._

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import me.yangbajing.util.Utils._

class UtilsSpec extends FunSuite with ShouldMatchers {

  test("Try Resource") {
    val ret = tryusing(
      new BufferedWriter(new FileWriter("/tmp/temp1.txt")),
      new BufferedWriter(new FileWriter("/etc/temp2.txt"))) {
        (r1, r2) =>
          r1.write("中华人民共和国")
          r1.newLine
          r2.write("Scala。我最大！")
          r2.newLine
          throw new RuntimeException("老子想弄个异常出来，行否？")
          "正常运行结束。"
      } capture {
        case e: Exception =>
          println(e)
          "当然可以，但你被抓鸟！"
      }

    ret should be("当然可以，但你被抓鸟！")
  }

  test("Implicitly") {
    val opt1: Option[String] = None
    opt1.getOr("XXX") should be("XXX")

    val opt2: Option[Int] = Some(5)
    opt2.tmap(7)(v => v + 5) should be(10)
  }
}

