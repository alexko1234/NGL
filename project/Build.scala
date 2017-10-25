import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

import play.sbt.routes.RoutesKeys.routesGenerator
import play.routes.compiler.StaticRoutesGenerator
// import play.routes.compiler.DynamicRoutesGenerator
import play.sbt.routes.RoutesCompiler.autoImport._


object ApplicationBuild extends Build {

	import BuildSettings._	
	import Resolvers._
	import Dependencies._
	// import play.Play.autoImport._
	import play.sbt.Play.autoImport._

	// import PlayKeys._
	import play.sbt.PlayImport._

   val sqVersion = "1.36.0-SNAPSHOT"  
   val biVersion = "1.38.0-SNAPSHOT"

 	val appName    = "ngl"
	val appVersion = "1.0-SNAPSHOT"
  // val scala      = "2.11.6"
	val scala      = "2.11.8"
  
    val projectsVersion = "1.3.1-SNAPSHOT"  
   val reagentsVersion = "1.3.0-SNAPSHOT" 
   val subVersion = "1.4.0-SNAPSHOT"  
   val libDatatableVersion = "1.2-SNAPSHOT"
   val libFrameworkWebVersion = "1.1-SNAPSHOT"
   
   val postgresql = "postgresql" % "postgresql" % "8.3-603.jdbc4"
   //val postgresql = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
   //val postgresql = "org.postgresql" % "postgresql" % "9.4.1208"
   
  override def settings = super.settings ++ Seq(
        EclipseKeys.skipParents in ThisBuild := false
  )

  val distSuffix = ".2.4"
  
	val sqVersion  = "1.34.0"+distSuffix+"-SNAPSHOT"  
	val biVersion  = "1.38.0"+distSuffix+"-SNAPSHOT"

	val projectsVersion = "1.3.0"+distSuffix+"-SNAPSHOT"  
	val reagentsVersion = "1.3.0"+distSuffix+"-SNAPSHOT"

	val subVersion = "1.3.0"+distSuffix+"-SNAPSHOT"  
	val libDatatableVersion = "1.2"+distSuffix+"-SNAPSHOT"
	val libFrameworkWebVersion = "1.1"+distSuffix+"-SNAPSHOT"

	val postgresql = "postgresql" % "postgresql" % "8.3-603.jdbc4"
	//val postgresql = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
	//val postgresql = "org.postgresql" % "postgresql" % "9.4.1208"
	
  // val ceaAuth     = "fr.cea.ig.modules" %% "authentication"     % "2.4-1.5-SNAPSHOT"
	val ceaAuth     = "fr.cea.ig.modules" %% "authentication"     % "2.5-1.5.1-SNAPSHOT"
  // val ceaSpring   = "fr.cea.ig"         %% "play-spring-module" % "2.4-1.4-SNAPSHOT"
  val ceaSpring   = "fr.cea.ig"         %% "play-spring-module" % "2.5-1.4.1-SNAPSHOT"
  // val ceaMongo   = "fr.cea.ig"         %% "mongodbplugin"      % "1.6.0-SNAPSHOT"
  // val ceaMongo    = "fr.cea.ig"         %% "mongodbplugin"      % "2.4-1.7.0-SNAPSHOT"
  // val ceaMongo    = "fr.cea.ig"         %% "mongodbplugin"      % "2.4-1.7.1-SNAPSHOT"
  val ceaMongo    = "fr.cea.ig"         %% "mongodbplugin"      % "2.5-1.7.2-SNAPSHOT"
  
  val commonsLang = "commons-lang"       % "commons-lang"       % "2.4"
  // val jsMessages  = "org.julienrf"      %% "play-jsmessages"    % "1.6.2"
  val jsMessages  = "org.julienrf"      %% "play-jsmessages"    % "2.0.0" // play 2.4,2.5
  // val scalaLib    = "org.scala-lang"     % "scala-library"      % scala 
    
	override def settings = super.settings ++ Seq(
			EclipseKeys.skipParents in ThisBuild := false
		)


	object BuildSettings {

		val buildOrganization = "fr.cea.ig"
		val buildVersion      = appVersion

