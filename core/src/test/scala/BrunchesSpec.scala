package cfx70.redrr

import org.scalatest._

//import utest._

import cfx70.vecquat._
import scala.math._


class DetTest extends FunSuite with DiagrammedAssertions {

   test("det 2x2"){
        val m=Mat(2)(3,6,2,8)
        assert(m.det == 12)
     }
   test("det 3x3"){
        val m=Mat(3)(3,6,1,2,8,1,2,1,3)
        assert(m.det == 31)
        assert(Mat(3)(8,1,2,
                      3,2,1,
                      4,1,3).det== 25)
        assert(Mat(3)(3,6,1,
                      3,2,1,
                      4,1,3).det== -20)
        assert(Mat(3)(3,6,1,
                      8,1,2,
                      4,1,3).det== -89)
        assert(Mat(3)(3,6,1,
                      8,1,2,
                      3,2,1).det== -8)
     }

   test("det 4x4"){
        val m=Mat(4)(3,6,1,2,
                     8,1,2,1,
                     3,2,1,1,
                     4,1,3,1)
        assert(m.det == 11)
     }
}

class PlaneTest extends FunSuite with DiagrammedAssertions {
    
    val pl=Plane3(Vec(0,0,0),Vec(1,0,0),Vec(0,1,0))
    
    test("normal is (0,0,1), distance is 0"){
        assert(pl.n == Vec(0,0,1))
        assert(pl.p == 0)
    }
    
    test(" distance to (0,0,2) is 2") {
        assert(pl.distance(Vec(0,0,2))==2)
    }
    
    test("(1,1,0) is coplanar") {
        assert(pl.coplanar(Vec(1,1,0)))
    }
    
    val pl1=Plane3(Vec(0,0,1),Vec(1,0,1),Vec(0,1,1))
    
    test("normal is (0,0,1), distance is -1"){
        assert(pl1.n == Vec(0,0,1))
        assert(pl1.p == -1)
    }
    
    test(" distance to (0,0,2) is 1") {
        assert(pl1.distance(Vec(0,0,2))==1)
    }
    
    test("(1,1,1) is coplanar") {
        assert(pl1.coplanar(Vec(1,1,1)))
    }

    val pl2=Plane3(Vec(1,0,0),Vec(0,1,0),Vec(0,0,1))
    val cs=1/sqrt(3)
    
    test("normal is (cs,cs,cs), distance is -cs"){
        assert(pl2.n == Vec(cs,cs,cs))
        assert(pl2.p == -cs)
    }
    
    test(" distance to (1,1,1) is sqrt(3)-cs") {
        assert(abs(pl2.distance(Vec(1,1,1)) - (sqrt(3)-cs))<epsilon)
    }
    
    test("distance n*p ") {
        assert(abs(pl2.distance(pl2.n * (-pl2.p)))< epsilon)
    }
    test("n*p is coplanar") {
        assert(pl2.coplanar(pl2.n *(- pl2.p)))
    }
    
    val l=new Line3(Vec(1,0,0),Vec(0,1,0))
    
    test("l parallel pl2"){
     assert(pl2 parallel l)
    }

    test("pl2 containe l"){
     assert(pl2 containe l)
    }

    val l1=new Line3(Vec(2,0,0),Vec(0,2,0))
    test("l1 parallel pl2"){
     assert(pl2 parallel l1)
    }

    test("pl2 not containe l1"){
     assert(!(pl2 containe l1))
    }
    
    val l2=new Line3(Vec(0,0,0),Vec(1,1,1))
    test("l2 not parallel pl2"){
     assert(!(pl2 parallel l2))
    }

    test("intersection l p"){
        val l = new Line3(Vec(-2,3,-1),Vec(-2+1,3+3,-1+2))
        val p = Plane3(0,2,-1,11)
        val i=p intersect l
        assert(true)
    }
    
    test("cramer"){
        val x = (Mat.cramer(Mat(3)(3, -2, 4,
                          3,  4,-2,
                          2, -1,-1),Vec(21,9,10))).get
        assert(x.x == 5 && x.y == -1 && x.z == 1)
    }

}


