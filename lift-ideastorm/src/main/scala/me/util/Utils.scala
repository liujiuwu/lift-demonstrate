package me.yangbajing
package util

import java.security.MessageDigest
import java.util.Date
import java.text.SimpleDateFormat
import org.bouncycastle.util.encoders.Hex

object Utils extends TryUsingResources with Implicitly {

  val dateIsoWeak = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E")
  val dateIso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
}

sealed trait Implicitly {
  implicit def byteArray2String(data: Array[Byte]) = new ByteArray2String(data)

  implicit def option2OptionRich[T](option: Option[T]) = new OptionRich(option)
}

sealed class Date2String(date: Date) {
  def toISOWeak = Utils.dateIsoWeak.format(date)
}

sealed class ByteArray2String(data: Array[Byte]) {
  def __String = new String(data)

  def __HexString = new String(Hex.encode(data))
}

sealed class OptionRich[T](option: Option[T]) {

  def tmap[B](ret: => B)(f: T => B): B = {
    if (option.isDefined)
      f(option.get)
    else
      ret
  }

  def getOr[B >: T](deft: => B): B = option.getOrElse(deft)

}

/**
 * 仿Java 7 try-catch-sources 和 C# using 方法的自动资源管理
 */
sealed trait TryUsingResources {
  def optionNull[A](f: => A): Option[A] = {
    val a = f
    if (a == null) None
    else Some(a)
  }

  def tryout[R](f: => R): Option[R] = try {
    Some(f)
  } catch {
    case e: Exception => None
  }

  type TClose = { def close(): Unit }

  def closeQuietly[A <: TClose](params: A*) {
    for (p <- params if p != null) {
      try {
        p.close
      } catch {
        case _ =>
      }
    }
  }

  def closeQuietlyOption[A <: TClose](params: Option[A]*) {
    for (
      po <- params;
      p <- po if p != null
    ) {
      try {
        p.close
      } catch {
        case _ =>
      }
    }
  }

  def closeException[A <: TClose](params: A*)(fe: PartialFunction[Throwable, Unit]): Unit = params.foreach(p =>
    try {
      if (p != null) p.close
    } catch {
      fe
    })

  def using[A <: TClose, R](a: => A)(f: A => R): R = try {
    f(a)
  } finally {
    closeQuietly(a)
  }

  def using[A <: TClose, B <: TClose, R](a: => A, b: => B)(f: (A, B) => R): R = try {
    f(a, b)
  } finally {
    closeQuietly(b, a)
  }

  def using[A <: TClose, B <: TClose, C <: TClose, R](a: => A, b: => B, c: => C)(f: (A, B, C) => R): R = try {
    f(a, b, c)
  } finally {
    closeQuietly(c, b, a)
  }

  def using[A <: TClose, B <: TClose, C <: TClose, D <: TClose, R](a: => A, b: => B, c: => C, d: => D)(f: (A, B, C, D) => R): R = try {
    f(a, b, c, d)
  } finally {
    closeQuietly(d, c, b, a)
  }

  def using[A <: TClose, B <: TClose, C <: TClose, D <: TClose, E <: TClose, R](a: => A, b: => B, c: => C, d: => D, e: => E)(f: (A, B, C, D, E) => R): R = try {
    f(a, b, c, d, e)
  } finally {
    closeQuietly(e, d, c, b, a)
  }

  /**
   * 类似Java 7新加try-with-resources功能。
   * tryusing-capture
   *
   * （注：最多支持5个参数，多于5个的话应该考虑重构代码了。）
   */
  object tryusing {
    def apply[A <: TClose, R](a: => A)(f: A => R): WithCapture1[A, R] =
      new WithCapture1(a, f)

    def apply[A <: TClose, B <: TClose, R](a: => A, b: => B)(f: (A, B) => R): WithCapture2[A, B, R] =
      new WithCapture2(a, b, f)

    def apply[A <: TClose, B <: TClose, C <: TClose, R](a: => A, b: => B, c: => C)(f: (A, B, C) => R): WithCapture3[A, B, C, R] =
      new WithCapture3(a, b, c, f)

    def apply[A <: TClose, B <: TClose, C <: TClose, D <: TClose, R](a: => A, b: => B, c: => C, d: => D)(f: (A, B, C, D) => R): WithCapture4[A, B, C, D, R] =
      new WithCapture4(a, b, c, d, f)

    def apply[A <: TClose, B <: TClose, C <: TClose, D <: TClose, E <: TClose, R](a: => A, b: => B, c: => C, d: => D, e: => E)(f: (A, B, C, D, E) => R): WithCapture5[A, B, C, D, E, R] =
      new WithCapture5(a, b, c, d, e, f)

    class WithCapture1[A <: TClose, R](a: => A, f: A => R) {
      def capture(fe: PartialFunction[Throwable, R]): R = {
        var ax: Option[A] = None
        try {
          ax = Some(a)
          f(ax.get)
        } catch {
          fe
        } finally {
          closeQuietlyOption(ax)
        }
      }
    }

    class WithCapture2[A <: TClose, B <: TClose, R](a: => A, b: => B, f: (A, B) => R) {
      def capture(fe: PartialFunction[Throwable, R]): R = {
        var ax: Option[A] = None
        var bx: Option[B] = None
        try {
          ax = Some(a)
          bx = Some(b)
          f(ax.get, bx.get)
        } catch {
          fe
        } finally {
          closeQuietlyOption(bx, ax)
        }
      }
    }

    class WithCapture3[A <: TClose, B <: TClose, C <: TClose, R](a: => A, b: => B, c: => C, f: (A, B, C) => R) {
      def capture(fe: PartialFunction[Throwable, R]): R = {
        var ax: Option[A] = None
        var bx: Option[B] = None
        var cx: Option[C] = None
        try {
          ax = Some(a)
          bx = Some(b)
          cx = Some(c)
          f(ax.get, bx.get, cx.get)
        } catch {
          fe
        } finally {
          closeQuietlyOption(cx, bx, ax)
        }
      }
    }

    class WithCapture4[A <: TClose, B <: TClose, C <: TClose, D <: TClose, R](a: => A, b: => B, c: => C, d: => D, f: (A, B, C, D) => R) {
      def capture(fe: PartialFunction[Throwable, R]): R = {
        var ax: Option[A] = None
        var bx: Option[B] = None
        var cx: Option[C] = None
        var dx: Option[D] = None
        try {
          ax = Some(a)
          bx = Some(b)
          cx = Some(c)
          dx = Some(d)
          f(ax.get, bx.get, cx.get, dx.get)
        } catch {
          fe
        } finally {
          closeQuietlyOption(dx, cx, bx, ax)
        }
      }
    }

    class WithCapture5[A <: TClose, B <: TClose, C <: TClose, D <: TClose, E <: TClose, R](a: => A, b: => B, c: => C, d: => D, e: => E, f: (A, B, C, D, E) => R) {
      def capture(fe: PartialFunction[Throwable, R]): R = {
        var ax: Option[A] = None
        var bx: Option[B] = None
        var cx: Option[C] = None
        var dx: Option[D] = None
        var ex: Option[E] = None
        try {
          ax = Some(a)
          bx = Some(b)
          cx = Some(c)
          dx = Some(d)
          ex = Some(e)
          f(ax.get, bx.get, cx.get, dx.get, ex.get)
        } catch {
          fe
        } finally {
          closeQuietlyOption(ex, dx, cx, bx, ax)
        }
      }
    }
  }
}
