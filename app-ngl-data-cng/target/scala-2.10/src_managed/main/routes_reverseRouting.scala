// @SOURCE:C:/Projets/NGL/app-ngl-data/conf/routes
// @HASH:838f0fa96ecd0453af91d508f08d645c971adff4
// @DATE:Wed Oct 09 15:18:31 CEST 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString


// @LINE:18
package controllers.description.experiments {

// @LINE:18
class ReverseExperiments {
    

// @LINE:18
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/experiments")
}
                                                
    
}
                          
}
                  

// @LINE:21
package controllers.description.runs {

// @LINE:21
class ReverseRuns {
    

// @LINE:21
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/runs")
}
                                                
    
}
                          
}
                  

// @LINE:24
// @LINE:6
package controllers {

// @LINE:6
class ReverseApplication {
    

// @LINE:6
def index(): Call = {
   Call("GET", _prefix)
}
                                                
    
}
                          

// @LINE:24
class ReverseAssets {
    

// @LINE:24
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          
}
                  

// @LINE:8
package controllers.description {

// @LINE:8
class ReverseAll {
    

// @LINE:8
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/all")
}
                                                
    
}
                          
}
                  

// @LINE:19
package controllers.description.processes {

// @LINE:19
class ReverseProcesses {
    

// @LINE:19
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/processes")
}
                                                
    
}
                          
}
                  

// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
package controllers.description.commons {

// @LINE:9
class ReverseStates {
    

// @LINE:9
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/commons/states")
}
                                                
    
}
                          

// @LINE:12
class ReverseLevels {
    

// @LINE:12
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/commons/levels")
}
                                                
    
}
                          

// @LINE:10
class ReverseResolutions {
    

// @LINE:10
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/commons/resolutions")
}
                                                
    
}
                          

// @LINE:11
class ReverseMeasures {
    

// @LINE:11
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/commons/measures")
}
                                                
    
}
                          
}
                  

// @LINE:15
package controllers.description.samples {

// @LINE:15
class ReverseSamples {
    

// @LINE:15
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/samples")
}
                                                
    
}
                          
}
                  

// @LINE:14
package controllers.description.projects {

// @LINE:14
class ReverseProjects {
    

// @LINE:14
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/projects")
}
                                                
    
}
                          
}
                  

// @LINE:17
package controllers.description.instruments {

// @LINE:17
class ReverseInstruments {
    

// @LINE:17
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/instruments")
}
                                                
    
}
                          
}
                  

// @LINE:16
package controllers.description.containers {

// @LINE:16
class ReverseContainers {
    

// @LINE:16
def save(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/description/containers")
}
                                                
    
}
                          
}
                  


// @LINE:18
package controllers.description.experiments.javascript {

// @LINE:18
class ReverseExperiments {
    

// @LINE:18
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.experiments.Experiments.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/experiments"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:21
package controllers.description.runs.javascript {

// @LINE:21
class ReverseRuns {
    

// @LINE:21
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.runs.Runs.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/runs"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:24
// @LINE:6
package controllers.javascript {

// @LINE:6
class ReverseApplication {
    

// @LINE:6
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        
    
}
              

// @LINE:24
class ReverseAssets {
    

// @LINE:24
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        
    
}
              
}
        

// @LINE:8
package controllers.description.javascript {

// @LINE:8
class ReverseAll {
    

// @LINE:8
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.All.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/all"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:19
package controllers.description.processes.javascript {

// @LINE:19
class ReverseProcesses {
    

// @LINE:19
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.processes.Processes.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/processes"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
package controllers.description.commons.javascript {

// @LINE:9
class ReverseStates {
    

// @LINE:9
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.commons.States.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/commons/states"})
      }
   """
)
                        
    
}
              

// @LINE:12
class ReverseLevels {
    

// @LINE:12
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.commons.Levels.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/commons/levels"})
      }
   """
)
                        
    
}
              

// @LINE:10
class ReverseResolutions {
    

// @LINE:10
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.commons.Resolutions.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/commons/resolutions"})
      }
   """
)
                        
    
}
              

// @LINE:11
class ReverseMeasures {
    

// @LINE:11
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.commons.Measures.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/commons/measures"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:15
package controllers.description.samples.javascript {

// @LINE:15
class ReverseSamples {
    

// @LINE:15
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.samples.Samples.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/samples"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:14
package controllers.description.projects.javascript {

// @LINE:14
class ReverseProjects {
    

// @LINE:14
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.projects.Projects.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/projects"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:17
package controllers.description.instruments.javascript {

// @LINE:17
class ReverseInstruments {
    

// @LINE:17
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.instruments.Instruments.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/instruments"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:16
package controllers.description.containers.javascript {

// @LINE:16
class ReverseContainers {
    

// @LINE:16
def save : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.description.containers.Containers.save",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/description/containers"})
      }
   """
)
                        
    
}
              
}
        


// @LINE:18
package controllers.description.experiments.ref {

// @LINE:18
class ReverseExperiments {
    

// @LINE:18
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.experiments.Experiments.save(), HandlerDef(this, "controllers.description.experiments.Experiments", "save", Seq(), "GET", """""", _prefix + """api/description/experiments""")
)
                      
    
}
                          
}
                  

// @LINE:21
package controllers.description.runs.ref {

// @LINE:21
class ReverseRuns {
    

// @LINE:21
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.runs.Runs.save(), HandlerDef(this, "controllers.description.runs.Runs", "save", Seq(), "GET", """""", _prefix + """api/description/runs""")
)
                      
    
}
                          
}
                  

// @LINE:24
// @LINE:6
package controllers.ref {

// @LINE:6
class ReverseApplication {
    

// @LINE:6
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq(), "GET", """ Home page""", _prefix + """""")
)
                      
    
}
                          

// @LINE:24
class ReverseAssets {
    

// @LINE:24
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          
}
                  

// @LINE:8
package controllers.description.ref {

// @LINE:8
class ReverseAll {
    

// @LINE:8
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.All.save(), HandlerDef(this, "controllers.description.All", "save", Seq(), "GET", """""", _prefix + """api/description/all""")
)
                      
    
}
                          
}
                  

