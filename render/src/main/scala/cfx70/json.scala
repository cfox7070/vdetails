package cfx70.cfpl.render

import scala.scalajs.js.annotation._
import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{html,document,Event}
import scala.scalajs.js.timers._

import scala.math._

import cfx70.cfpl.core._
import cfx70.cfpl.core.CommonHelpers._
import cfx70.cfpl.core.Helpers3d._
import cfx70.cfpl.core.Helpers2d._

import cfx70.threejsfacade.THREE._
import cfx70.threejsfacade.OrbitControls

import cfx70.vecquat._

class DetParams extends js.Object {
	var dtype : String = _
}
class RedRRParams extends DetParams {
	var a1 : Double = _
}




