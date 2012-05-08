package me.yangbajing
package util

import java.io.InputStream
import java.security.{ Security, MessageDigest }
import org.bouncycastle.jce.provider.BouncyCastleProvider

object DigestUtils {
  Security.addProvider(new BouncyCastleProvider)
  private val BC = "BC"
  private val STREAM_BUFFER_LENGTH = 1024

  def apply(digestName: String) = new MeMessageDigest(MessageDigest.getInstance(digestName, BC))

  def digest(md: MessageDigest, in: InputStream): Array[Byte] = {
    val buffer: Array[Byte] = (0 until 1024).map(_.toByte).toArray
    var rn = in.read(buffer, 0, STREAM_BUFFER_LENGTH)
    while (rn > -1) {
      md.update(buffer, 0, rn)
      rn = in.read(buffer, 0, STREAM_BUFFER_LENGTH)
    }
    md.digest()
  }



  lazy val md5 = apply("MD5")
  lazy val sha1 = apply("SHA1")
  lazy val sha256 = apply("SHA256")
  lazy val sha512 = apply("SHA512")

  class MeMessageDigest(md: MessageDigest) {
    def provider = md.getProvider

    def algorithm = md.getAlgorithm

    def digestLength = md.getDigestLength
    def length = digestLength

    def reset = {
      md.reset
      this
    }

    def digest = md.digest
    def digest(data: Array[Byte]) = md.digest(data)
    def digest(data: Array[Byte], x: Int, y: Int) = md.digest(data, x, y)

    def update(data: Array[Byte], x: Int, y: Int) = {
      md.update(data, x, y)
      this
    }
    def update(buffer: java.nio.ByteBuffer) = {
      md.update(buffer)
      this
    }
    def update(data: Array[Byte]) = {
      md.update(data)
      this
    }
    def update(b: Byte) = {
      md.update(b)
      this
    }
  }

}
