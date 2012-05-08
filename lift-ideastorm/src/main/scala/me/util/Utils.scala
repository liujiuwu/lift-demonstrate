package me.yangbajing
package util

import java.security.MessageDigest
import org.bouncycastle.util.encoders.Hex

object Utils {
  implicit def byteArray2String(data: Array[Byte]) = new ByteArray2String(data)
}

class ByteArray2String(data: Array[Byte]) {
  def __String = new String(data)

  def __HexString = new String(Hex.encode(data))
}

