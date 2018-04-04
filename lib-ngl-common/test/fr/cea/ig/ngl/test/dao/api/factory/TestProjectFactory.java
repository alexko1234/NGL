package fr.cea.ig.ngl.test.dao.api.factory;

import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.Project;

/**
 * Factory to create Project objects
 * @author ajosso
 *
 */
public class TestProjectFactory {
	
	public static Project project(String user) {
		return project(user, "default-project", "default");
	}

	public static Project projectArchived(String user) {
		Project p = project(user);
		p.archive = true;
		return p;
	}
	
	public static Project project(String user, String typeCode, String categoryCode) {
		Project project = new Project();
		project.name = "Project Test";
		project.typeCode = typeCode;
		project.categoryCode = categoryCode;
		project.description = "description";
		project.umbrellaProjectCode = null;
		project.lastSampleCode = "lastSampleCode";
		project.nbCharactersInSampleCode = 4;
		project.archive = Boolean.FALSE;
		project.state = new State("N", user);
		project.authorizedUsers = authorizedUsers(user);
		project.bioinformaticParameters = params();
		project.comments = comments(user);
		project.code = "TEST";
		
		project.traceInformation = traceInformation(user);
		/* TODO add values to these fields
		public Map<String, PropertyValue> properties;
		*/
		
		return project;
	}
	
	// Complex field constructors
	private static BioinformaticParameters params() {
		BioinformaticParameters params = new BioinformaticParameters();
		params.regexBiologicalAnalysis = "regexBiologicalAnalysis";
		params.mappingReference = "mappingReference";
		params.fgGroup = "fgGroup";
		params.fgPriority = 1;
		return params;
	}
	
	private static List<String> authorizedUsers(String user){
		return Arrays.asList(user);
	}
	
	private static List<Comment> comments(String user){
		return Arrays.asList(new Comment("very usefull comments", user));
	}
	
	private static TraceInformation traceInformation(String user) {
		return new TraceInformation(user);
	}
}