// @LINE:19
package controllers.description.processes.ref {

// @LINE:19
class ReverseProcesses {
    

// @LINE:19
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.processes.Processes.save(), HandlerDef(this, "controllers.description.processes.Processes", "save", Seq(), "GET", """""", _prefix + """api/description/processes""")
)
                      
    
}
                          
}
                  

// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
package controllers.description.commons.ref {

// @LINE:9
class ReverseStates {
    

// @LINE:9
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.commons.States.save(), HandlerDef(this, "controllers.description.commons.States", "save", Seq(), "GET", """""", _prefix + """api/description/commons/states""")
)
                      
    
}
                          

// @LINE:12
class ReverseLevels {
    

// @LINE:12
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.commons.Levels.save(), HandlerDef(this, "controllers.description.commons.Levels", "save", Seq(), "GET", """""", _prefix + """api/description/commons/levels""")
)
                      
    
}
                          

// @LINE:10
class ReverseResolutions {
    

// @LINE:10
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.commons.Resolutions.save(), HandlerDef(this, "controllers.description.commons.Resolutions", "save", Seq(), "GET", """""", _prefix + """api/description/commons/resolutions""")
)
                      
    
}
                          

// @LINE:11
class ReverseMeasures {
    

// @LINE:11
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.commons.Measures.save(), HandlerDef(this, "controllers.description.commons.Measures", "save", Seq(), "GET", """""", _prefix + """api/description/commons/measures""")
)
                      
    
}
                          
}
                  

// @LINE:15
package controllers.description.samples.ref {

// @LINE:15
class ReverseSamples {
    

// @LINE:15
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.samples.Samples.save(), HandlerDef(this, "controllers.description.samples.Samples", "save", Seq(), "GET", """""", _prefix + """api/description/samples""")
)
                      
    
}
                          
}
                  

// @LINE:14
package controllers.description.projects.ref {

// @LINE:14
class ReverseProjects {
    

// @LINE:14
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.projects.Projects.save(), HandlerDef(this, "controllers.description.projects.Projects", "save", Seq(), "GET", """""", _prefix + """api/description/projects""")
)
                      
    
}
                          
}
                  

// @LINE:17
package controllers.description.instruments.ref {

// @LINE:17
class ReverseInstruments {
    

// @LINE:17
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.instruments.Instruments.save(), HandlerDef(this, "controllers.description.instruments.Instruments", "save", Seq(), "GET", """""", _prefix + """api/description/instruments""")
)
                      
    
}
                          
}
                  

// @LINE:16
package controllers.description.containers.ref {

// @LINE:16
class ReverseContainers {
    

// @LINE:16
def save(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.description.containers.Containers.save(), HandlerDef(this, "controllers.description.containers.Containers", "save", Seq(), "GET", """""", _prefix + """api/description/containers""")
)
                      
    
}
                          
}
                  
      