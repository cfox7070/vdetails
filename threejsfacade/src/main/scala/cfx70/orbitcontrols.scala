package cfx70.threejsfacade

import org.scalajs.dom._
import org.scalajs.dom.{ HTMLCanvasElement, HTMLElement, HTMLImageElement }

import scalajs.js
import scalajs.js.annotation._
import scalajs.js.typedarray._

import cfx70.threejsfacade.THREE._

/*<script type="importmap">
{
  "imports": {
			"three/addons/": "path/to/addons"			
  }
}
</script>*/

  @js.native
  @JSImport("three/addons/OrbitControls.js","OrbitControls")
   class OrbitControls(obj : Camera, domElement : html.Element) extends js.Object{
       var autoRotate : Boolean =js.native
       var autoRotateSpeed : Float =js.native
       def addEventListener(evt:String,listener: js.Function1[Event, _]):Unit=js.native
       def update():Unit = js.native
   }
   
