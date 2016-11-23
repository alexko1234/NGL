package controllers.projects.api;

import java.util.List;
import java.util.Set;

import controllers.ListForm;


public class ProjectsSearchForm extends ListForm {

	public List<String> projectCodes;
	public String projectCode;

	public List<String> stateCodes;
	public String stateCode;

	public List<String> typeCodes;
	
	 public Set<String> existingFields, notExistingFields;

}

