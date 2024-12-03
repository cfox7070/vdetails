package cfx70.cfpl.core

import scalajs.js
import scalajs.js.typedarray._
import scalajs.js.JSConverters._

import math._

import cfx70.vecquat._

import cfx70.threejsfacade.THREE._

import CommonHelpers._


class Plane3(val n:Vec, val p:Double) extends LineIntersectable{
    
    def distance (pt:Vec)  = n * pt + p
    def coplanar (pt:Vec)  = abs(distance(pt)) < epsilon
    def parallel (l:Line3) = {n * l.v  < epsilon}
    def containe (l:Line3) = parallel(l) && coplanar(l.p1)
    def intersect (l:Line3) : Option[Vec]= if(this parallel l) None else {
        val t = -(p + n*l.p1)/(n*(l.p2-l.p1))
        Some(l point t)
    }
    def intersectXY() : Option[Line3] = n X Vec(0,0,1) match {
        case a if a.norm  < epsilon => None
        case a => { if(n.x > epsilon)
                        Some(new Line3(Vec(-p/n.x,0,0),Vec(-p/n.x+a.x,a.y,a.z)))
                    else if(n.y > epsilon)
                        Some(new Line3(Vec(0,-p/n.y,0),Vec(a.x,a.y-p/n.y,a.z)))
                    else
                        None
        }
    }
    
    override def toString() = s"Plane3 ($n $p)"    
}

object Plane3{
    def apply(p1:Vec, p2:Vec, p3:Vec) : Plane3 ={
        val a=Mat(3)(p1.y, p1.z, 1,
                     p2.y, p2.z, 1,
                     p3.y, p3.z, 1).det
        val b=Mat(3)(p1.z, p1.x, 1,
                     p2.z, p2.x, 1,
                     p3.z, p3.x, 1).det
        val c=Mat(3)(p1.x, p1.y, 1,
                     p2.x, p2.y, 1,
                     p3.x, p3.y, 1).det
       val d=Mat(3)(p1.x, p1.y, p1.z,
                    p2.x, p2.y, p2.z,
                    p3.x, p3.y, p3.z).det
        apply(a,b,c,d)
    }
    
    def apply(a:Double,b:Double,c:Double,d:Double) : Plane3 ={
        val v = Vec(a,b,c)
        val nn=v.normalize()
        val pp= -d/v.mod
        new Plane3(nn,pp)
    }

}

object Line3 {
	def apply(p1:Vec,p2:Vec) = new Line3(p1,p2)
    def point(p1:Vec,p2:Vec,t:Double) : Vec = p1+(p2-p1)*t
}

class Line3(val p1:Vec,val p2:Vec) extends LineIntersectable with WireframeHelper {
        val pts = Seq(p1,p2)
        val v = p2-p1
        
        def point (t:Double) : Vec = p1+v*t
        
        def pointX (x:Double) : Vec = point((x-p1.x)/v.x)
        def pointY (y:Double) : Vec = point((y-p1.y)/v.y)
        
        def intersect (p:Plane3) : Option[Vec] = p.intersect(this)
        def intersect (l2:Line3) : Option[Vec] = {
                val a=p2-p1
                val b=l2.p2-l2.p1
                val c=l2.p1-p1
                val crab=a X b
                if(crab==0 || (c * crab) > epsilon) None
                else {
                    val t=((c X b) * crab)/crab.norm
                    Some(point(t))
                }
        }
        
        def intersectXYCircle(r:Double) : Option[(Vec,Vec)] ={ // circle intersects projection
            val a= -v.y; val b=v.x; val c= -(p1.x*a+p1.y*b)
            val norm = a*a+b*b
            val m=Vec(-a*c/norm,-b*c/norm,0)
            if (c*c > r*r*norm)
                    None
            else if (abs (c*c - r*r*norm) < epsilon) {
                    Some((m,m))
            }
            else {
                    val d = r*r - c*c/norm
                    val mult = sqrt (d / norm)
                    Some((Vec(m.x+b*mult,m.y-a*mult,0),Vec(m.x-b*mult,m.y+a*mult,0)))
            }
        }
        
        def parallel (p:Plane3) = p.parallel(this)
        def contained (p:Plane3) = p.containe(this)
        
        override def toString() = s"Line3 ($p1 $p2)"
}

trait WireframeHelper{

    def pts : Seq[Vec]
    
