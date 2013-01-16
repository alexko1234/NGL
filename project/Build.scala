import sbt._
import Keys._
import PlayProject._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
   
   import BuildSettings._	
   import Resolvers._
   import Dependencies._

   val appName = "ngl"
   val appVersion = "1.0-SNAPSHOT"
   
  override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
  )
	
   
   object BuildSettings {
   
           val buildOrganization = "fr.cea.ig."+appName
           val buildVersion      = appVersion
           
           val buildSettings = Defaults.defaultSettings ++ Seq (
               organization   := buildOrganization,
               version        := buildVersion,
               credentials += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
               publishMavenStyle := true               
           )           
    }
 
   object Resolvers {        
   	import BuildSettings._
        val nexusig = "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/" 
        val nexusigrelease = "releases"  at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/releases"
        val nexusigsnapshot = "snapshots" at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/snapshots"
	val nexusigpublish = if (buildVersion.endsWith("SNAPSHOT")) nexusigsnapshot else nexusigrelease				
    } 
 
   object Dependencies {
   	val nglcommonDependencies = Seq(
		"fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT",
		"play" %% "spring" % "2.0",
		"org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
		"mysql" % "mysql-connector-java" % "5.1.18"
		
    	)	 
   	
   	val nglbiDependencies = Seq(
	        // Add your project dependencies here,
	        "commons-collections" % "commons-collections" % "3.2.1",
	        "org.apache.commons" % "commons-lang3" % "3.1",
	        "play" %% "spring" % "2.0",
	        "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
	        "mysql" % "mysql-connector-java" % "5.1.18",
	        "postgresql" % "postgresql" % "8.3-603.jdbc4",
	        "net.sourceforge.jtds" % "jtds" % "1.2.4",      	
	        "fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
	        "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
	        "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"
	       
    	)
    	
    	val nglsqDependencies = Seq(
	      // Add your project dependencies here,
	      "play" %% "spring" % "2.0",
	      "net.sourceforge.jtds" % "jtds" % "1.2.4",
	      "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
	      "mysql" % "mysql-connector-java" % "5.1.18",
	      "fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
	      "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
	      "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"
    	)
    	
    	val nglauthDependencies = Seq(
	        "fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
		"fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
		"mysql" % "mysql-connector-java" % "5.1.18"
    	)
   }
   
    
 
   val nglcommon = PlayProject(appName + "-common", appVersion, nglcommonDependencies, mainLang = JAVA, path = file("lib-ngl-common"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers += nexusig,
       publishTo := Some(nexusigpublish)       
    )
   
   val nglbi = PlayProject(appName + "-bi", appVersion, nglbiDependencies, mainLang = JAVA, path = file("app-ngl-bi"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers += nexusig,
       publishArtifact := false,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
 
    ).dependsOn(nglcommon)
   
   val nglsq = PlayProject(appName + "-sq", appVersion, nglsqDependencies, mainLang = JAVA, path = file("app-ngl-sq"),settings = buildSettings).settings(
          // Add your own project settings here      
          resolvers += nexusig,
          publishArtifact := false,
          publishArtifact in makePom := false,
          publishTo := Some(nexusigpublish) 
    
    ).dependsOn(nglcommon)
   
   
   val nglauth = PlayProject(appName + "-authorization", appVersion, nglauthDependencies, mainLang = JAVA, path = file("app-ngl-authorization"),settings = buildSettings).settings(
             // Add your own project settings here      
             resolvers += nexusig,
             publishArtifact := false,
             publishArtifact in makePom := false,
             publishTo := Some(nexusigpublish) 
       
   ).dependsOn(nglcommon)
   
   val nglasset = PlayProject(appName + "-asset", appVersion, mainLang = JAVA, path = file("app-ngl-asset"),settings = buildSettings).settings(
                // Add your own project settings here      
                resolvers += nexusig,
                publishArtifact := false,
                publishArtifact in makePom := false,
                publishTo := Some(nexusigpublish) 
          
      )
   
   
   val main = PlayProject(appName, appVersion, settings = buildSettings).settings(
      // Add your own project settings here      
      resolvers += nexusig,
      publishArtifact := false,
      publishArtifact in makePom := false,
      publishTo := Some(nexusigpublish)
    ).aggregate(
     	nglcommon,nglsq,nglbi,nglauth,nglasset
    )

}