	  val globSettings = Seq(
      // scalacOptions += "-deprecation",
			//javacOptions  ++= Seq("-Xlint:deprecation","-Xlint:unchecked")
			//javacOptions += "-verbose"
			// javacOptions += "-Xlint",
			// scalaVersion := "2.11.1"
			//libraryDependencies += evolutions,
			//libraryDependencies += jdbc,
			// libraryDependencies += jdbc,
			version             := buildVersion,  
			credentials         += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
			publishMavenStyle   := true,
			routesGenerator     := StaticRoutesGenerator,
			       //dependencyOverrides += "io.netty" % "netty" % "3.10.1.Final",
			       //dependencyOverrides += "org.jboss.logging" % "jboss-logging" % "3.1.1.GA",
 			       //dependencyOverrides += "com.google.guava" % "guava" % "15.0",
			       //dependencyOverrides += "com.typesafe.play" % "play-java_2.11" % "2.3.0",
 			       //dependencyOverrides += "com.typesafe.play" % "twirl-api_2.11" % "1.0.0",
			       //dependencyOverrides += "com.typesafe.play" % "play-java-jdbc_2.11" % "2.3.0", 
			       //dependencyOverrides += "org.springframework" % "spring-tx" % "3.2.8.RELEASE",
			       //dependencyOverrides += "com.typesafe.play" % "play-jdbc_2.11" % "2.3.0",
			       //dependencyOverrides += "com.typesafe.play" % "play-java-ws_2.11" % "2.3.0",
			       //dependencyOverrides += "com.typesafe.play" % "play-ws_2.11" % "2.3.0",
             //dependencyOverrides += "org.springframework" % "spring-context" % "3.2.8.RELEASE",
             //dependencyOverrides += "org.springframework" % "spring-aop" % "3.2.8.RELEASE",
             //dependencyOverrides += "org.springframework" % "spring-beans" % "3.2.8.RELEASE",
             //dependencyOverrides += "org.springframework" % "spring-core" % "3.2.8.RELEASE",
             //dependencyOverrides += "com.typesafe.play" % "play_2.11" % "2.3.0",
             //dependencyOverrides += "xml-apis" % "xml-apis" % "1.0.b2",
             // routesGenerator := StaticRoutesGenerator, // default value, supposed no efect
					   // routesGenerator := DynamicRoutesGenerator,
             //libraryDependencies += guice,
			       scalaVersion        := scala // play 2.4 req
		)

		val buildSettings =  Seq (
		  organization   := buildOrganization+"."+appName
					   //version        := buildVersion,
					   //scalaVersion := "2.11.1",
					   //credentials += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials"))
					   //publishMavenStyle := true
					   ) ++ globSettings

			   val buildSettingsLib = Seq (
					   organization   := buildOrganization
					   //credentials       += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials"))
					   ) ++ globSettings

   }

  
   object Resolvers {        
	   import BuildSettings._
	   var location = sys.env.getOrElse("NGL_LOCATION", default = "external")

	   val nglgithub	= "NGL GitHub Repo" at "https://institut-de-genomique.github.io/NGL-Dependencies/"    
	   val nexusoss = "Sonatype OSS" at "https://oss.sonatype.org/content/groups/public/"
	   val mavencentral = "Maven central" at "http://central.maven.org/maven2/"
	   val nexusig = "Nexus repository" at "https://gsphere.genoscope.cns.fr/nexus/content/groups/public/" 
	   val nexus = if(location.equalsIgnoreCase("external")) Seq(nexusoss,mavencentral,nglgithub) else Seq(nexusig,nglgithub)	
	   val nexusigrelease = "releases"  at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/releases"
	   val nexusigsnapshot = "snapshots" at "https://gsphere.genoscope.cns.fr/nexus/content/repositories/snapshots"
	   val nexusigpublish = if (buildVersion.endsWith("SNAPSHOT")) nexusigsnapshot else nexusigrelease				
   } 
 
   object Dependencies {
	   val nglcommonDependencies = Seq(
			   javaCore,
			   javaJdbc, 
			   javaWs,
			   //"fr.cea.ig" %% "play-spring-module" % "1.3-SNAPSHOT",
			   // "fr.cea.ig" %% "play-spring-module" % "2.4-1.4-SNAPSHOT",
			   ceaSpring,
			   "mysql" % "mysql-connector-java" % "5.1.18",
			   "net.sourceforge.jtds" % "jtds" % "1.2.2",
			   // "net.sourceforge.jtds" % "jtds" % "1.3.1",
			   "net.sf.opencsv" % "opencsv" % "2.0",
			   "commons-collections" % "commons-collections" % "3.2.1",
			   "org.springframework" % "spring-jdbc" % "4.0.3.RELEASE",		
			   "org.springframework" % "spring-test" % "4.0.3.RELEASE",
			   // "org.julienrf" %% "play-jsmessages" % "1.6.2",
			   jsMessages,
			   "javax.mail" % "mail" % "1.4.2",
			   "org.codehaus.janino" % "janino" % "2.5.15",
			   "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.0",
			   "org.javassist" % "javassist" % "3.20.0-GA",
			   commonsLang,
			   cache
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
					   "org.drools" % "drools-core" % "6.1.0.Final",
					   "org.drools" % "drools-compiler" % "6.1.0.Final",
					   "org.drools" % "knowledge-api" % "6.1.0.Final",
					   "org.kie" % "kie-api" % "6.1.0.Final",
					   "org.kie" % "kie-internal" % "6.1.0.Final",
					   // "fr.cea.ig.modules" %% "authentication" % "1.4-SNAPSHOT",
					   // "fr.cea.ig.modules" %% "authentication" % "2.4-1.5-SNAPSHOT",
					   // "fr.cea.ig" %% "mongodbplugin" % "1.6.0-SNAPSHOT"
					   ceaAuth,
					   ceaMongo
					   )
			   val nglbiDependencies = Seq(
					   // Add your project dependencies here,
					   javaCore, javaJdbc,

					   // "postgresql" % "postgresql" % "8.3-603.jdbc4"
					   postgresql
					   )
			   val nglsqDependencies = Seq(
					   javaCore, 
					   javaJdbc,
					   // Add your project dependencies here,
					   // "postgresql" % "postgresql" % "8.3-603.jdbc4",
					   postgresql,
					   "org.assertj" % "assertj-core" % "1.7.1"
					   )
/*
	val nglauthDependencies = Seq(
	javaCore, javaJdbc,javaEbean,
	"fr.cea.ig" %% "bootstrap" % "1.2-SNAPSHOT"
		)
*/		
		
