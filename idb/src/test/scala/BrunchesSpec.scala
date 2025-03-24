package cfx70.idbwrap

import scala.scalajs.js.annotation._
import scala.scalajs.js

//import utest._

@JSExportTopLevel(name = "IDBtest", moduleID = "idbtest")
object IDBtest {
	 val db = IndexedDb.open("example",1,Seq(("test","id",true)))
	 println(db)
}

/*object HelloTests extends TestSuite{
  val tests = Tests{
    test("test1"){
      5 ==> 5
    }
    test("test2"){
      8 ==>8
    }
  }
}*/

