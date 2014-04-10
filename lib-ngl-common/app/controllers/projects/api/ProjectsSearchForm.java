package controllers.projects.api;

import java.util.Date;
import java.util.List;
import play.data.format.Formats.DateTime;
import controllers.ListForm;


public class ProjectsSearchForm extends ListForm {

	  public List<String> projectCodes;
	    public String projectCode;
	    
	    public List<String> stateCodes;
	    public String stateCode;

	    public List<String> typeCodes;

}