    def wireframe(color : Double):Line = {
        val gs = new BufferGeometry().setFromPoints(pts.map(V3(_)).toJSArray) 
        val lm = new LineBasicMaterial()
        lm.color=new Color(color)
        new Line( gs, lm )
    }
}

trait LineIntersectable {
    def intersect(l : Line3) : Option[Vec]
}

trait MoveTopBottom {

    var bpts :Seq[Vec]
    var tpts :Seq[Vec]

    def rotate(ang:Double,v:Vec) : this.type= {
        val rot= (p:Vec) => Quat.rotate(p,ang,v)
        bpts=bpts.map(rot(_))
        tpts=tpts.map(rot(_))
        this
    }
    
    def translate(v:Vec) : this.type = {
        val trans = (p:Vec) => p+v
        bpts=bpts.map(trans(_))
        tpts=tpts.map(trans(_)) 
        this
    }
    
    def front=tpts.init
    def back=bpts.init
}

object BPoint{
       def apply(pt:Vec,color : Double=0x888888) =  { val gp = new BufferGeometry().setFromPoints(jsA[Vector3](pt))
                   val pm=new PointsMaterial()
                   pm.color=new Color(color)
                   pm.size=5
                   new Points( gp, pm )
       }
}

object BGeometry {
    val segments=24
    val segAng=2.0*Pi/segments
    
    def rectPts(a:Double, b:Double, center:Vec) : Seq[Vec]=Seq(Vec(center.x+a/2,center.y+b/2,center.z),
                                                                Vec(center.x-a/2,center.y+b/2,center.z),
                                                                Vec(center.x-a/2,center.y-b/2,center.z),
                                                                Vec(center.x+a/2,center.y-b/2,center.z),
                                                                Vec(center.x+a/2,center.y+b/2,center.z))
                                        
   /* def roundPts(r:Double, center:Vec,startAng:Double=0, endAng:Double = 2*Pi) : Seq[Vec]= 
        for(i <- 0 to segments if ((segAng*i) >= startAng && (segAng*i) <= endAng)) yield Vec(center.x+r*cos(segAng*i),center.y+r*sin(segAng*i),center.z)*/
        
    def roundPts(r:Double, center:Vec,startAng:Double=0, endAng:Double = 2*Pi) : Seq[Vec]= {
        val dA=(endAng-startAng)/segments
        for(i <- 0 to segments ) yield 
            Vec(center.x+r*cos(startAng+dA*i),center.y+r*sin(startAng+dA*i),center.z)
    }

        def shell(b:Seq[Vec], t:Seq[Vec]) : Seq[Vec] = {
        if(b.length != t.length) throw new Exception("Seqs incompartible")
        (for(i <- 0 until b.length-1) yield Seq(b(i),b(i+1),t(i),b(i+1),t(i+1),t(i))).flatten
    }
    
    def shellNormals(b:Seq[Vec], t:Seq[Vec]) : Seq[Vec] = {
        if(b.length != t.length) throw new Exception("Seqs incompartible")
        def NN(v1 : Vec,v2 : Vec) : Vec = (v1 X v2).normalize()
        (for(i <- 0 until b.length-1) yield Seq(NN( b(i+1)-b(i)   , t(i)-b(i)     ),
                                                NN( t(i+1)-b(i+1) , b(i)-b(i+1)   ),
                                                NN( b(i)-t(i)     , t(i+1)-t(i)   ),
                                                NN( t(i+1)-b(i+1) , b(i)-b(i+1)   ),
                                                NN( t(i)-t(i+1)   , b(i+1)-t(i+1) ),
                                                NN( b(i)-t(i)     , t(i+1)-t(i)   ))).flatten
    }
    def fan(b:Seq[Vec], t:Seq[Vec]) : Seq[Vec] = {
        if(((t.length-1) % (b.length-1)) != 0) throw new Exception("Seqs incompartible")
        val tp = (t.length-1) / (b.length-1)
        (for(i <- 0 until b.length -1) yield {
            val s = for(j <- i*tp until (i+1)*tp) yield  Seq(b(i),t(j+1),t(j))
            s.flatten ++ Seq(b(i),b(i+1),t(i*tp+tp))
        }).flatten
    }
    def fanNormals(b:Seq[Vec], t:Seq[Vec]) : Seq[Vec] = {
        if(((t.length-1) % (b.length-1)) != 0) throw new Exception("Seqs incompartible")
        def NN(v1 : Vec,v2 : Vec) : Vec = (v1 X v2).normalize()
        val tp = (t.length-1) / (b.length-1)
        (for(i <- 0 until b.length -1) yield {
            val s = for(j <- i*tp until (i+1)*tp) yield Seq( NN( t(j+1)-b(i) , t(j)-b(i) ),
                                                             NN( t(j)-t(j+1) , b(i)-t(j+1) ),
                                                             NN( b(i)-t(j)   , t(j+1)-t(j) ))
            s.flatten ++ Seq( NN( b(i+1)-b(i)       , t(i*tp+tp)-b(i) ),
                              NN( t(i*tp+tp)-b(i+1) , b(i)-b(i+1)     ),
                              NN( b(i)-t(i*tp+tp)   , b(i+1)-t(i*tp+tp)))
        }).flatten
    }
}

