// @SOURCE:C:/Projets/NGL/app-ngl-data/conf/routes
// @HASH:838f0fa96ecd0453af91d508f08d645c971adff4
// @DATE:Wed Oct 09 15:18:31 CEST 2013


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:6
private[this] lazy val controllers_Application_index0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:8
private[this] lazy val controllers_description_All_save1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/all"))))
        

// @LINE:9
private[this] lazy val controllers_description_commons_States_save2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/commons/states"))))
        

// @LINE:10
private[this] lazy val controllers_description_commons_Resolutions_save3 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/commons/resolutions"))))
        

// @LINE:11
private[this] lazy val controllers_description_commons_Measures_save4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/commons/measures"))))
        

// @LINE:12
private[this] lazy val controllers_description_commons_Levels_save5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/commons/levels"))))
        

// @LINE:14
private[this] lazy val controllers_description_projects_Projects_save6 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/projects"))))
        

// @LINE:15
private[this] lazy val controllers_description_samples_Samples_save7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/samples"))))
        

// @LINE:16
private[this] lazy val controllers_description_containers_Containers_save8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/containers"))))
        

// @LINE:17
private[this] lazy val controllers_description_instruments_Instruments_save9 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/instruments"))))
        

// @LINE:18
private[this] lazy val controllers_description_experiments_Experiments_save10 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/experiments"))))
        

// @LINE:19
private[this] lazy val controllers_description_processes_Processes_save11 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/processes"))))
        

// @LINE:21
private[this] lazy val controllers_description_runs_Runs_save12 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/description/runs"))))
        

// @LINE:24
private[this] lazy val controllers_Assets_at13 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/all""","""controllers.description.All.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/commons/states""","""controllers.description.commons.States.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/commons/resolutions""","""controllers.description.commons.Resolutions.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/commons/measures""","""controllers.description.commons.Measures.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/commons/levels""","""controllers.description.commons.Levels.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/projects""","""controllers.description.projects.Projects.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/samples""","""controllers.description.samples.Samples.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/containers""","""controllers.description.containers.Containers.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/instruments""","""controllers.description.instruments.Instruments.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/experiments""","""controllers.description.experiments.Experiments.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/processes""","""controllers.description.processes.Processes.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/description/runs""","""controllers.description.runs.Runs.save()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Application_index0(params) => {
   call { 
        invokeHandler(controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Nil,"GET", """ Home page""", Routes.prefix + """"""))
   }
}
        

// @LINE:8
case controllers_description_All_save1(params) => {
   call { 
        invokeHandler(controllers.description.All.save(), HandlerDef(this, "controllers.description.All", "save", Nil,"GET", """""", Routes.prefix + """api/description/all"""))
   }
}
        

// @LINE:9
case controllers_description_commons_States_save2(params) => {
   call { 
        invokeHandler(controllers.description.commons.States.save(), HandlerDef(this, "controllers.description.commons.States", "save", Nil,"GET", """""", Routes.prefix + """api/description/commons/states"""))
   }
}
        

// @LINE:10
case controllers_description_commons_Resolutions_save3(params) => {
   call { 
        invokeHandler(controllers.description.commons.Resolutions.save(), HandlerDef(this, "controllers.description.commons.Resolutions", "save", Nil,"GET", """""", Routes.prefix + """api/description/commons/resolutions"""))
   }
}
        

// @LINE:11
case controllers_description_commons_Measures_save4(params) => {
   call { 
        invokeHandler(controllers.description.commons.Measures.save(), HandlerDef(this, "controllers.description.commons.Measures", "save", Nil,"GET", """""", Routes.prefix + """api/description/commons/measures"""))
   }
}
        

// @LINE:12
case controllers_description_commons_Levels_save5(params) => {
   call { 
        invokeHandler(controllers.description.commons.Levels.save(), HandlerDef(this, "controllers.description.commons.Levels", "save", Nil,"GET", """""", Routes.prefix + """api/description/commons/levels"""))
   }
}
        

// @LINE:14
case controllers_description_projects_Projects_save6(params) => {
   call { 
        invokeHandler(controllers.description.projects.Projects.save(), HandlerDef(this, "controllers.description.projects.Projects", "save", Nil,"GET", """""", Routes.prefix + """api/description/projects"""))
   }
}
        

// @LINE:15
case controllers_description_samples_Samples_save7(params) => {
   call { 
        invokeHandler(controllers.description.samples.Samples.save(), HandlerDef(this, "controllers.description.samples.Samples", "save", Nil,"GET", """""", Routes.prefix + """api/description/samples"""))
   }
}
        

// @LINE:16
case controllers_description_containers_Containers_save8(params) => {
   call { 
        invokeHandler(controllers.description.containers.Containers.save(), HandlerDef(this, "controllers.description.containers.Containers", "save", Nil,"GET", """""", Routes.prefix + """api/description/containers"""))
   }
}
        

// @LINE:17
case controllers_description_instruments_Instruments_save9(params) => {
   call { 
        invokeHandler(controllers.description.instruments.Instruments.save(), HandlerDef(this, "controllers.description.instruments.Instruments", "save", Nil,"GET", """""", Routes.prefix + """api/description/instruments"""))
   }
}
        

// @LINE:18
case controllers_description_experiments_Experiments_save10(params) => {
   call { 
        invokeHandler(controllers.description.experiments.Experiments.save(), HandlerDef(this, "controllers.description.experiments.Experiments", "save", Nil,"GET", """""", Routes.prefix + """api/description/experiments"""))
   }
}
        

// @LINE:19
case controllers_description_processes_Processes_save11(params) => {
   call { 
        invokeHandler(controllers.description.processes.Processes.save(), HandlerDef(this, "controllers.description.processes.Processes", "save", Nil,"GET", """""", Routes.prefix + """api/description/processes"""))
   }
}
        

// @LINE:21
case controllers_description_runs_Runs_save12(params) => {
   call { 
        invokeHandler(controllers.description.runs.Runs.save(), HandlerDef(this, "controllers.description.runs.Runs", "save", Nil,"GET", """""", Routes.prefix + """api/description/runs"""))
   }
}
        

// @LINE:24
case controllers_Assets_at13(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        
}
    
}
        