   val nglplaquesDependencies = Seq(
		  javaCore, javaJdbc
		)
   val ngldevguideDependencies = Seq(
	   javaCore
		)

	val ngldataDependencies = Seq(
		javaCore, javaJdbc,
		  // Add your project dependencies here,
		  //"postgresql" % "postgresql" % "8.3-603.jdbc4"
		  postgresql
		)
	val nglsubDependencies = Seq(
	    javaCore, javaJdbc
	    //"uk.co.panaxiom" %% "play-jongo" % "0.7.1-jongo1.0"
	)

	val nglprojectsDependencies = Seq(
	    javaCore, javaJdbc,
		//"postgresql" % "postgresql" % "8.3-603.jdbc4"
	    postgresql
	)

  }

  val ngldatatable = Project("datatable", file("lib-ngl-datatable"), settings = buildSettingsLib).enablePlugins(play.sbt.Play).settings(
    // Add your own project settings here
    version := libDatatableVersion,
    libraryDependencies ++= ngldatatableDependencies,
    resolvers := nexus,
    sbt.Keys.fork in Test := false,
    publishTo := Some(nexusigpublish),
    packagedArtifacts in publishLocal := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    },
    packagedArtifacts in publish := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    })

  val nglframeworkweb = Project("lib-frameworkweb", file("lib-ngl-frameworkweb"), settings = buildSettingsLib).enablePlugins(play.sbt.Play).settings(
    // Add your own project settings here     
    version := libFrameworkWebVersion,
    libraryDependencies ++= nglframeworkwebDependencies,
    resolvers := nexus,
    sbt.Keys.fork in Test := false,
    publishTo := Some(nexusigpublish),
    packagedArtifacts in publishLocal := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    },
    packagedArtifacts in publish := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    }).dependsOn(ngldatatable)

  val nglcommon = Project(appName + "-common", file("lib-ngl-common"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    // Add your own project settings here   
    version := appVersion,
    libraryDependencies ++= nglcommonDependencies,

    resolvers := nexus,
    resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/",
    sbt.Keys.fork in Test := false,
    publishTo := Some(nexusigpublish),
    // resourceDirectory in Test <<= baseDirectory / "conftest" 
    // baseDirectory : RichFileSetting
    //   /(c: String): Def.Initialize[File]
    // trait DefinableSetting[S]
    //   <<=(app: Def.Initialize[S]): Def.Setting[S]
    // (resourceDirectory.in(Test)).<<=(baseDirectory.value./("conftest"))
    // resourceDirectory.in(Test).:=(baseDirectory.value./("conftest"))
    resourceDirectory in Test := baseDirectory.value / "conftest"
    ).dependsOn(nglframeworkweb)

  val nglbi = Project(appName + "-bi", file("app-ngl-bi"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    // Add your own project settings here      
    version := biVersion,
    libraryDependencies ++= nglbiDependencies,
    resolvers := nexus,
    publishArtifact in makePom := false,
    publishTo := Some(nexusigpublish)).dependsOn(nglcommon % "test->test;compile->compile")

  val ngldata = Project(appName + "-data", file("app-ngl-data"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    // Add your own project settings here 
    version := appVersion,
    libraryDependencies ++= ngldataDependencies,
    resolvers := nexus,
    publishArtifact in makePom := false,
    publishTo := Some(nexusigpublish)).dependsOn(nglcommon % "test->test;compile->compile")

  val nglsq = Project(appName + "-sq", file("app-ngl-sq"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version              := sqVersion,
    libraryDependencies ++= nglsqDependencies,
    resolvers            := nexus,
	//publishArtifact in (Compile, packageDoc) := false,
    //publishArtifact in packageDoc := false,
    sources in (Compile,doc) := Seq.empty,
    publishArtifact in makePom := false,
    publishTo            := Some(nexusigpublish)
    ).dependsOn(nglcommon % "test->test;compile->compile")

  val nglsub = Project(appName + "-sub", file("app-ngl-sub"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    // Add your own project settings here    
    version := subVersion,
    libraryDependencies ++= nglsubDependencies,
    resolvers := nexus,
    publishArtifact in makePom := false,
    publishTo := Some(nexusigpublish)).dependsOn(nglcommon % "test->test;compile->compile")
/*    
    val nglauth = Project(appName + "-authorization", file("app-ngl-authorization"),settings = buildSettings).enablePlugins(play.sbt.Play).settings(
             // Add your own project settings here   
		version := appVersion,
		libraryDependencies ++= nglauthDependencies,			 
             resolvers := nexus,
             publishArtifact in makePom := false,
             publishTo := Some(nexusigpublish)
   ).dependsOn(nglcommon)
*/   
   val nglassets = Project(appName + "-assets", file("app-ngl-asset"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		// Add your own project settings here  
		version := appVersion,				
		resolvers := nexus,
		publishArtifact in makePom := false,
		publishTo := Some(nexusigpublish)
          
      )
   
   val nglplates = Project(appName + "-plates", file("app-ngl-plaques"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
       // Add your own project settings here
	   version := appVersion,
		libraryDependencies ++= nglplaquesDependencies,	
       resolvers := nexus,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
    ).dependsOn(nglcommon % "test->test;compile->compile")


    val ngldevguide = Project(appName + "-devguide", file("app-ngl-devguide"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
       // Add your own project settings here      
	     version := appVersion,
		libraryDependencies ++= ngldevguideDependencies,
       resolvers := nexus,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish) 
    ).dependsOn(nglcommon % "test->test;compile->compile")

   val nglprojects = Project(appName + "-projects", file("app-ngl-projects"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
       // Add your own project settings here   
		version := projectsVersion,
		libraryDependencies ++= nglprojectsDependencies,   
       resolvers := nexus,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon % "test->test;compile->compile")
	 
	val nglreagents = Project(appName + "-reagents", file("app-ngl-reagents"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
       // Add your own project settings here   
		version := reagentsVersion,
		libraryDependencies ++= nglreagentsDependencies,   
       resolvers := nexus,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)
     ).dependsOn(nglcommon % "test->test;compile->compile")

     /*
   val bcRoute = Project("bc-route",file("buildcheck/route"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		     version := "0.1-SNAPHSHOT"
		     )
	 // Use ngl-seq dependencies 
   val bcRouteCommon = Project("bc-routeCommon",file("buildcheck/routeCommon"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		     version := "0.1-SNAPHSHOT",
		     //libraryDependencies ++= nglcommonDependencies,
		     libraryDependencies ++= nglsqDependencies,
		     // libraryDependencies ++= nglsubDependencies,
		     // libraryDependencies += "fr.cea.ig.modules" %% "authentication" % "1.4-SNAPSHOT",
       resolvers := nexus/*,
       publishArtifact in makePom := false,
       publishTo := Some(nexusigpublish)*/
		   ).dependsOn(nglcommon % "test->test;compile->compile")
		   
  val bcRouteAuth = Project("bc-routeAuth",file("buildcheck/routeAuth"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version             := "0.1-SNAPHSHOT",
    libraryDependencies += ceaAuth, // "fr.cea.ig.modules" %% "authentication" % "2.4-1.5-SNAPSHOT",
    // libraryDependencies += "fr.cea.ig"         %% "mongodbplugin"  % "1.6.0-SNAPSHOT",
    libraryDependencies += ceaSpring, // "fr.cea.ig" %% "play-spring-module" % "2.4-1.4-SNAPSHOT",
    // libraryDependencies ++= nglsqDependencies,
    libraryDependencies ++= nglcommonDependencies,
    resolvers           := nexus
	)
		   */
     
   val main = Project(appName, file("."),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
      // Add your own project settings here     
		version := appVersion,			  
      resolvers := nexus,
      publishArtifact in makePom := false,
      publishTo := Some(nexusigpublish)
    ).aggregate(
     	nglcommon,nglframeworkweb,ngldatatable,nglsq,nglbi,nglassets,nglplates,ngldata,nglsub,nglreagents,nglprojects
     	//,bcRoute,bcRouteCommon,bcRouteAuth
    )


}
