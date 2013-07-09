import AssemblyKeys._

name := "shinynemesis"

version := "0.2-SNAPSHOT"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers ++= Seq(
    "Xuggle Repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"
)

libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC2"

mainClass := Some("net.benmur.shinynemesis.ShinyNemesis")

assemblySettings

jarName in assembly <<= (name, version) map (_ + "-" + _ + "-fat.jar")

fork in run := true
