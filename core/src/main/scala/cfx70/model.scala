package cfx70.cfpl.core

import scalajs.js

import math._

import cfx70.threejsfacade.THREE
import cfx70.threejsfacade.THREE._
import cfx70.vecquat._
import CommonHelpers._

trait FromParamsSeq {
	val imgSrc : String
	val imgAlt : String
	def defaultParamsSeq : ParamsSeq
	def apply(ps : ParamsSeq) : Model
    def default : Model = apply(defaultParamsSeq)
}

object Model {

    val phongBlueMaterial = new MeshPhongMaterial($(color=0xc8bfe7/*0x79bff8*/,side=THREE.DoubleSide))

    def edges(geom: BufferGeometry,mcolor :Int=0x000000) : LineSegments = {    
        val mat= new LineBasicMaterial($(color = mcolor))
        val medges = new EdgesGeometry(geom)
        new LineSegments(medges, mat) 
    }
        
    def makeMeshes(mat: Material, bgeoms : BGeometry*) : Object3D = {
        val m =new Object3D
        for (bg <- bgeoms)  m.add(new Mesh(bg.getGeometry1(),mat)) 
        m
    }

}

abstract class Model{
	def whdsize: Vec = new Box3().setFromObject(meshes).getSize(new Vector3()) //width height depth
	
	def bsphere: Sphere = new Box3().setFromObject(meshes).getBoundingSphere(new Sphere())
	
	val meshes:Object3D
	
	def edges(color :Int=0x000000) :Object3D  = {
        val e =new Object3D
        for(m <- meshes.children) m match {
            case mm : Mesh => e.add(Model.edges(mm.geometry,color))
  //        case _ =>
        }
        e
    }
	
	def dispose() : Unit = {
        for(m <- meshes.children) m match {
            case mm : Mesh => mm.geometry.dispose()
  //        case _ =>
        }
	}

    def description(lang:String):String	
    def area : Double = 0.0
    def areaIns(thk : Double):Double = 0.0
    def flangeL (n : Int):Double = 0.0
    def secA (n : Int):Double = 0.0
}
////////////////////////
class RedRR(val a1:Double,val b1:Double,val a2:Double,val b2:Double,
			val h:Double,val da:Double,val db:Double, 
                            val hfb:Double=30.0,val hft:Double=30.0 ) extends Model{
        import Model._                                          
		def description(lang:String):String = 
			s"Переход $a1 &times; $b1 &minus; $a2 &times; $b2 L=$h"
		
       val fcb = new BBox(a1,b1,hfb)
       val fct = new BBox(a2,b2,hft).translate(Vec(da,db,h-hft))
       val cn  = new BPyramid(a1,b1,a2,b2,h-hfb-hft,da,db).translate(Vec(0,0,hfb))
                                                
       val meshes = makeMeshes(phongBlueMaterial,fcb,fct,cn)  
       
       override def area : Double = cn.area + fcb.area + fct.area														
}
object RedRR extends FromParamsSeq{
	val imgSrc = "img/red-r-ra.png"
	val imgAlt = "Развертка перехода с прямоугольника на прямоугольник"
	def defaultParamsSeq : ParamsSeq = Seq(ParamsItem("a1",400),
										 ParamsItem("b1",300),
										 ParamsItem("a2",300),
										 ParamsItem("b2",250),	
										 ParamsItem("h",350),
										 ParamsItem("da",175),
										 ParamsItem("db",75),
										 ParamsItem("df1",30),
										 ParamsItem("df2",30)
										)
	def apply(ps : ParamsSeq) : RedRR = {
		val pm = ps.view.map(p => p.name -> p).toMap
		def V (n : String) : Double = pm(n).v
		new RedRR(V("a1"), V("b1"), V("a2"),V("b2"), V("h"),V("da")-V("a1")/2+V("a2")/2,V("b1")/2 - V("db") - V("b2")/2,V("df1"),V("df2"))
	}	
}
/////////////////////////////
class RedRC(val a1:Double,val b1:Double,val d:Double,
                val h:Double,val da:Double,val db:Double, 
                val df1:Double=30.0,val df2:Double=50) extends Model{

        import Model._                                          
    def description(lang:String):String = s"Переход $a1 &times; $b1 &minus; &Oslash;$d L=$h"

       val fcb = new BBox(a1,b1,df1)
       val fct = new BCylinder(d,df2).translate(Vec(da,db,h-df2))
       val cn  = new BRCRed(a1,b1,d,h-df1-df2,da,db).translate(Vec(0,0,df1))
                                                
       val meshes = makeMeshes(phongBlueMaterial,fcb,fct,cn)                                                             
}
object RedRC extends FromParamsSeq{
	val imgSrc = "img/red-r-c.png"
	val imgAlt = "Развертка перехода с прямоугольника на круг"
	def defaultParamsSeq : ParamsSeq = Seq(ParamsItem("a1",400),
										 ParamsItem("b1",300),
										 ParamsItem("d",250,"5","diams"),
										 ParamsItem("h",350),
										 ParamsItem("da",325),
										 ParamsItem("db",225),
										 ParamsItem("df1",30),
										 ParamsItem("df2",40)
										)
	def apply(ps : ParamsSeq) : RedRC = {
		val pm = ps.view.map(p => p.name -> p).toMap
		def V (n : String) : Double = pm(n).v
		new RedRC(V("a1"), V("b1"), V("d"), V("h"), V("da")-V("a1")/2,V("db")-V("b1")/2,V("df1"),V("df2"))
	}	
}

