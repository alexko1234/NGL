package fr.cea.ig.ngl.test.dao.api.factory;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.Project;

public class TestProjectFactory {
	
	// List of objects for tests
	// force test dev using final objects 
	
	/**
	 * Use as reference in tests
	 */
	public final Project project;
	/**
	 * Use as reference in tests
	 */
	public final Project projectArchived;
	
	@Inject
	public TestProjectFactory() {
		project = project("ngsrg");
		projectArchived = projectArchived("ngsrg");
	}
	
	private static Project project(String user) {
		Project project = new Project();
		project.name = "Project Test";
		project.typeCode = "default-project";
		project.categoryCode = "default";
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
		public TraceInformation traceInformation;
		public Map<String, PropertyValue> properties;
		*/
		
		return project;
	}

	private static Project projectArchived(String user) {
		Project p = project(user);
		p.archive = true;
		return p;
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
