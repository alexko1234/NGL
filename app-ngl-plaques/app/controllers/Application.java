package controllers;

import java.util.List;

import ls.models.Manip;
import ls.services.LimsManipServices;
import play.api.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
  
   public static Result index() {
        return ok(index.render("Your new application is ready.","home"));
        
    }
   
  /*  public static Result index() {
        LimsManipServices  limsManipServices = Spring.getBeanOfType(LimsManipServices.class);
        List<Manip> results = limsManipServices.getManips(13,2);
        System.out.println("SIZE = "+results.size());

        return ok(index.render("Your new application is ready."));
  }*/

  
}
