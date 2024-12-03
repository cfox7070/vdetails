package cfx70.threejsfacade

import org.scalajs.dom._
import org.scalajs.dom.{ HTMLCanvasElement, HTMLElement, HTMLImageElement }

import scalajs.js
import scalajs.js.annotation._
import scalajs.js.typedarray._

/*<script type="importmap">
{
  "imports": {
			"three": "./path.to.three.module.js"			
  }
}
</script>*/

object THREE {
	
	val DoubleSide = 2
//A
	@js.native
	@JSImport("three", "AmbientLight")
	class AmbientLight(var color : Integer = js.native, var intensity : Double = js.native) extends Light(color,intensity){
		def dispose () :Unit = js.native 
	}		

	@js.native
	@JSImport("three", "AxesHelper")
	class AxesHelper(size: Double = js.native) extends Line
//B	
	@js.native
	@JSImport("three", "Box3")
	class Box3(var min: Vector3 = js.native, var max: Vector3 = js.native) extends js.Object {
		def setFromObject(`object`: Object3D): Box3 = js.native
		def getSize(target: Vector3 ): Vector3 = js.native
		def getBoundingSphere(target: Sphere ): Sphere = js.native
	}

	@js.native
	@JSImport("three", "Box3Helper")
	class Box3Helper(box : Box3 = js.native, color : Color = js.native) extends LineSegments

	@js.native
	@JSImport("three", "BoxGeometry")
	class BoxGeometry(width: Double, height: Double, depth: Double,
						var widthSegments: Integer = js.native, var heightSegments: Integer = js.native, 
							var depthSegments: Integer = js.native) extends BufferGeometry {
	}

	@js.native
	@JSImport("three", "BufferAttribute")
	class BufferAttribute(var array: js.Any, var itemSize: Integer, normalized : Boolean = js.native) extends js.Object

	@js.native
	@JSImport("three", "BufferGeometry")
	class BufferGeometry() extends js.Object {
		var boundingBox: Box3 = js.native
		def setFromPoints(points : js.Array[Vector3]) : BufferGeometry = js.native
		def setAttribute(name: String, attribute: BufferAttribute): BufferGeometry = js.native
		def dispose(): Unit = js.native
		def computeBoundingBox(): Unit = js.native
	}
//C
	@js.native
	@JSImport("three", "Camera")
	class Camera() extends Object3D 
	
	@js.native
	@JSImport("three", "Color")
	class Color(color: Color = js.native) extends js.Object {
	  def this(r: Double, g: Double, b: Double) = this()
	  def this(hex: Double) = this()
	  def this(name: String) = this()
	}
//D
	@js.native
	@JSImport("three", "DirectionalLight")
	class DirectionalLight(var color : Integer = js.native, var intensity : Double = js.native) extends Light(color,intensity){
		var target : Object3D = js.native
		def dispose () :Unit = js.native 
	}
//E	
	@js.native
	@JSImport("three","EdgesGeometry")
	class EdgesGeometry(geometry : BufferGeometry, thresholdAngle : Integer =js.native) extends BufferGeometry

	@js.native
	@JSImport("three", "Euler")
	class Euler(var x : Double,var y : Double,var z : Double,var order : String ) extends js.Object{
	}
	
//L
	@js.native
	@JSImport("three", "Light")
	class Light(color : Integer = js.native, intensity : Double = js.native) extends Object3D 

	@js.native
	@JSImport("three","Line")
	class Line(var geometry: BufferGeometry = js.native, var material:  Material = js.native) extends Object3D 

	@js.native
	@JSImport("three", "LineBasicMaterial")
	class LineBasicMaterial(parameters:  js.Object = js.native) extends Material {
		var color: Color = js.native
	}

	@js.native
	@JSImport("three","LineSegments")
	class LineSegments(geometry: BufferGeometry = js.native, material:  Material = js.native) extends Line(geometry,material) 
	
//M
	@js.native
	@JSImport("three", "Material")
	class Material() extends js.Object {
		def dispose(): Unit = js.native
	}

