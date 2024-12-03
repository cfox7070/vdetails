package cfx70
 
 package object vecquat{
	val epsilon=1.0e-7
}

package vecquat{

import scala.language.postfixOps

import scala.math._


class Vec(val crds:Vector[Double]){

    def this(ncrds:Double*) = { this(ncrds.toVector) }
    
    override def toString()=crds.map((a:Double) => f"$a%.2f").mkString("V(",",",")")

    override def equals(that: Any): Boolean =    //change
        that match {
            case that: Vec =>this.hashCode==that.hashCode
            case _ => false
     }
	 
    override def hashCode: Int = crds.toSeq.hashCode+"Vec".hashCode //change
    
	def isEqual(that:Vec):Boolean={ if(this.crds.length != that.crds.length){
									false
								}else{
									var res=true
									for(i <- 0 until crds.length){
									    val(c,c1)=(crds(i),that.crds(i))
										res=(res && (signum(c)==signum(c1) && abs(c-c1)<epsilon))
										}
								  res
								}
							 }
							 
    def apply(i:Int):Double=crds(i)
    
    def norm : Double ={var n=0d;for(x<-crds) n+=x*x; if(abs(n)<epsilon) 0d else n} 
    
    def mod : Double = sqrt(norm);

    def normalize():Vec={val m=if(mod!=0) mod else 1;new Vec( crds map { (x:Double)=>{val c=x/m; if(abs(c)<epsilon) 0 else c} } )}
    
    def dotProd(v2:Vec) : Double={
		if(crds.length!=v2.crds.length) throw new Exception("Dot product: vectors are different sizes") 
		(for(i<-0 until crds.length) yield crds(i)*v2(i)).sum // different methods and timing
	}
    def *(v2:Vec) : Double=dotProd(v2)
    def *(f:Double) : Vec=new Vec( (for(i<-0 until crds.length) yield crds(i)*f): _* )       
    
    def mulNumb(nmb:Double) : Vec= new Vec( crds map { (x:Double)=>x*nmb } )
    
    def x=crds(0)
    def y=crds(1)
    def z=crds(2)
    def w=crds(3)
    def xy=Vec(crds(0),crds(1))
    def xz=Vec(crds(0),crds(2))
    def yz=Vec(crds(1),crds(2))
    def xyz=Vec(crds(0),crds(1),crds(2))

    def crossProd(v2:Vec) : Vec=new Vec(y*v2.z-v2.y*z,z*v2.x-v2.z*x,x*v2.y-v2.x*y)
    def X(v2:Vec) : Vec=crossProd(v2)

    def +(v2:Vec) : Vec=new Vec( (for(i<-0 until crds.length) yield crds(i)+v2(i)): _* )       
    def -(v2:Vec) : Vec=new Vec( (for(i<-0 until crds.length) yield crds(i)-v2(i)): _* )       
    def unary_- : Vec=this * (-1) 
   
    def project(v2:Vec): Vec={val v2u=v2.normalize(); val a=this*v2u;; v2u*a}
	
    def ang(v2:Vec): Double=acos(dotProd(v2)/(mod*v2.mod))
    
    def ang3(v2:Vec): (Double,Vec)={
                        val cp=crossProd(v2)
                        val ang=acos(dotProd(v2)/(mod*v2.mod))
                        if(cp.mod==0){
                             val pp=Vec(y,-x,0)
                            (ang,pp.normalize())
                        }else{
                            (ang,cp.normalize())
                        }
                    }
     def dist(v2:Vec) : Double = (v2 - this).mod
     def <->(v2:Vec) = dist(v2)
    
    def toHmg:Vec = { crds.length match {
			case 4 => this
			case 3 =>Vec(crds(0),crds(1),crds(2),1)
			case 2 =>Vec(crds(0),crds(1),0,1)
			case _ =>throw new Exception("Vec: can't convert to homogenius")
		}
   }
   
   def toQuat : Quat = new Quat(0,this)
}

object Vec{

    def apply(crds:Double*)=new Vec(crds: _*)  
   // def apply(crds:Double*)=new Vec(crds.map(_): _*)  
    def apply(crds:Vector[Double])=new Vec(crds) 
	def zero = new Vec(0,0,0)
    
}

class Mat(val a:Vector[Vector[Double]]) { //test package

    def this(c:Int)(v:Double*)=this(v.toVector.sliding(c,c).toVector) //if not 4 (?)
	
    override def toString()=(a.map(_.mkString("\t"))).mkString("\nMat(","\n    ",")")

    override def equals(that: Any): Boolean =
        that match {
            case that: Mat => this.hashCode == that.hashCode
            case _ => false
     }
	 
    override def hashCode: Int = a.map(_.toSeq.hashCode).toSeq.hashCode+"Mat".hashCode
    
    def apply(i:Int,j:Int):Double=a(i)(j)
    
    def *(v:Vec) :Vec= Vec((for(i<-0 until a.length) yield Vec(a(i)) dotProd v).toVector)
    
    def *(m:Mat) :Mat= {
	   if(a.length!=m.a(0).length) throw new Exception("Matrix multiplication: matrixes not compatible") 
	   var rs=(for(k <- 0 until m.a(0).length) yield {
			   (for(i <- 0 until m.a(0).length) yield {
				  (for(j<- 0 until a(i).length) yield a(k)(j)*m.a(j)(i)).sum
				}).toVector}).toVector
		
		new Mat(rs)
	}

    def translate(tx:Double, ty:Double, tz:Double)= Mat.translation(tx,ty,tz) * this
	def xRotate(angR:Double) = Mat.xRotation(angR) * this
	def yRotate(angR:Double) = Mat.yRotation(angR) * this
	def zRotate(angR:Double) = Mat.zRotation(angR) * this
	def scale( sx:Double, sy:Double, sz:Double) = Mat.scaling(sx, sy, sz) * this
	
	def det :Double = a.length match {
                    case n if n != a(0).length => throw new Exception("Matrix not sqware")
                    case 0 => throw new Exception("Empty matrix")
                    case 1 => a(0)(0)
                    case 2 => a(0)(0)*a(1)(1)-a(0)(1)*a(1)(0)
                    case 3 => a(0)(0)*a(1)(1)*a(2)(2)+a(0)(1)*a(1)(2)*a(2)(0)+a(0)(2)*a(1)(0)*a(2)(1)-
                                a(0)(2)*a(1)(1)*a(2)(0)-a(0)(1)*a(1)(0)*a(2)(2)-a(0)(0)*a(1)(2)*a(2)(1)
                    case n => { var d=0.0
                                var s= -1
                                for(k <- 0 until n){                                       
                                    val dm= (Vector() ++ {for (i <- 1 until n) yield 
                                                         for(j <- 0 until n if j !=k ) yield a(i)(j)}).flatten
                                     s *= -1
                                     d += s * a(0)(k) * Mat(n-1)(dm: _*).det
                                 }
                                 d
                              }
 	}

	def toVector:Vector[Double]=a flatMap {(v:Vector[Double])=>v}
}

object Mat {
    def apply(cols:Int)(v:Double*) : Mat=new Mat(v.toVector.sliding(cols,cols).toVector) 

	def perspective(fieldOfViewInRadians:Double, aspect:Double, near:Double, far:Double)={
		val f = tan(Pi * 0.5 - 0.5 * fieldOfViewInRadians)
		var nfInv = 1.0 / (near - far)
		apply(4)( f / aspect,     0,        0,                  0,
					  0,          f,        0,                  0,
					  0,          0,  (near + far) * nfInv,     -1,
					  0,          0,   near * far * nfInv * 2,   0)
	}
	def translation(tx:Double, ty:Double, tz:Double)={
		apply(4)(
		   1,  0,  0,  0,
		   0,  1,  0,  0,
		   0,  0,  1,  0,
		   tx, ty, tz, 1)
    }
    def xRotation(angR:Double)={
		val c = cos(angR)
		val s = (angR)
		apply(4)(
		  1, 0, 0, 0,
		  0, c, s, 0,
		  0, -s, c, 0,
		  0, 0, 0, 1)
    }
    def yRotation(angR:Double)={
		val c = cos(angR)
		val s = sin(angR)
		apply(4)(
		  c, 0, -s, 0,
		  0, 1, 0, 0,
		  s, 0, c, 0,
		  0, 0, 0, 1)
    }
	def zRotation(angR:Double)={
		val c = cos(angR)
		val s = sin(angR)
		apply(4)(
		   c, s, 0, 0,
		  -s, c, 0, 0,
		   0, 0, 1, 0,
		   0, 0, 0, 1)
    }
	def scaling(sx:Double, sy:Double, sz:Double)={
		apply(4)(
		  sx, 0,  0,  0,
		  0, sy,  0,  0,
		  0,  0, sz,  0,
		  0,  0,  0,  1)
    }
        def cramer (a : Mat, b : Vec) : Option[Vec] =  a.a.length match {
                    case n if n != a.a(0).length => throw new Exception("Matrix not sqware")
                    case 0 => throw new Exception("Empty matrix")
                    case n if n != b.crds.length => throw new Exception("Matrix and vector not same size")
                    case n =>{
                        val da=a.det
                        val vx = Vector() ++ (for(k <- 0 until n) yield {
                            val mx=new Mat(Vector() ++ {for(i <- 0 until n) yield {
                                    Vector() ++ {for(j <- 0 until n) yield {
                                        if(j==k) b(i) else a.a(i)(j)
                                    }}}
                                })
                            mx.det/da
                         })
                         Some(new Vec(vx))
                    }            
        }
}

class Quat(val s:Double,val v:Vec){
  //exception if not 3?  
    override def toString()="s: "+s+" v: "+v

    override def equals(that: Any): Boolean =    //change
        that match {
            case that: Quat => this.hashCode == that.hashCode
            case _ => false
     }
         
    override def hashCode: Int = s.hashCode+v.hashCode+"Quat".hashCode //change

	def isEqual(that:Quat):Boolean={(signum(s)==signum(that.s)) && (abs(s-that.s)<epsilon) && (v isEqual that.v)}
    def +(q2:Quat) : Quat=new Quat(s+q2.s,v+q2.v )       
    def -(q2:Quat) : Quat=new Quat(s-q2.s,v-q2.v )       
    def *(q2:Quat) : Quat=new Quat(s * q2.s-v * q2.v, q2.v*s +v*q2.s+(v X q2.v))       
    def *(v2:Vec) : Quat =  *(v2.toQuat)      
    def & : Quat=new Quat(s, v * -1)  
    def unary_- : Quat=new Quat(-s, v * -1)  
    
    def norm : Double=sqrt(s*s+v.norm) toFloat
    def normalize() : Quat={val n=norm;new Quat(s/n,v * (1/n))}
    def inverse() : Quat={val nn=norm*norm;new Quat(s/nn,v * (-1/nn))}
    def dotProduct(q2:Quat) : Double=s*q2.s+v*q2.v
    def cosAng(q2:Quat) : Double=dotProduct(q2)/(norm*q2.norm)
	
	def rotate(v2:Vec):Vec=(this * v2 * inverse()).toVec
	
	def decompST(twistAxis:Vec):(Quat,Quat)={
		var twist:Quat=Quat.identity
		var swing:Quat=Quat.identity
	   if (v.norm < epsilon){
			def rotatedTwistAxis = (this * twistAxis).toVec
            def swingAxis = twistAxis X rotatedTwistAxis
         if (swingAxis.norm > epsilon){
           val swingAngle = twistAxis ang rotatedTwistAxis
           swing = Quat.rot(swingAngle, swingAxis);
         } else { // more singularity: rotation axis parallel to twist axis
           swing = Quat.identity; // no swing
         }
         twist = Quat.rot(Pi,twistAxis)  // always twist 180 degree on singularity
      } else {
		  val p = v project twistAxis
		  twist = (Quat(s,p)).normalize()
		  swing = this * twist.inverse()
	  }
	  (swing,twist)
    }
    
    def toVec = new Vec(v.x,v.y,v.z)
    def ang=(acos(s)*2)

}

object Quat{
    def apply(s:Double,v:Vec)=new Quat(s,v)
    def apply(v:Vec)=new Quat(0,v)
    def rot(ang:Double,v:Vec)=new Quat(cos(ang/2),v.normalize() * sin(ang/2)) 
    def rotate (vr:Vec,ang:Double,v:Vec):Vec={
        val qr=vr.toQuat
        val q=rot(ang,v)
        val qr1=q*qr
        val qr2=qr1*q.inverse()
        qr2.toVec
    }
	def identity=Quat(1,Vec(0,0,0))
}
}//package
