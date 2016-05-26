resolvers ++=
 Seq( "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
   "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.0")