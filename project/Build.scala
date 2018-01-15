//
// Configuration from command line or environment
//
//   embedded.auth=true
//     configures the build to use the authentication directory
//     in ngl instead of an external dependency.
//
//   NGL_CONF_TEST_DIR=<path>
//   ngl.test.conf.dir=<path>
//     Those definitions are added to the build classpath and the test
//     infrastructure will locate the test configuration through the
//     classpath.
//

import sbt._
import Keys._

import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import com.typesafe.sbteclipse.core.EclipsePlugin._
// import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys._

import play.sbt.routes.RoutesKeys.routesGenerator
import play.routes.compiler.StaticRoutesGenerator
// import play.routes.compiler.DynamicRoutesGenerator
import play.sbt.routes.RoutesCompiler.autoImport._


import play.sbt.routes.RoutesKeys.routesGenerator
import play.routes.compiler.StaticRoutesGenerator
// import play.routes.compiler.DynamicRoutesGenerator
import play.routes.compiler.InjectedRoutesGenerator


object ApplicationBuild extends Build {
//
	import BuildSettings._	
	import Resolvers._
	import Dependencies._
	// import play.Play.autoImport._
	import play.sbt.Play.autoImport._

	// import PlayKeys._
	import play.sbt.PlayImport._

	// Command line definition that allows the compilation 
	// of a linked authentication library instead of the published one.
	// It is enabled using sbt command line option -Dembedded.auth=true .
	val embeddedAuth   = System.getProperty("embedded.auth")   == "true"
	val eclipseLinking = System.getProperty("eclipse.linking") == "true"
	
	// Disable paralell test execution (hoped to fixed test failure but didn't work)
	// parallelExecution in Global := false
	
 	val appName    = "ngl"
	
  val scala            = "2.12.3"

  // Dist suffix should be "-SNAPSHOT" for the master and "xxx-SNAPSHOT" for specific branches
  // so the deployed application do not land in the same directories. This could be defined 
  // in some configuration instead of being hardcoded.
  // val distSuffix             = "-SNAPSHOT"
  val distSuffix             = "-SNAPSHOT"
  //val appVersion             = "2.0.0"    + distSuffix
  
  val buildOrganization      = "fr.cea.ig"
	val buildVersion           = "2.1"    + distSuffix
	val nglVersion             = "2.0"    + distSuffix
	
	val sqVersion              = "2.1.0" + distSuffix
	val biVersion              = "2.2.0" + distSuffix

	val projectsVersion        = "2.2.0"  + distSuffix
	val reagentsVersion        = "2.1.0"  + distSuffix

	val subVersion             = "2.2.0"  + distSuffix
	
	
	// val dataVersion            = "2.0.0"  + distSuffix
	val nglAssetsVersion       = "2.0.0"  + distSuffix
	val nglDataVersion         = "2.0.1"  + distSuffix
	val nglPlatesVersion       = "2.0.0"  + distSuffix
	val nglDevGuideVersion     = "2.0.0"  + distSuffix
	
	val libDatatableVersion    = "2.0.0"    + distSuffix
	val libFrameworkWebVersion = "2.0.0"    + distSuffix
	val nglCommonVersion       = "2.1"    + distSuffix

	// IG libraries
  // val ceaAuth     = "fr.cea.ig.modules"   %% "authentication"     % "2.6-1.5.3-SNAPSHOT"
  // val ceaAuth     = "fr.cea.ig.modules"   %% "authentication"     % "2.6-1.5.4-SNAPSHOT"
  val ceaAuth     = "fr.cea.ig.modules"   %% "authentication"     % "2.6-2.0.4-SNAPSHOT"
	val ceaSpring   = "fr.cea.ig"           %% "play-spring-module" % "2.6-1.4.2-SNAPSHOT"
	val ceaMongo    = "fr.cea.ig"           %% "mongodbplugin"      % "2.6-1.7.4-SNAPSHOT"
  // External libraries versions
	val postgresql  = "org.postgresql"       % "postgresql"         % "9.4-1206-jdbc41"  
  val commonsLang = "commons-lang"         % "commons-lang"       % "2.4"
  val jsMessages  = "org.julienrf"        %% "play-jsmessages"    % "3.0.0" 
  val fest        = "org.easytesting"      % "fest-assert"        % "1.4" % "test"
  val jtds        = "net.sourceforge.jtds" % "jtds"               % "1.3.1"

  // This does not work
  val eclipseLinkingSettings =
    /*if (eclipseLinking) 
      Seq(EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources),	
          EclipseKeys.preTasks  := Seq(compile in Compile),
          EclipseKeys.skipParents in ThisBuild := false) 
    else*/ 
      Seq()
          
