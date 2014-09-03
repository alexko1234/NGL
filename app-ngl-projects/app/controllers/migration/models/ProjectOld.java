package controllers.migration.models;

import models.laboratory.project.instance.Project;

public class ProjectOld extends Project {

	//for old compatibility
	public Boolean bioinformaticAnalysis = Boolean.FALSE; //move now under bioinformaticParameters	
		
		

}
