package me.yangbajing
package log

abstract class Level(val code: Int)

object Level {
  case object ERROR extends Level(100)
  case object WARN extends Level(200)
  case object SUCCESS extends Level(300)
  case object INFO extends Level(400)
  case object DEBUG extends Level(500)
  case object TRACE extends Level(600)
}
