import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ngl-bi"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.0.0", 
      "play" %% "spring" % "2.0",
      "net.sourceforge.jtds" % "jtds" % "1.2.4",
      "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
      "mysql" % "mysql-connector-java" % "5.1.18",
      "postgresql" % "postgresql" % "8.3-603.jdbc4",
      "net.sourceforge.jtds" % "jtds" % "1.2.4",      	
      "fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
      "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
      "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT",
      "fr.cea.ig.ngl" %% "ngl-common" % "1.0-SNAPSHOT",
      "commons-collections" % "commons-collections" % "3.2.1",
      "org.apache.commons" % "commons-lang3" % "3.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
      resolvers += "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/"
    )

}
