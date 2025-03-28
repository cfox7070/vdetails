package cfx70.cfpl.render

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.timers._
import scala.scalajs.js.annotation._

@JSExportTopLevel(name = "DParams", moduleID = "vdetails")
object DetParams {
	@JSExport
	def fromJSON (json :String) : DetParams = JSON.parse(json).asInstanceOf[DetParams]
	
	@JSExport
	def tt() : Unit = {
		val dp = fromJSON(s"""{"dtype":"ductR", "a":400, "b":300}""")
		println(dp)
		val sp = dp.toJSON()
		println(sp)
	}	
}

@JSExportTopLevel(name = "DetParams", moduleID = "vdetails")
abstract class DetParams extends js.Object {
	var dtype : String = _
	def toJSON() : String = JSON.stringify(this)
}

object DuctRParams{	
	val paramsDraft = ""
	val defaultJson = s"""{"dtype":"ductR", "a":400, "b":300}"""
}

class DuctRParams extends DetParams {
	var a : Double = _
	var b : Double = _		
	var l : Double = _		
	
	//def toJSON () : String = JSON.stringify(this)	
}





