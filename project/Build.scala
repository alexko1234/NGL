import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build {
   
   import BuildSettings._	
   import Resolvers._
   import Dependencies._
   import play.Play.autoImport._
   import PlayKeys._

   val appName = "ngl"
   val appVersion = "1.0-SNAPSHOT"
     
   val sqVersion = "1.5.1-SNAPSHOT"  
   val biVersion = "1.13-SNAPSHOT"
   val projectsVersion = "1.0-SNAPSHOT"  
   val reagentsVersion = "1.0-SNAPSHOT" 
   val subVersion = "1.0-SNAPSHOT"  
     
   val libDatatableVersion = "1.1-SNAPSHOT"
   val libFrameworkWebVersion = "1.1-SNAPSHOT"
   
   
  override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
  )
	
   
   object BuildSettings {
   
           val buildOrganization = "fr.cea.ig"
           val buildVersion      = appVersion
           
          val buildSettings =  Seq (
               organization   := buildOrganization+"."+appName,
               version        := buildVersion,
			   scalaVersion := "2.11.1",
               credentials += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
               publishMavenStyle := true
           )  
           
           val buildSettingsLib = Seq (
               organization   := buildOrganization,
               version        := buildVersion,  
			   scalaVersion := "2.11.1",
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
		javaCore, javaJdbc,javaEbean,javaWs,
		"fr.cea.ig" %% "play-spring-module" % "1.3-SNAPSHOT",
		"mysql" % "mysql-connector-java" % "5.1.18",
		"net.sourceforge.jtds" % "jtds" % "1.2.2",
	     "net.sf.opencsv" % "opencsv" % "2.0",
        "commons-collections" % "commons-collections" % "3.2.1",
		"org.springframework" % "spring-jdbc" % "4.0.3.RELEASE",		
		"org.springframework" % "spring-test" % "4.0.3.RELEASE",
		"org.julienrf" %% "play-jsmessages" % "1.6.2",
		"javax.mail" % "mail" % "1.4.2",
	    "org.codehaus.janino" % "janino" % "2.5.15"

    	)	
   	val ngldatatableDependencies = Seq(
   	    javaCore
   	    )
	val nglreagentsDependencies = Seq(
   	    javaCore
   	    )
   	val nglframeworkwebDependencies = Seq(
   	    javaCore,
		javaWs,
   	    "javax.mail" % "mail" % "1.4.2",
   	    "fr.cea.ig" %% "mongodbplugin" % "1.3-SNAPSHOT",
   	    "org.drools" % "drools-core" % "6.1.0.Final",
   	    "org.drools" % "drools-compiler" % "6.1.0.Final",
   	    "org.drools" % "knowledge-api" % "6.1.0.Final",
   	    "org.kie" % "kie-api" % "6.1.0.Final",
   	    "org.kie" % "kie-internal" % "6.1.0.Final",
		"fr.cea.ig.modules" %% "authentication" % "1.2-SNAPSHOT"
   	    )
   	val nglbiDependencies = Seq(
	        // Add your project dependencies here,
	      javaCore, javaJdbc,

		"postgresql" % "postgresql" % "8.3-603.jdbc4"

		)
	val nglsqDependencies = Seq(
		javaCore, javaJdbc,
		  // Add your project dependencies here,
		"org.assertj" % "assertj-core" % "1.7.1"
		)
	val nglauthDependencies = Seq(
	javaCore, javaJdbc,javaEbean,
	"fr.cea.ig" %% "bootstrap" % "1.2-SNAPSHOT"

		)
   val nglplaquesDependencies = Seq(
		  javaCore, javaJdbc
		)
   val ngldevguideDependencies = Seq(
	   javaCore
		)

	val ngldataDependencies = Seq(
		javaCore, javaJdbc,
		  // Add your project dependencies here,
		  "postgresql" % "postgresql" % "8.3-603.jdbc4"
		)
	val nglsubDependencies = Seq(
	    javaCore, javaJdbc
	    //"uk.co.panaxiom" %% "play-jongo" % "0.7.1-jongo1.0"
	)

	val nglprojectsDependencies = Seq(
	    javaCore, javaJdbc,
		"postgresql" % "postgresql" % "8.3-603.jdbc4"
	)

  }
   
   val ngldatatable = Project("datatable", file("lib-ngl-datatable"), settings = buildSettingsLib).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here
		version := libDatatableVersion,
		libraryDependencies ++= ngldatatableDependencies,
       resolvers := Seq(nexusig),
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish),
	   packagedArtifacts in publishLocal := {
		  val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
		  val assets: java.io.File = (playPackageAssets in Compile).value
		  artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
	   },
	   packagedArtifacts in publish := {
		  val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
		  val assets: java.io.File = (playPackageAssets in Compile).value
		  artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
	   }
    )    
  

  val nglframeworkweb = Project("lib-frameworkweb", file("lib-ngl-frameworkweb"),settings = buildSettingsLib).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here     
		version := libFrameworkWebVersion,
		libraryDependencies ++= nglframeworkwebDependencies,
       resolvers := Seq(nexusig),
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish),
        packagedArtifacts in publishLocal := {
		  val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
		  val assets: java.io.File = (playPackageAssets in Compile).value
		  artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
	   },
	   packagedArtifacts in publish := {
		  val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
		  val assets: java.io.File = (playPackageAssets in Compile).value
		  artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
	   }
    ).dependsOn(ngldatatable)
    
    
   val nglcommon = Project(appName + "-common", file("lib-ngl-common"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here   
		version := appVersion,
		libraryDependencies ++= nglcommonDependencies,
			   
       resolvers := Seq(nexusig),
       resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/",
	   sbt.Keys.fork in Test := false,
       publishTo := Some(nexusigpublish),       
       resourceDirectory in Test <<= baseDirectory / "conftest"
    ).dependsOn(nglframeworkweb)
    
    
   val nglbi = Project(appName + "-bi", file("app-ngl-bi"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here      
       version := biVersion,
		libraryDependencies ++= nglbiDependencies,
	   resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon)
   
    val ngldata = Project(appName + "-data", file("app-ngl-data"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here 
		version := appVersion,
		libraryDependencies ++= ngldataDependencies,		   
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon) 
    
	val nglsq = Project(appName + "-sq", file("app-ngl-sq"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
          // Add your own project settings here      
		  version := sqVersion,
		libraryDependencies ++= nglsqDependencies,		
          resolvers := Seq(nexusig),
          publishArtifact in makePom := false,
          publishTo := Some(nexusigpublish)
    ).dependsOn(nglcommon)
	
    val nglsub = Project(appName + "-sub", file("app-ngl-sub"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
        // Add your own project settings here    
		version := subVersion,
		libraryDependencies ++= nglsubDependencies,			
          resolvers := Seq(nexusig),
          publishArtifact in makePom := false,
          publishTo := Some(nexusigpublish)
    ).dependsOn(nglcommon)
    
    val nglauth = Project(appName + "-authorization", file("app-ngl-authorization"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
             // Add your own project settings here   
		version := appVersion,
		libraryDependencies ++= nglauthDependencies,			 
             resolvers := Seq(nexusig),
             publishArtifact in makePom := false,
             publishTo := Some(nexusigpublish)
   ).dependsOn(nglcommon)
   
   val nglassets = Project(appName + "-assets", file("app-ngl-asset"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
		// Add your own project settings here  
		version := appVersion,				
		resolvers := Seq(nexusig),
		publishArtifact in makePom := false,
		publishTo := Some(nexusigpublish)
          
      )
   
   val nglplates = Project(appName + "-plates", file("app-ngl-plaques"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here
	   version := appVersion,
		libraryDependencies ++= nglplaquesDependencies,	
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
    ).dependsOn(nglcommon)


    val ngldevguide = Project(appName + "-devguide", file("app-ngl-devguide"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here      
	     version := appVersion,
		libraryDependencies ++= ngldevguideDependencies,
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
    ).dependsOn(nglcommon)

   val nglprojects = Project(appName + "-projects", file("app-ngl-projects"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here   
		version := projectsVersion,
		libraryDependencies ++= nglprojectsDependencies,   
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon)
	 
	val nglreagents = Project(appName + "-reagents", file("app-ngl-reagents"),settings = buildSettings).enablePlugins(play.PlayJava).settings(
       // Add your own project settings here   
		version := reagentsVersion,
		libraryDependencies ++= nglreagentsDependencies,   
       resolvers := Seq(nexusig),
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon)

 	 
   val main = Project(appName, file("."),settings = buildSettings).enablePlugins(play.PlayJava).settings(
      // Add your own project settings here     
		version := appVersion,			  
      resolvers := Seq(nexusig),
      publishArtifact in makePom := false,
      publishTo := Some(nexusigpublish)
    ).aggregate(
     	nglcommon,nglframeworkweb,ngldatatable,nglsq,nglbi,nglauth,nglassets,nglplates,ngldata,nglsub
    )

}
