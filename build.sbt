organization  := "com.monkeygroover"

name := "cloudant-crdts"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += Resolver.mavenLocal

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.ibm" %% "couchdb-scala" % "0.5.2-SNAPSHOT",
  "com.typesafe" % "config" % "1.2.1"
)

mainClass in Compile := Some("Main")

enablePlugins(JavaAppPackaging)


    