class RedCC(val d1:Double,val d2:Double,val dc:Double,val h:Double,
						val f1:Double,val f2:Double) extends Model{
        import Model._						
	def description(lang:String):String = 
			s"Переход &Oslash;$d1 &minus; &Oslash;$d2 L=$h"
		       
       val fcb = new BCylinder(d1,f1)
       val fct = new BCylinder(d2,f2).translate(Vec(dc,0,h-f2))
       val cn  = new BCone(d1,d2,h-f1-f2,dc).translate(Vec(0,0,f1))
       						
       val meshes = makeMeshes(phongBlueMaterial,fcb,fct,cn)                                                             
}
object RedCC extends FromParamsSeq{
	val imgSrc = "img/red-c-c.png"
	val imgAlt = "Развертка перехода с круга на круг"
	def defaultParamsSeq : ParamsSeq = Seq(ParamsItem("d1",400,"5","diams"),
										 ParamsItem("d2",250,"5","diams"),
										 ParamsItem("h",350),
										 ParamsItem("dc",50),
										 ParamsItem("df1",40),
										 ParamsItem("df2",40)
										)
	def apply(ps : ParamsSeq) : RedCC = {
		val pm = ps.view.map(p => p.name -> p).toMap
		def V (n : String) : Double = pm(n).v
		new RedCC(V("d1"), V("d2"), V("dc"), V("h"),V("df1"),V("df2"))
	}	
}

class ElbC(val d:Double,val ang:Double,val rad:Double, val segs : Double,
							val f1:Double,val f2:Double) extends Model{
        import Model._						
	def description(lang:String):String = 
			s"Отвод &Oslash;$d &lt;ang Rc=$rad"
		       
       val fcb = new BCylinder(d,f1)
       /*val fct = new BCylinder(d2,f2).translate(Vec(dc,0,h-f2))
       val cn  = new BCone(d1,d2,h-f1-f2,dc).translate(Vec(0,0,f1))*/
       						
       val meshes = makeMeshes(phongBlueMaterial,fcb)                                                             
}
object ElbC extends FromParamsSeq{
	val imgSrc = "img/elb-c.png"
	val imgAlt = "Развертка круглого отвода"
	def defaultParamsSeq : ParamsSeq = Seq(ParamsItem("d",200,"5","diams"),
										 ParamsItem("&lt;",90),
										 ParamsItem("Rc",200,"5"),
										 ParamsItem("segments",4),
										 ParamsItem("df1",40),
										 ParamsItem("df2",40)
										)
	def apply(ps : ParamsSeq) : ElbC = {
		val pm = ps.view.map(p => p.name -> p).toMap
		def V (n : String) : Double = pm(n).v
		new ElbC(V("d"), V("&lt;"), V("Rc"),V("segments"),V("df1"),V("df2"))
	}	
}

