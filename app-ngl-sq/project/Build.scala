import sbt._
import Keys._
import PlayProject._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {

    val appName         = "NGL"
    val appVersion      = "1.0-SNAPSHOT"

     override def settings = super.settings ++ Seq(
      EclipseKeys.skipParents in ThisBuild := false
      )
	
    val appDependencies = Seq(
      // Add your project dependencies here,
      "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.0.0", 
      "play" %% "spring" % "2.0",
      "net.sourceforge.jtds" % "jtds" % "1.2.4",
      "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
      "mysql" % "mysql-connector-java" % "5.1.18",
      "fr.cea.ig" %% "playtbforms" % "1.0-SNAPSHOT",
      "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
      "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT",
      "fr.cea.ig" %% "ngl-common" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here    
      resolvers += "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/"
    )
}
