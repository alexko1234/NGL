import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ngl-common"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
	"fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      	organization := "fr.cea.ig.ngl",
	credentials += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
      	publishMavenStyle := true,
      	resolvers += "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/",
	publishTo <<= version { (v: String) =>  				
				if (v.trim.endsWith("SNAPSHOT")) 
				    Some("snapshots" at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/snapshots") 
				else
				    Some("releases"  at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/releases")
	}     
    )

}