	@js.native
	@JSImport("three", "Mesh")
	class Mesh( val geometry : BufferGeometry, val material : Material ) extends Object3D 	

	@js.native
	@JSImport("three", "MeshBasicMaterial")
	class MeshBasicMaterial(params: js.Object = js.native) extends Material {
		var color: Color = js.native
	}

	@js.native
	@JSImport("three", "MeshPhongMaterial")
	class MeshPhongMaterial(params: js.Object = js.native) extends Material {
	}
//O
	@js.native
	@JSImport("three", "Object3D")
	class Object3D() extends js.Object {
		var position : Vector3 = js.native
		var rotation : Euler = js.native	
		var children: js.Array[Object3D] = js.native
		def add ( obj : Object3D* ) : Object3D = js.native
		def remove(`object`: Object3D): Unit = js.native
	}
//P
	@js.native
	@JSImport("three", "PerspectiveCamera")
	class PerspectiveCamera(var fov : Double, var aspect : Double, var near : Double, var far : Double) extends Camera {
		def updateProjectionMatrix(): Unit = js.native
	}

	@js.native
	@JSImport("three","Plane")
	class Plane(var normal: Vector3 = js.native, var constant: Double = js.native) extends js.Object {
	  def set(normal: Vector3, constant: Double): Plane = js.native
	  def setComponents(x: Double, y: Double, z: Double, w: Double): Plane = js.native
	  def setFromNormalAndCoplanarPoint(normal: Vector3, point: Vector3): Plane = js.native
	  def setFromCoplanarPoints(a: Vector3, b: Vector3, c: Vector3): Plane = js.native
	  def copy(plane: Plane): Plane = js.native
	  def normalize(): Plane = js.native
	  def negate(): Plane = js.native
	  def distanceToPoint(point: Vector3): Double = js.native
//	  def distanceToSphere(sphere: Sphere): Double = js.native
	  def projectPoint(point: Vector3, optionalTarget: Vector3 = js.native): Vector3 = js.native
	  def orthoPoint(point: Vector3, optionalTarget: Vector3 = js.native): Vector3 = js.native
//	  def isIntersectionLine(line: Line3): Boolean = js.native
//	  def intersectLine(line: Line3, optionalTarget: Vector3 = js.native): Vector3 = js.native
	  def coplanarPoint(optionalTarget: Boolean = js.native): Vector3 = js.native
//	  def applyMatrix4(matrix: Matrix4, optionalNormalMatrix: Matrix3 = js.native): Plane = js.native
	  def translate(offset: Vector3): Plane = js.native
	  def equals(plane: Plane): Boolean = js.native
	  override def clone(): Plane = js.native
	}

	@js.native
	@JSImport("three","Points")
	class Points(var geometry: BufferGeometry = js.native, var material:  Material = js.native) extends Object3D {
	}
		
	@js.native
	@JSImport("three", "PointsMaterial")
	class PointsMaterial(parameters: js.Object = js.native) extends Material{
		var size: Double = js.native
		var color: Color = js.native
	}
//S
	@js.native
	@JSImport("three", "Scene")
	class Scene() extends Object3D {
		var background : Color = js.native		
	}
	@js.native
	@JSImport("three","Sphere")
	class Sphere(var center: Vector3 = js.native, var radius: Double = js.native) extends js.Object 
//V
	@js.native
	@JSImport("three", "Vector3")
	class Vector3(var x : Double = js.native, var y : Double = js.native, var z : Double = js.native ) extends js.Object{
		def set ( x : Double, y : Double, z : Double ): Vector3 = js.native
	}
//W
	@js.native
	@JSImport("three", "WebGLRenderer")
	class WebGLRenderer(params: js.Object = js.native) extends js.Object{
		def domElement : HTMLCanvasElement = js.native
		def render ( scene : Object3D, camera : Camera ) : Unit =  js.native
	}
}