	override def settings = super.settings ++ Seq(
		EclipseKeys.skipParents in ThisBuild := false,
    // Compile the project before generating Eclipse files,
    // so that generated .scala or .class files for views and routes are present
    // EclipseKeys.preTasks := Seq(compile in Compile),
    // Java project. Don't expect Scala IDE
    EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
    // Use .class files instead of generated .scala files for views and routes
    // EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)	
  ) ++ eclipseLinkingSettings

	object BuildSettings {


		// Probably poor scala style
    val tev0 = if (System.getProperty("ngl.test.conf.dir") != null)
	        Seq(unmanagedResourceDirectories in Compile += file(System.getProperty("ngl.test.conf.dir")),
	            dependencyClasspath in Compile += file(System.getProperty("ngl.test.conf.dir")),
	            dependencyClasspath in Test    += file(System.getProperty("ngl.test.conf.dir")))
	      else
	        Seq()
	  val tev1 = if (System.getProperty("NGL_CONF_TEST_DIR") != null)
	        Seq(dependencyClasspath in Test    += file(System.getProperty("NGL_CONF_TEST_DIR")),
	            dependencyClasspath in Compile += file(System.getProperty("NGL_CONF_TEST_DIR")))
	      else
	        Seq()
	  
	  val globSettings = Seq(
	    // -- Scala compilation options are not defined as there are no scala sources
      // scalacOptions += "-deprecation",
	    // -- Java compilation options are not defined as there are too many warnings
	    // TODO: enable java compilation warnings
			// javacOptions  ++= Seq("-Xlint:deprecation","-Xlint:unchecked")
			// javacOptions += "-verbose"
			// javacOptions += "-Xlint",
			version             := buildVersion,  
			credentials         += Credentials(new File(sys.env.getOrElse("NEXUS_CREDENTIALS","") + "/nexus.credentials")),
			publishMavenStyle   := true,
			// We keep the static route generator but this should be removed
			// TODO: use dynamic route generator
			routesGenerator     := StaticRoutesGenerator,
			// jackson 2.8 series is problematic so we fall back on 2.7
			dependencyOverrides += "com.fasterxml.jackson.core"     % "jackson-core"            % "2.7.3",
			dependencyOverrides += "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.7.3",
			dependencyOverrides += "com.fasterxml.jackson.core"     % "jackson-annotations"     % "2.7.3",
			dependencyOverrides += "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8"   % "2.7.3",
			dependencyOverrides += "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.7.3",
			javacOptions in (Compile,doc) ++= Seq("-notimestamp", "-linksource", "-quiet"),
			// Remove scala files from the doc process so javadoc is used. 
			sources in (Compile, doc) <<= sources in (Compile, doc) map { _.filterNot(_.getName endsWith ".scala") },
			// sources in doc in Compile := Seq(),
			// Remove javadoc jar creation when packaging (building dist)
			mappings in (Compile, packageDoc) := Seq(),
			scalaVersion        := scala
		) ++ tev0 ++ tev1

		val buildSettings =  Seq (
		  organization   := buildOrganization + "." + appName
		) ++ globSettings

