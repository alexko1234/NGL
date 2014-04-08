package controllers.projects.api;

import net.vz.mongodb.jackson.DBQuery;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ProjectsController extends CommonController {

    protected static Project getProject(String code) {
	Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, code);
	return project;
    }

    protected static Project getProject(String code, String...keys) {
	MongoDBResult<Project> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", code), getIncludeKeys(keys));
	if(projects.size() == 1)
	    return projects.toList().get(0);
	else
	    return null;
    }
    
    protected static TraceInformation getUpdateTraceInformation(Project project) {
		TraceInformation ti = project.traceInformation;
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	

}
