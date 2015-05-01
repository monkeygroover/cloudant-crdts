organization  := "com.monkeygroover"

name := "cloudant-crdts"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.ibm" %% "couchdb-scala" % "0.5.0",
  "com.typesafe" % "config" % "1.2.1"
)

mainClass in Compile := Some("Main")

enablePlugins(JavaAppPackaging)


    
