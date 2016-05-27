name := "sgbus-streaming"

version := "0.0.1"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
  "org.elasticsearch" %% "elasticsearch-spark" % "2.3.2",
  "com.google.code.gson" % "gson" % "2.6.2",
  "joda-time" % "joda-time" % "2.9.3"

)

test in assembly := {}



assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
      case _ => MergeStrategy.discard
    }
  case "application.conf"                            => MergeStrategy.concat
  case _                                => MergeStrategy.first
}

fork in run := true

parallelExecution in Test := false
