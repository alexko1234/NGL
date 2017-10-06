// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"


// dependency tool
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// Use the Play sbt plugin for Play projects
// addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.9")
// addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.0.0")

// bytecode enhancement has been pulled out of core play 
addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

// test
// addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "1.0.0")





libraryDependencies += "org.javassist" % "javassist" % "3.20.0-GA"

scalacOptions += "-deprecation"

//javacOptions  ++= Seq("-Xlint:deprecation","-Xlint:unchecked")
// javacOptions += "-verbose"
javacOptions += "-Xlint"

// parallelExecution := false

