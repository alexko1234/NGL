package controllers.migration.models;

import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.project.instance.Project;

public class ProjectOld5 extends Project {
	public Boolean bioinformaticAnalysis = Boolean.FALSE;	
	
	public List<String> umbrellaProjectCodes; 
	
	public List<Comment> comments; 
}