abstract class BGeometry {
    
    def rotate(ang:Double,v:Vec) : this.type
    def translate(v:Vec) : this.type
    
    def pts : Seq[Vec]
    def normals : Seq[Vec]
   
    def getGeometry() : BufferGeometry = new BufferGeometry().setFromPoints(pts.map(V3(_)).toJSArray) 

    def getGeometry1() : BufferGeometry = { 
           val geometry = new BufferGeometry()
           val posarr= Float32Array from pts.flatMap((p) => Float32Array from Seq(p.x.toFloat,p.y.toFloat,p.z.toFloat).toJSIterable).toJSIterable
           val normarr= Float32Array from normals.flatMap( (p) => Float32Array from Seq(p.x.toFloat,p.y.toFloat,p.z.toFloat).toJSIterable ).toJSIterable
          if(posarr.size != normarr.size) 
                throw new Exception(s"position size (${posarr.size}) and normals size (${normarr.size}) not equal")
            geometry.setAttribute("position",new BufferAttribute(posarr, 3))
            geometry.setAttribute("normal",new BufferAttribute(normarr,3))
            geometry
     }
}

class BBox(a:Double,b:Double,h:Double) extends BPyramid(a,b,a,b,h) {
}

class BRCRed(val a1:Double,val b1:Double,
              val d:Double,
              val h:Double,
               val da:Double =0,val db:Double =0) extends BGeometry 
                                     with LineIntersectable with WireframeHelper with MoveTopBottom{ 
                                     
    import BGeometry._
    var bpts = rectPts(a1,b1,Vec(0,0,0))
    var tpts = roundPts(d/2,Vec(da,db,h))

    def pts = fan(bpts,tpts)
    def normals = fanNormals(bpts,tpts)
    
    def intersect(l:Line3) : Option[Vec] = None  
    
    def tophalf=Seq(bpts(3),tpts(0),tpts(segments/2-1),bpts(2))
    
    class Half( val pl : Vec,
				val pr : Vec,
				val pll : Vec,
				val prr : Vec,
				val pc : Vec,
				val left : Seq[Vec],
				val right : Seq[Vec])
				    
    def halfA : Half = new Half( pl = bpts(0),
								  pr = bpts(1),
								  pll = Line3.point(bpts(0),bpts(3),0.5),
								  prr = Line3.point(bpts(1),bpts(2),0.5),
								  pc = tpts(segments/4),
								  left = tpts.slice(0,segments/4+1).reverse,
								  right = tpts.slice(segments/4,segments/2+1) )
				  
    def halfB : Half = new Half( pl = bpts(2),
								  pr = bpts(3),
								  pll = Line3.point(bpts(2),bpts(1),0.5),
								  prr = Line3.point(bpts(3),bpts(4),0.5),
								  pc = tpts(segments*3/4),
								  left = tpts.slice(segments/2,segments*3/4+1).reverse,
								  right = tpts.slice(segments*3/4,segments+1) )
}
                                     