class ElbR(val a:Double,val b:Double,
						val r:Double, val ang:Double, val f:Double=0) extends Model{
    import Model._
    def description(lang:String):String = s"Отвод $a &times; $b R=$r &lt;$ang&deg;"

    val ins = new BCylinder(r*2,b,0,ang).translate(Vec(0,0,-b/2))
    val outs = new BCylinder((r+a)*2,b,0,ang).translate(Vec(0,0,-b/2))
    val tdsc = new BDisc(r,r+a,ang).translate(Vec(0,0,-b/2))
    val bdsc = new BDisc(r,r+a,ang).translate(Vec(0,0,b/2))
    
    val meshes = makeMeshes(phongBlueMaterial,ins,outs,tdsc,bdsc)
                                            
}

class ElbRR(val a1:Double,val b1:Double,val a2:Double,val b2:Double,
						val r:Double,val db:Double,val ang:Double, val f:Double) extends Model{

    def description(lang:String):String = if(a1 == a2 && b1 == b2 && db == 0) {
        s"Отвод $a1 &times; $b1 R=$r f=$f &lt;$ang&deg;"
    } else {
        s"Отвод $a1 &times; $b1 &minus; $a2 &times; $b2 R=$r f=$f &lt;$ang&deg;"
    }
	
                val meshes=null
                                                                
             //   def dispose(){}
}

class ElbCC(val d1:Double,val d2:Double,val r:Double,val ang:Double, val f:Double) extends Model{

    def description(lang:String):String = "ElbCC"
        
                val meshes=null
                                                                
           //     def dispose(){}
}

class BrunchCC(val d1:Double,val d2:Double, val h1:Double, val dd1:Double,
                val d3:Double,val d4:Double, val h2:Double, val dd2:Double,
                 val ang:Double,val h3:Double, val df1:Double,val df2:Double, val df3:Double) extends Model{
     import Model._                                          
       def description(lang:String):String = 
                s"Тройник &Oslash;$d1 &minus; &Oslash;$d2 &minus; &Oslash;$d4 &lt;$ang&deg; l=$h1"
       val angr=Pi/180*ang

       val fcb = new BCylinder(d1,df1)
       val fct = new BCylinder(d2,df2).translate(Vec(-dd1,0,h1-df2))       
       val cns  = new BCone(d1,d2,h1-df1-df2,-dd1).translate(Vec(0,0,df1))
       
       val h0=h1-df1-df2;val b0=d1/2-(d2/2-dd1)
       val b=b0*h3/h0
       val dv=Vec(d2/2-dd1+b+d3/2*sin(angr),0,df1+h0-h3-d3/2*cos(angr))
       val cnv  = new BCone(d3,d4,h2-df3,dd2).rotate(angr,Vec(0,1,0)).translate(dv).cut(cns)
       val fcv = new BCylinder(d4,df3).translate(Vec(dd2,0,0)).rotate(angr,Vec(0,1,0)).
                                translate(dv+Vec((h2-df3)*cos(angr),0,(h2-df3)*sin(angr)))

                                                
       val gb = fcb.getGeometry1()
       val gt = fct.getGeometry1()
       val gv = fcv.getGeometry1()
       val gcns = cns.getGeometry1()
       val gcnv = cnv.getGeometry1()
       
       val meshes = {
           val mb=new Mesh(gb,phongBlueMaterial)
           val mt=new Mesh(gt,phongBlueMaterial)
           val mv=new Mesh(gv,phongBlueMaterial)
           val mcns=new Mesh(gcns,phongBlueMaterial)
           val mcnv=new Mesh(gcnv,phongBlueMaterial)
           val m =new Object3D
           m.add(mb);m.add(mt);m.add(mv)
           m.add(mcns);m.add(mcnv)
           m
       }
       
     //  def dispose(){ gb.dispose();gt.dispose();gv.dispose()
     //                  gcns.dispose();gcnv.dispose()}
}

