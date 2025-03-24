package cfx70.cfpl.render

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.timers._

object DetParams {
	
	def fromJson (json :String) : DetParams = JSON.parse(json).asInstanceOf[DetParams]
		
}

abstract class DetParams extends js.Object {
	var dtype : String = _
	def toJSON : String
}

object DuctRParams{	
	val paramsDraft = ""
	val defaultJson = s"""{"dtype":"ductR", "a":400, "b":300}"""
}

class DuctRParams extends DetParams {
	var a : Double = _
	var b : Double = _		
	var l : Double = _		
	
	def toJSON : String = JSON.stringify(this)	
}

object mApp {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    val mob = DetParams.fromJson(DuctRParams.defaultJson)
    println(mob.dtype)
    val s= mob.toJSON
    println(s)
  }
}



