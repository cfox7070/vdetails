package cfx70.cfpl.core

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, html, window,document,Event}
import scala.language.implicitConversions

import cfx70.vecquat.Vec
import cfx70.threejsfacade.THREE._
import cfx70.threejsfacade.OrbitControls

object Side extends Enumeration {
	type Side = Value
	val TOP, BOTTOM, LEFT, RIGHT, CENTER, MIDDLE = Value
}

object Ordinal extends Enumeration {
	type Ordinal = Value
	val FIRST, SECOND, THIRD, FORTH = Value
}

object CommonHelpers {
	
	case class ParamsItem(name : String, v : Double, step : String = "50", list : String = "")
	type ParamsSeq = Seq[ParamsItem]

	
	implicit def vec2vec3(pt: Vec) : Vector3 = new Vector3(pt.x,pt.y,pt.z)
	//implicit def vec2vec2(pt: Vec)=new Vector2(pt.x,pt.y)
	implicit def vec3Vec(v: Vector3) : Vec = Vec(v.x,v.y,v.z)
	implicit def plane3threePlane(p: Plane3) : Plane = new Plane(p.n,p.p)
	implicit def tuple22vec(tp: (Double,Double) ) : Vec = Vec(tp._1, tp._2)

	val $=js.Dynamic.literal
	def jsA[T](v1:T*)=js.Array[T](v1: _*)
	
	def V3(x:Double,y:Double,z:Double=0)=new Vector3(x,y,z)
    def V3(v : Vec)= vec2vec3(v)
//    def V2(x:Double,y:Double)=new Vector2(x,y)

	def maxN (a1:Double,a2:Double,ax:Double*):Double={
		var mx=math.max(a1,a2)
		for(x<-ax) mx=math.max(mx,x)
		mx
	}
    def minN (a1:Double,a2:Double,ax:Double*):Double={
		var mn=math.min(a1,a2)
		for(x<-ax) mn=math.min(mn,x)
		mn
	}

    def addListener(evt:String,listener: js.Function1[Event, _],els:dom.Element*)=
        for(el <- els) el.addEventListener(evt,listener,false)
    
    def dimsChange(e:dom.Event) : Unit = {
        println("smth changed")
    }

	def s(v: Vector3)=s"Vector3: ${v.x},${v.y},${v.z}"
	/*def printmt(m:Matrix4,comment:String=""){
		println(comment)
		val e=m.elements
		println(f""" ${e(0)}%.2f\t  ${e(4)}%.2f\t  ${e(8)}%.2f\t  ${e(12)}%.2f\t
					|${e(1)}%.2f\t  ${e(5)}%.2f\t  ${e(9)}%.2f\t  ${e(13)}%.2f\t
					|${e(2)}%.2f\t  ${e(6)}%.2f\t  ${e(10)}%.2f\t  ${e(14)}%.2f\t
					|${e(3)}%.2f\t  ${e(7)}%.2f\t  ${e(11)}%.2f\t  ${e(15)}%.2f\t
				 """.stripMargin)
	}*/

}

object Helpers2d{
	type Context2d = CanvasRenderingContext2D
	
	implicit def context2rich(ctx: CanvasRenderingContext2D) : RichContext.type = RichContext
	implicit def rich2context(rctx: RichContext.type) : CanvasRenderingContext2D = RichContext.ctx

	@js.native
	trait MTextMetrics extends js.Object {
		val actualBoundingBoxLeft: Double = js.native
		val actualBoundingBoxRight: Double = js.native
		val actualBoundingBoxAscent: Double = js.native
		val actualBoundingBoxDescent: Double = js.native
	}
		
