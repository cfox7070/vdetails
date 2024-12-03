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

@JSExportTopLevel(name = "VDetails", moduleID = "vdetails")
object VDetails{
	
	def fromJSON(json : String) : Model = ???
}


@JSExportTopLevel(name = "Det3dView", moduleID = "vdetails")	
object Det3dView {
	def apply(canvasID : String) : Det3dView = new Det3dView(canvasID)
}

class Det3dView (val canvasID : String /*light, alight, background*/) { 
	
    lazy val renderer : WebGLRenderer = {  val cnv=document.querySelector(canvasID).asInstanceOf[html.Canvas]
										println(cnv)
                          new WebGLRenderer($(canvas = cnv ))}

    val camera3d : PerspectiveCamera = new PerspectiveCamera(75, 800.0/600.0, 0.1, 2000)
    var dlight : DirectionalLight = new DirectionalLight(0xffffff, 0.6)
	var alight = new AmbientLight(0xffffff, 0.5)
    val scene : Scene = { val s = new Scene()
						  s.background=new Color(0xF3F3FA)
						  s.add(dlight)
                          s.add(alight)
                          s}
                          
    lazy val controls : OrbitControls = { val c =new OrbitControls( camera3d, renderer.domElement )
                                         c.addEventListener("change",(e:dom.Event)=>{    
                                                            dlight.position.set(camera3d.position.x, camera3d.position.y, camera3d.position.z)
                                                            renderer.render(scene,camera3d) 
                                                        })
                                      c}

    var model:Option[Model]=None
    var animation:Boolean=false
    
    @JSExport
    def setBackGround(color : Int) : Unit = {
		scene.background=new Color(color)
		updateScene()
	}
    @JSExport
    def setDirectionalLight(color:Int, intensity:Double) :Unit = {
		scene.remove(dlight)
		dlight.dispose()
		dlight = new DirectionalLight(color, intensity)
		scene.add(dlight)
		model.foreach(mm => {dlight.target = mm.meshes})
		updateScene()
	}
	@JSExport       
    def setAmbientLight(color:Int, intensity:Double) :Unit = {
		scene.remove(alight)
		alight.dispose()
		alight = new AmbientLight(color, intensity)
		scene.add(alight)
		updateScene()
	}
	@JSExport
    def setAnimation(a:Boolean) : Unit ={ animation=a;animate(0) }

	@JSExport
    def setModel(m:Model) : Unit = {
            model.foreach(mm => {scene.remove(mm.meshes); mm.dispose()})
            model=Some(m)                   		   
 		   show3d()		   
    }
    
	def updateScene() : Unit ={renderer.render(scene, camera3d)}
	
	def show3d():Unit=model match {
		case Some(m) =>{
                        val s=m.bsphere
                        camera3d.position.z= (s.radius /** 2*/)/tan((camera3d.fov/2).toRadians)
                        camera3d.position.x= -s.radius
                        camera3d.position.y= s.radius
                        camera3d.rotation.x= -Pi/6
                        camera3d.updateProjectionMatrix()

                        scene.add(model.get.meshes) //? m.meshes
						controls.update()
						dlight.position.set(camera3d.position.x, camera3d.position.y, camera3d.position.z)
						animate(0)
		}
		case None => {}
	}
		    
     def animate(time:Double) : Unit = {     
        if(animation){ //println(s"animation $animation")
                        dom.window.requestAnimationFrame(animate)}
        controls.update()
        dlight.position.set(camera3d.position.x, camera3d.position.y, camera3d.position.z)
        renderer.render(scene, camera3d)
    }
  
  	def dataURL() :String = {
	  updateScene()
	  renderer.domElement.toDataURL("image/png")
	}
	
}




