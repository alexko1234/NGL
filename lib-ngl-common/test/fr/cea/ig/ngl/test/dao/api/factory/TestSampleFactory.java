package fr.cea.ig.ngl.test.dao.api.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

/**
 * @author ajosso
 *
 */
public class TestSampleFactory {
	
	public static Sample sample(String user) {
		Project proj = TestProjectFactory.project(user);
		return sample(user, proj);
	}
	
	public static Sample sample(String user, Project proj) {
		Sample s = new Sample();
		s.code = "Test_Sample";
		s.categoryCode = "default";
		s.importTypeCode = "default-import";
		s.name = "Sample de test";
		Set<String> pcodes = new TreeSet<>();
		pcodes.add(proj.code);
		s.projectCodes = pcodes;
		s.traceInformation = new TraceInformation(user);
		s.typeCode = "DNA";
		s.comments = comments(user);
		return s;
	}
	
	private static List<Comment> comments(String user){
		return Arrays.asList(new Comment("very usefull comments", user));
	}
}
