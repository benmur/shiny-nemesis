import AssemblyKeys._

name := "frameprint"

version := "0.1"

resolvers ++= Seq(
    "Xuggle Repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"
)

libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.2"

mainClass := Some("net.benmur.frameprint.FramePrint")

assemblySettings

jarName in assembly <<= (name, version) ((n, v) => "%s-%s-fat.jar".format(n, v))

fork in run := true
