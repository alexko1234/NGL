package controllers.containers.tpl;

// import play.Routes;
import play.routing.JavaScriptReverseRouter;


import play.mvc.Result;
import views.html.containerSupports.details;
import views.html.containerSupports.home;
import views.html.containerSupports.homeScanner;
import views.html.containerSupports.search;

import javax.inject.Inject;

import controllers.CommonController;

// TODO: cleanup and comment

public class ContainerSupports extends CommonController{
	
	private final home home;
	private final details details;
	private final homeScanner homeScanner;
	
	@Inject
	public ContainerSupports(home home, details details, homeScanner homeScanner) {
		this.home = home;
		this.details = details;
		this.homeScanner = homeScanner;
	}
	
	public /*static*/ Result home(String code){		
		return ok(home.render(code));		
	}
	
	public /*static*/ Result homeScanner(){		
		return ok(homeScanner.render());		
	}
	
	public /*static*/ Result search(){
		return ok(search.render());
	}
	
	
	
	public /*static*/ Result get(String code){
		return ok(home.render("search"));
	}
	
	public /*static*/ Result details() {
		return ok(details.render());
	}
	
	
	public /*static*/ Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      //Routes.javascriptRouter("jsRoutes",
  	    		JavaScriptReverseRouter.create("jsRoutes",	  
  	        // Routes
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.search(),
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.home(),
  	    		controllers.printing.tpl.routes.javascript.Printing.home(),
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.homeScanner(),
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.details(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.list(),
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.get(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.get(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.update(),        // 26/05/2016 NLG-825
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.updateStateBatch(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.samples.api.routes.javascript.Samples.save(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),  	    		
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessCategories.list(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.commons.api.routes.javascript.Users.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	    		controllers.containers.tpl.routes.javascript.Containers.get()
  	      )	  	      
  	    );
  	}
}
