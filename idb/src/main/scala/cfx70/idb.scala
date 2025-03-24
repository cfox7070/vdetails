package cfx70

import scala.scalajs.js
import org.scalajs.dom.{ErrorEvent, Event, idb, IDBCreateObjectStoreOptions, IDBVersionChangeEvent}
//import org.scalajs.dom.raw.IDBVersionChangeEvent

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

package object idbwrap{
	val $=js.Dynamic.literal
}

package idbwrap {
	
object IndexedDb {
  
  private def factory = js.Dynamic.global.indexedDB.asInstanceOf[idb.Factory]
  
  private def targetType[T] (event : Event) = event.target.asInstanceOf[js.Dynamic].result.asInstanceOf[T]

  def open(databaseName: String, version: Option[Int] = Some(1))
			(upgradeHandler: (idb.Database, idb.Transaction) => Unit): Future[idb.Database] = {
    val promise = Promise[idb.Database]()
    val request = version match {
      case Some(v) => factory.open(databaseName, v)
      case None => factory.open(databaseName)
    }
    request.onupgradeneeded = {(event: IDBVersionChangeEvent) =>
      val db = targetType[idb.Database](event) //event.target.asInstanceOf[js.Dynamic].result.asInstanceOf[idb.Database]
      val transaction = event.currentTarget.asInstanceOf[js.Dynamic].transaction.asInstanceOf[idb.Transaction]
      upgradeHandler(db, transaction)
    }
    request.onblocked = {(event: Event) => }
    request.onsuccess = {(event: Event) =>
      val db = event.target.asInstanceOf[js.Dynamic].result.asInstanceOf[idb.Database]
      promise.success(db)
    }
    request.onerror = {(event: ErrorEvent) =>
      promise.failure(new Exception(event.message))
    }
    promise.future
  }

  def delete(databaseName: String) = factory.deleteDatabase(databaseName)
  
  def open(name :String, version : Int, stores :Seq[(String, String,Boolean)]) : Future[idb.Database] ={
	  val uhandler = (db : idb.Database, t : idb.Transaction) => {  
	    println(s"upgrading database")
	    for((sname,skey,sInc) <- stores){
			if (!db.objectStoreNames.contains(sname))
				db.createObjectStore(sname, $(keyPath = "name", autoIncrement = true).asInstanceOf[IDBCreateObjectStoreOptions])
		}
	  }
	  open(name,Some(version))(uhandler)
  }	
}

}
