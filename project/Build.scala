import sbt._
import Keys._
import play.Project._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
   
   import BuildSettings._	
   import Resolvers._
   import Dependencies._

   val appName = "ngl"
   val appVersion = "1.0-SNAPSHOT"
   val libDatatableVersion = "1.0-SNAPSHOT"
   val libFrameworkWebVersion = "1.0-SNAPSHOT"
   
   
  override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
  )
	
   
   object BuildSettings {
   
           val buildOrganization = "fr.cea.ig"
           val buildVersion      = appVersion
           
           val buildSettings = Defaults.defaultSettings ++ Seq (
               organization   := buildOrganization+"."+appName,
               version        := buildVersion,
               credentials += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
               publishMavenStyle := true               
           )  
           
           val buildSettingsLib = Defaults.defaultSettings ++ Seq (
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
		javaCore, javaJdbc,javaEbean,
		"fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT",
		"fr.cea.ig" %% "play-spring-module" % "1.1-SNAPSHOT",
		"org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
		"mysql" % "mysql-connector-java" % "5.1.18",
		"net.sourceforge.jtds" % "jtds" % "1.2.2",
        "net.sf.opencsv" % "opencsv" % "2.0",
		"org.springframework" % "spring-test" % "3.0.7.RELEASE",
		"com.github.julienrf" %% "play-jsmessages" % "1.4.1"
    	)	
   	val ngldatatableDependencies = Seq(
   	    javaCore
   	    )
   	val nglframeworkwebDependencies = Seq(
   	    javaCore
   	    )
   	val nglbiDependencies = Seq(
	        // Add your project dependencies here,
	      javaCore, javaJdbc,
			"commons-collections" % "commons-collections" % "3.2.1",
	        "org.apache.commons" % "commons-lang3" % "3.1",
	       "fr.cea.ig" %% "play-spring-module" % "1.1-SNAPSHOT",
	        "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
	        "mysql" % "mysql-connector-java" % "5.1.18",
	        "postgresql" % "postgresql" % "8.3-603.jdbc4",
	        "net.sourceforge.jtds" % "jtds" % "1.2.4",      	
	        "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
	        "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"
			)
    	
    	val nglsqDependencies = Seq(
			javaCore, javaJdbc,
		      // Add your project dependencies here,
		      "fr.cea.ig" %% "play-spring-module" % "1.1-SNAPSHOT",
		      "net.sourceforge.jtds" % "jtds" % "1.2.4",
		      "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
		      "mysql" % "mysql-connector-java" % "5.1.18",
		      "net.sourceforge.jtds" % "jtds" % "1.2.2",
		      "fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
		      "fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
		      "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"  
		 	)
    	
    	val nglauthDependencies = Seq(
		javaCore, javaJdbc,javaEbean,
			"fr.cea.ig" %% "bootstrap" % "1.0-SNAPSHOT",
			"fr.cea.ig" %% "casplugin" % "1.0-SNAPSHOT",
	        "mysql" % "mysql-connector-java" % "5.1.18"
		)


       val nglplaquesDependencies = Seq(
              javaCore, javaJdbc,
              // Add your project dependencies here,
              "commons-collections" % "commons-collections" % "3.2.1",
              "fr.cea.ig" %% "play-spring-module" % "1.1-SNAPSHOT",
              "net.sourceforge.jtds" % "jtds" % "1.2.4",
              "org.springframework" % "spring-jdbc" % "3.0.7.RELEASE",
              "fr.cea.ig" %% "mongodbplugin" % "1.0-SNAPSHOT"
       )
        
       val ngldevguideDependencies = Seq(
           javaCore
       )
   }
   
  
    
  val nglframeworkweb = play.Project("lib-frameworkweb", libFrameworkWebVersion, nglframeworkwebDependencies, path = file("lib-ngl-frameworkweb"),settings = buildSettingsLib).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish)      
    )
    
     val ngldatatable = play.Project("datatable", libDatatableVersion, ngldatatableDependencies, path = file("lib-ngl-datatable"),settings = buildSettingsLib).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish)
    )
    
   val nglcommon = play.Project(appName + "-common", appVersion, nglcommonDependencies, path = file("lib-ngl-common"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
       resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/",
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish),       
       resourceDirectory in Test <<= baseDirectory / "conftest"
    ).dependsOn(nglframeworkweb, ngldatatable)
    
    
   val nglbi = play.Project(appName + "-bi", appVersion, nglbiDependencies, path = file("app-ngl-bi"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
     ).dependsOn(nglcommon)
   
    val ngldata = play.Project(appName + "-data", appVersion, nglsqDependencies, path = file("app-ngl-data"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
     ).dependsOn(nglcommon) 
    
    
   val nglsq = play.Project(appName + "-sq", appVersion, nglsqDependencies, path = file("app-ngl-sq"),settings = buildSettings).settings(
          // Add your own project settings here      
          resolvers := Seq(nexusig),
          publishArtifact in makePom := false,
          publishTo := Some(nexusigpublish) 
    ).dependsOn(nglcommon)
   
   
   val nglauth = play.Project(appName + "-authorization", appVersion, nglauthDependencies, path = file("app-ngl-authorization"),settings = buildSettings).settings(
             // Add your own project settings here      
             resolvers := Seq(nexusig),
             publishArtifact in makePom := false,
             publishTo := Some(nexusigpublish)
   ).dependsOn(nglcommon)
   
   val nglassets = play.Project(appName + "-assets", appVersion, path = file("app-ngl-asset"),settings = buildSettings).settings(
                // Add your own project settings here      
                resolvers := Seq(nexusig),
                publishArtifact in makePom := false,
                publishTo := Some(nexusigpublish) 
          
      )
   
   val nglplates = play.Project(appName + "-plates", appVersion, nglplaquesDependencies, path = file("app-ngl-plaques"),settings = buildSettings).settings(
       // Add your own project settings here
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
    ).dependsOn(nglcommon)


    val ngldevguide = play.Project(appName + "-devguide", appVersion, ngldevguideDependencies, path = file("app-ngl-devguide"),settings = buildSettings).settings(
       // Add your own project settings here      
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
    ).dependsOn(nglcommon)
    
   val main = play.Project(appName, appVersion, settings = buildSettings).settings(
      // Add your own project settings here      
      resolvers := Seq(nexusig),
      publishArtifact in makePom := false,
      publishTo := Some(nexusigpublish)
    ).aggregate(
     	nglcommon,nglframeworkweb,ngldatatable,nglsq,nglbi,nglauth,nglassets,nglplates,ngldata
    )

}