	object RichContext{
       var lscale=1.0
	   var ctx : CanvasRenderingContext2D = null
	   var curPt=Vec(0,0)
		def polygon(st:Vec,pts:Vec *) : RichContext.type={
			M(st)
			for (pt <- pts) L(pt)
			ctx.closePath()
			this
		}
		def polygon(pts:Seq[Vec]) : RichContext.type = polygon(pts.head,pts.tail: _*)
		def M(x:Double,y:Double)={ctx.moveTo(x*lscale,y*lscale);curPt=Vec(x*lscale,y*lscale);this}
		def M(pt:Vec):RichContext.type=M(pt.x,pt.y)
		def L(x:Double,y:Double)={ctx.lineTo(x*lscale,y*lscale);curPt=Vec(x*lscale,y*lscale);this}
		def L(pt:Vec):RichContext.type=L(pt.x,pt.y)
		def H(x:Double)=L(x,curPt.y)
		def V(y:Double)=L(curPt.x,y)
		def line (p1:Vec,p2:Vec):RichContext.type = {M(p1);L(p2)}

		def translateS(x:Double, y:Double) = ctx.translate(x*lscale, y*lscale)
		
		def measureText(txt :String) = ctx.measureText(txt).asInstanceOf[MTextMetrics]
		def textMetrics(txt :String) = { 
			val tm = measureText(txt)
			(tm.actualBoundingBoxLeft, tm.actualBoundingBoxRight,
						tm.actualBoundingBoxAscent,tm.actualBoundingBoxDescent)			
		}
		
		def drawAx() : Unit = {
		   ctx.save()
		   ctx.beginPath()
		   ctx.strokeStyle = "#ff0000"
		   ctx.lineWidth = 6.0
		   ctx.M(0,0) L(0,100) M(0,0) L(100,0) 
		   ctx.stroke()
		   ctx.restore()
	    }
	    
	    @js.native
	    trait DOMMatrix extends js.Object {
			val a : Double = js.native
			val b : Double = js.native
			val c : Double = js.native
			val d : Double = js.native
			val e : Double = js.native
			val f : Double = js.native
		}
		
		import scala.scalajs.js.annotation.JSName

		@js.native
		trait MoreContext extends js.Object {
			def getTransform() : DOMMatrix = js.native
		}
	    
	    import Side._
	    def text(tx:String, pt:Vec, halign:Side.Side = LEFT, valign:Side.Side = BOTTOM, ang:Double = 0) : Unit = {
			val t = (ctx.asInstanceOf[RichContext.MoreContext]).getTransform()
			val sx = if(t.a < 0) -1 else 1
			val sy = if(t.d < 0) -1 else 1
			ctx.save()
			ctx.textBaseline = valign match {case BOTTOM =>"bottom"; case MIDDLE =>"middle"; case TOP => "top"}
			ctx.textAlign = halign match {case LEFT =>"left"; case CENTER =>"center"; case RIGHT => "right"}
			ctx.translateS(pt.x, pt.y )
			ctx.rotate(ang)
			ctx.scale(sx,sy)	
			ctx.beginPath()
			ctx.fillStyle = "#fff"
			val(l,r,a,d) = ctx.textMetrics(tx)
			ctx.fillRect(-(l+1),-(a+1),(r+l+2),(a+d+2) )
			ctx.beginPath()
			ctx.fillStyle = "#000"
			ctx.fillText(tx,0,0)
			ctx.restore()
		}
	}
}

object Helpers3d{	
    import CommonHelpers.$
    
    def set3dRenderer(canvasId : String):(WebGLRenderer,Scene,PerspectiveCamera,
												DirectionalLight,OrbitControls) ={
        val renderer = {  val cnvDev=document.querySelector(canvasId).asInstanceOf[html.Canvas]
                          new WebGLRenderer($(canvas = cnvDev ))}
                          
        val scene = new Scene()
        scene.background=new Color(0xF3F3FA)
        
        val camera3d = new PerspectiveCamera(75, 800.0/600.0, 0.1, 2000)
        val light = new DirectionalLight(0xffffff, 0.6)
		val alight = new AmbientLight(0xffffff, 0.5)
        scene.add(light)
        scene.add(alight)
    
        val controls = new OrbitControls( camera3d, renderer.domElement )
        controls.addEventListener("change",(e:dom.Event)=>{    
                                                            light.position.set(camera3d.position.x, camera3d.position.y, camera3d.position.z)
                                                            renderer.render(scene,camera3d) 
                                                        })
        (renderer,scene,camera3d,light,controls)
    }
}







