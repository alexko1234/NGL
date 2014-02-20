package controllers;

import java.util.ArrayList;
import java.util.List;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

import models.datatable.Tube;

public class Datatable extends Controller{

	
	public static Result getExamples(){
		List<Tube> tubes = new ArrayList<Tube>();
		tubes.add(new Tube("test1","20","N","AX_32"));
		tubes.add(new Tube("test2","40","IWP","AP_67"));
		tubes.add(new Tube("test3","60","IWP","KI_98"));
		
		return ok(Json.toJson(new DatatableResponse(tubes, tubes.size())));
	}
}
