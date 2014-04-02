package controllers.main.tpl;


import java.util.List;

import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.dao.CodeLabelDAO;
import controllers.CommonController;
import jsmessages.JsMessages;
import play.api.modules.spring.Spring;
import play.i18n.Lang;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home ;


public class Main extends CommonController {

	
   public static Result home() {
	   return ok(home.render());
        
    }
   
}
