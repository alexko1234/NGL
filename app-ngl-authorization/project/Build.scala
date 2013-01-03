import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "authorisationAdministration"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "fr.cea.ig" %% "playtbforms" % "1.0-SNAPSHOT",
		"fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
		 "fr.cea.ig" %% "ngl-common" % "1.0-SNAPSHOT",
		 "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/"    
    )

}
