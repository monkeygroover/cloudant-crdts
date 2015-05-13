/**
 * Created by rilakkuma on 29/04/2015.
 */

import com.ibm.couchdb._
import com.typesafe.config.ConfigFactory
import crdts.Vote

import scalaz._

object Main extends App {

  // Define a type mapping used to transform class names into the doc kind
  val typeMapping = TypeMapping(
    classOf[Vote] -> "Vote")

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

  val pollId = "dsfdsfsdf"

  val pollView = CouchView(map =
    """
      |function(doc){
      |   if(doc.doc.pollId){
      |       emit(doc.doc.pollId, doc.doc);
      |   }
      |}
    """.stripMargin)

  val designDoc = CouchDesign(
    name  = "test-design",
    views = Map("poll-view" -> pollView))

  val pollViewQuery = db.query.view[String, Vote]("test-design", "poll-view").get

  val votes = Vote("poll1", 1) :: Vote("poll2", 2) :: Vote("poll1", 3) :: Vote("poll2", 2) :: Vote("poll2", 1) :: Vote("poll1", 7) :: Nil


  val actions = for {
    _ <- cloudant.dbs.delete(dbName).ignoreError
    _ <- cloudant.dbs.create(dbName)
    _ <- db.design.create(designDoc)
    _ <- db.docs.createMany(votes)
    result <- pollViewQuery.queryIncludeDocs[Vote]("poll1" :: Nil)
  } yield result

  // Execute the actions and process the result
  actions.attemptRun match {
    // In case of an error (left side of Either), print it
    case -\/(e) => println(e)
    // In case of a success (right side of Either), print each object
    case \/-(a) => println(a)
    case x => println(x)
  }
}