		val buildSettingsLib = Seq (
			organization   := buildOrganization
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
    
    val nglTestingDependencies = Seq(
      javaCore,
      javaWs,
      ceaMongo
    )
    
	  val nglcommonDependencies = Seq(
		  javaCore,
		  javaJdbc, 
		  javaWs,
		  ceaSpring,
		  jsMessages,
		  commonsLang,
			fest,
		  jtds,
		   guice,
		  "mysql"                % "mysql-connector-java"      % "5.1.18",
		  "net.sf.opencsv"       % "opencsv"                   % "2.0",
		  "commons-collections"  % "commons-collections"       % "3.2.1",
		  "org.springframework"  % "spring-jdbc"               % "4.0.3.RELEASE",		
		  "org.springframework"  % "spring-test"               % "4.0.3.RELEASE",
		  "javax.mail"           % "mail"                      % "1.4.2",
		  "org.codehaus.janino"  % "janino"                    % "2.5.15",
		  "de.flapdoodle.embed"  % "de.flapdoodle.embed.mongo" % "1.50.0",
		  "org.javassist"        % "javassist"                 % "3.20.0-GA",
		  "com.sybase"		       % "jdbc4"                 	   % "7.0"
		)	
			   
	  val ngldatatableDependencies = Seq(
			javaForms
	  )

	  val nglreagentsDependencies = Seq(
		  javaCore
		)
			
		val nglframeworkwebDependencies = Seq(
		  javaCore,
		  javaWs,
		  //ceaAuth,
		  ceaMongo,
		  "javax.mail" % "mail"            % "1.4.2",
		  "org.drools" % "drools-core"     % "6.1.0.Final",
		  "org.drools" % "drools-compiler" % "6.1.0.Final",
		  "org.drools" % "knowledge-api"   % "6.1.0.Final",
		  "org.kie"    % "kie-api"         % "6.1.0.Final",
		  "org.kie"    % "kie-internal"    % "6.1.0.Final"
		) ++ (if (embeddedAuth) Seq() else Seq(ceaAuth))
		
		val nglbiDependencies = Seq(
			javaCore, 
			javaJdbc,
			postgresql
		)
		
		val nglsqDependencies = Seq(
		  javaCore, 
		  javaJdbc,
		  postgresql,
		  "org.assertj" % "assertj-core" % "1.7.1"
	  )
	  
    val nglplaquesDependencies = Seq(
		  javaCore, 
		  javaJdbc
		)
		
    val ngldevguideDependencies = Seq(
	    javaCore
		)

	  val ngldataDependencies = Seq(
  		javaCore, 
	  	javaJdbc,
	    postgresql
		)
		
	  val nglsubDependencies = Seq(
	    javaCore, 
	    javaJdbc,
	    "org.apache.commons" % "commons-csv" % "1.5",
	    "xerces"             % "xercesImpl"  % "2.8.0"
  	)

	  val nglprojectsDependencies = Seq(
	    javaCore, 
	    javaJdbc,
	    postgresql
	  )

	  val nglPlayMigrationDependencies = Seq(
	    ceaMongo,
	    ehcache,
	    ws,
	    jsMessages
    )
    
    val springVersion    = "4.1.6.RELEASE"
    val springSecVersion = "4.0.0.RELEASE"

    val authenticationDependencies = Seq(
      javaCore,
		  ws,
		  javaJdbc,
		  ceaSpring,
  		"org.springframework.security"  % "spring-security-web"    % springSecVersion,
		  "org.springframework.security"  % "spring-security-ldap"   % springSecVersion,
      "org.springframework.security"  % "spring-security-config" % springSecVersion,
  		"org.springframework"           % "spring-aop"             % springVersion
    )
    
  }

  // Allow the use of embbeded auth sources instead of the lib.
  // We use a virtual project that has only one dependency when using the
  // external dependency.
  val authentication = 
    if (embeddedAuth)
      Project("auth",file("authentication"),settings = buildSettings)
        .enablePlugins(play.sbt.PlayJava)
        .settings(
      // routesGenerator     := InjectedRoutesGenerator, // does not work
      libraryDependencies ++= authenticationDependencies,
      version              := "2.0.0-SNAPSHOT",
      resolvers            := nexus
    )
    else
      Project("auth",file("dependency-authentication"),settings = buildSettings)
        .enablePlugins(play.sbt.PlayJava)
        .settings(
      libraryDependencies  += ceaAuth,
      version              := "0.1-SNAPSHOT",
      resolvers            := nexus
    )
  
  val nglPlayMigration =  Project("ngl-play-migration",file("lib-ngl-play-migration"),settings = buildSettings)
      .enablePlugins(play.sbt.PlayJava)
      .settings(
    libraryDependencies ++= nglPlayMigrationDependencies,   
    version              := "0.1-SNAPSHOT",
    resolvers            := nexus
	)
	
	val nglTesting = Project("ngl-testing",file("lib-ngl-testing"),settings = buildSettings)
      .enablePlugins(play.sbt.PlayJava)
      .settings(
    libraryDependencies       ++= nglTestingDependencies, 
    version                    := "0.1-SNAPSHOT",
    resolvers                  := nexus
  )	    

  val ngldatatable = Project("datatable", file("lib-ngl-datatable"), settings = buildSettingsLib).enablePlugins(play.sbt.Play).settings(
    version                    := libDatatableVersion,
    libraryDependencies       ++= ngldatatableDependencies,
    resolvers                  := nexus,
    sbt.Keys.fork in Test      := false,
    publishTo                  := Some(nexusigpublish),
    packagedArtifacts in publishLocal := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    },
    packagedArtifacts in publish := {
      val artifacts: Map[sbt.Artifact, java.io.File] = (packagedArtifacts in publishLocal).value
      val assets: java.io.File = (PlayKeys.playPackageAssets in Compile).value
      artifacts + (Artifact(moduleName.value, "asset", "jar", "assets") -> assets)
    }
  ).dependsOn(nglPlayMigration)

