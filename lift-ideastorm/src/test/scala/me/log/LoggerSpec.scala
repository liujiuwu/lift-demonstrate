package me.yangbajing
package log

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import me.yangbajing.util.Utils._

class LoggerSpec extends FunSuite with ShouldMatchers {
  test("LOgger test ....") {
    new Test

    new Demo
  }

  class Test extends Loggable {
    logger.info("开始记录日志了。。。。")
  }

  class Demo extends Loggable {
    override protected implicit val implicitKey = "我重写了默认key"

    logger.warn(new MakeLog)
  }

  class MakeLog {
    override def toString = "Make log success...."
  }

}
