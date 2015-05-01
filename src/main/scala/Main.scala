/**
 * Created by rilakkuma on 29/04/2015.
 */

import com.ibm.couchdb._
import com.typesafe.config.ConfigFactory

import scalaz._

object Main extends App {

  // Define a simple case class to represent our data model
  case class Person(name: String, age: Int)

  // Define some sample data
  val alice = Person("Alice", 25)
  val bob   = Person("Bob", 30)
  val carl  = Person("Carl", 20)

  // Define a type mapping used to transform class names into the doc kind
  val typeMapping = TypeMapping(classOf[Person] -> "Person")

  val config = ConfigFactory.load()
  val host = config.getString("cloudant.host")
  val port = config.getInt("cloudant.port")
  val https = config.getBoolean("cloudant.https")
  val username = config.getString("cloudant.username")
  val password = config.getString("cloudant.password")

  val cloudant = CouchDb(
    host=host,
    port=port,
    https=https,
    username=username,
    password=password
  )

  // Define a database name
  val dbName = "test-db"
  // Get an instance of the DB API by name and type mapping
  val db  = cloudant.db(dbName, typeMapping)

  val actions = for {
  // Delete the database or ignore the error if it doesn't exist
    _ <- cloudant.dbs.delete(dbName).ignoreError
    // Create a new database
    _ <- cloudant.dbs.create(dbName)
    // Insert documents into the database
    _ <- db.docs.createMany(Seq(alice, bob, carl))
    // Retrieve all documents from the database and unserialize to Person
    docs <- db.docs.getMany.queryIncludeDocs[Person]
  } yield docs.getDocsData

  // Execute the actions and process the result
  actions.attemptRun match {
    // In case of an error (left side of Either), print it
    case -\/(e) => println(e)
    // In case of a success (right side of Either), print each object
    case \/-(a) => a.map(println(_))
  }

}
