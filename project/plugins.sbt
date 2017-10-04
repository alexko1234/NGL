// Comment to get more information during initialization
// logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.9")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.0.0")

libraryDependencies += "org.javassist" % "javassist" % "3.20.0-GA"

scalacOptions += "-deprecation"

//javacOptions  ++= Seq("-Xlint:deprecation","-Xlint:unchecked")
// javacOptions += "-verbose"
javacOptions += "-Xlint"

// parallelExecution := false