  val nglframeworkweb = Project("lib-frameworkweb", file("lib-ngl-frameworkweb"), settings = buildSettingsLib).enablePlugins(play.sbt.Play).settings(
    version                    := libFrameworkWebVersion,
    libraryDependencies       ++= nglframeworkwebDependencies,
    resolvers                  := nexus,
    sbt.Keys.fork in Test      := false,
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
    }
  ).dependsOn(ngldatatable,authentication)
  
  val nglcommon = Project(appName + "-common", file("lib-ngl-common"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    // version                    := appVersion,
    version                    := nglCommonVersion,
    libraryDependencies       ++= nglcommonDependencies,
    resolvers                  := nexus,
    resolvers                  += "julienrf.github.com" at "http://julienrf.github.com/repo/",
    sbt.Keys.fork in Test      := false,
    publishTo                  := Some(nexusigpublish),
    
    // resourceDirectory in Test <<= baseDirectory / "conftest" 
    // baseDirectory : RichFileSetting
    //   /(c: String): Def.Initialize[File]
    // trait DefinableSetting[S]
    //   <<=(app: Def.Initialize[S]): Def.Setting[S]
    // (resourceDirectory.in(Test)).<<=(baseDirectory.value./("conftest"))
    // resourceDirectory.in(Test).:=(baseDirectory.value./("conftest"))
    resourceDirectory in Test := baseDirectory.value / "conftest"
  // ).dependsOn(nglframeworkweb % "compile->compile;test->test;doc->doc", nglPlayMigration, nglTesting % "test->test")
  ).dependsOn(nglframeworkweb, nglPlayMigration, nglTesting % "test->test")

  val nglbi = Project(appName + "-bi", file("app-ngl-bi"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version                    := biVersion,
    libraryDependencies       ++= nglbiDependencies,
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val ngldata = Project(appName + "-data", file("app-ngl-data"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version                    := nglDataVersion,
    libraryDependencies       ++= ngldataDependencies,
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val nglsq = Project(appName + "-sq", file("app-ngl-sq"), settings = buildSettings)
                 .enablePlugins(play.sbt.PlayJava)
                 .configs( IntegrationTest )
                 .settings(Defaults.itSettings : _*)
                 .settings(                    
    version                    := sqVersion,
    libraryDependencies       ++= nglsqDependencies,
    resolvers                  := nexus,
	  //publishArtifact in (Compile, packageDoc) := false,
    //publishArtifact in packageDoc := false,
    //sources in (Compile,doc)   := Seq.empty,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val nglsub = Project(appName + "-sub", file("app-ngl-sub"), settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version                    := subVersion,
    libraryDependencies       ++= nglsubDependencies,
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val nglassets = Project(appName + "-assets", file("app-ngl-asset"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		version                    := nglAssetsVersion,
		libraryDependencies        += guice,
		resolvers                  := nexus,
		publishArtifact in makePom := false,
		publishTo                  := Some(nexusigpublish)
  )
   
  val nglplates = Project(appName + "-plates", file("app-ngl-plaques"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
    version                    := nglPlatesVersion,
		libraryDependencies       ++= nglplaquesDependencies,	
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val ngldevguide = Project(appName + "-devguide", file("app-ngl-devguide"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(      
    version                    := nglDevGuideVersion,
		libraryDependencies       ++= ngldevguideDependencies,
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish) 
  ).dependsOn(nglcommon % "test->test;compile->compile")

  val nglprojects = Project(appName + "-projects", file("app-ngl-projects"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		version                    := projectsVersion,
		libraryDependencies       ++= nglprojectsDependencies,   
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")
	 
	val nglreagents = Project(appName + "-reagents", file("app-ngl-reagents"),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		version                    := reagentsVersion,
		libraryDependencies       ++= nglreagentsDependencies,   
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).dependsOn(nglcommon % "test->test;compile->compile", nglTesting % "test->test")

  val main = Project(appName, file("."),settings = buildSettings).enablePlugins(play.sbt.PlayJava).settings(
		version                    := nglVersion,			  
    resolvers                  := nexus,
    publishArtifact in makePom := false,
    publishTo                  := Some(nexusigpublish)
  ).aggregate(
    // libs
   	nglcommon,
   	nglframeworkweb,
   	ngldatatable,
    // applications
   	nglsq,       // 2.6
   	nglbi,       // 
   	nglassets,   // 2.6 - empty
   	nglplates,   // 2.6 - compiles, partial routes
   	ngldata,     // 2.6 - compiles, routes fail, test removed
   	nglsub,      // 2.6 - compiles, partial routes
   	nglreagents, // 2.6
   	nglprojects, // 2.6
    // play migration and testing
    nglPlayMigration,
   	nglTesting,
   	authentication
   	//,bcRoute,bcRouteCommon,bcRouteAuth
  )

  
  // TODO: remove dead code
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

}