class BPyramid(val a1:Double,val b1:Double,
              val a2:Double,val b2:Double,
              val h:Double,
               val da:Double =0,val db:Double =0) extends BGeometry 
                                     with LineIntersectable with WireframeHelper with MoveTopBottom{               
    import BGeometry._
    var bpts = rectPts(a1,b1,Vec(0,0,0))
    var tpts = rectPts(a2,b2,Vec(da,db,h))

    def pts = shell(bpts,tpts)
    def normals = shellNormals(bpts,tpts)
    
    def intersect(l:Line3) : Option[Vec] = None 

    def bottom=Seq(bpts(3),tpts(3),tpts(2),bpts(2))
    def top=Seq(bpts(1),tpts(1),tpts(0),bpts(0))
    def right=Seq(bpts(0),tpts(0),tpts(3),bpts(3))
    def left=Seq(bpts(2),tpts(2),tpts(1),bpts(1))
    
	def hside(pts : Seq[Vec]) : Double = {
		val(v1,v2) = (pts(1)-pts(0),pts(3)-pts(0))
		if(v1*v2 < epsilon)
			v1.mod
		else{
			val vp = v1 project v2
			(v1 - vp).mod
		}
	}
	
	def aside(pts : Seq[Vec]) : Double = ((pts(3)-pts(0)).mod+(pts(2)-pts(1)).mod)/2*hside(pts)
    def area : Double = aside(top)+aside(bottom)+aside(left)+aside(right)
}

class BCylinder(val d:Double, h:Double, dd:Double = 0, ang:Double = 2.0*Pi) extends 
                            BCone(d,d,h,dd,ang) {
                            
    override def apex : Vec = { throw new Exception("Cylinder hasn't apex"); null }
    
    override def intersect(l:Line3) : Option[Vec] = {                                                        
       l.intersectXYCircle(d1/2) match {
            case Some((pp1,pp2)) =>{ 
                        val pb= if((l.p1-pp1).norm < (l.p1-pp2).norm) pp1 else pp2
                        val cs=pb.x/(d1/2);val sn=pb.y/(d1/2)
                        val pt=Vec(dd+d2/2*cs,d2/2*sn,h)
                        val ll=new Line3(pb,pt)
                        (l intersect ll) match {
                            case Some(lp) => Some(lp)
                            case None => None
                        }
                   }
            case None => None
        }
    }
}

class BDisc(val r1:Double, val r2:Double,val ang:Double = 2.0*Pi) extends BGeometry 
                                       with LineIntersectable with WireframeHelper 
                                 with MoveTopBottom {   
     import BGeometry._
     var tpts = roundPts(r1,Vec(0,0,0),-ang/2,ang/2)
     var bpts = roundPts(r2,Vec(0,0,0),-ang/2,ang/2)

     def pts = shell(tpts,bpts)
     def normals = shellNormals(tpts,bpts)
     
     def intersect(l:Line3) : Option[Vec] = None
     
}       

class BCone(val d1:Double,val d2:Double,val h:Double,val dd:Double = 0,val ang:Double = 2*Pi) extends BGeometry 
                                                    with LineIntersectable with WireframeHelper
                                                     with MoveTopBottom {
    import BGeometry._
    
    var bpts = roundPts(d1/2,Vec(0,0,0),-ang/2,ang/2)
    var tpts = roundPts(d2/2,Vec(dd,0,h),-ang/2,ang/2)

    def pts = shell(bpts,tpts)
    def normals = shellNormals(bpts,tpts)

    
    def cut(c : LineIntersectable) : this.type = {
        val npts= for(i <- 0 until tpts.length) yield {
            c.intersect(new Line3(tpts(i),bpts(i))) match {
                case Some(p) => p
                case None => bpts(i)
            }
        }
        bpts=npts
        this
    }
        
    def apex : Vec = {
        val l1=new Line3(Vec(-d1/2,0,0),Vec(dd-d2/2,0,h))
        val l2=new Line3(Vec(d1/2,0,0),Vec(dd+d2/2,0,h))
        (l1 intersect l2).get
    }
    
    def intersect(l:Line3) : Option[Vec] = {                                                        
        val p=Plane3(l.p1,l.p2,apex)
        val lxy=p.intersectXY().get
        lxy.intersectXYCircle(d1/2) match {
            case Some((pp1,pp2)) =>{ 
                        val pb= if((l.p1-pp1).norm < (l.p1-pp2).norm) pp1 else pp2
                        val cs=pb.x/(d1/2);val sn=pb.y/(d1/2)
                        val pt=Vec(dd+d2/2*cs,d2/2*sn,h)
                        val ll=new Line3(pb,pt)
                        (l intersect ll) match {
                            case Some(lp) => Some(lp)
                            case None => None
                        }
                   }
            case None => None
        }
    }
   
   def tophalf=Seq(bpts(0),bpts(segments/2),tpts(segments/2),tpts(0))
//    override def top=Seq(bpts(3),tpts(3),tpts(2),bpts(2)).map(_.xz)
}

abstract class BPShape extends BGeometry {
    def setup : Unit = {
        println("setup")
    }
}

