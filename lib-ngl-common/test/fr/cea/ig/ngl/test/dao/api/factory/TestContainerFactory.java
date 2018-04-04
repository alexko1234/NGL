package fr.cea.ig.ngl.test.dao.api.factory;

import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

public class TestContainerFactory {

	public static Container container(String user, Project proj, Sample s) {
		Container c = new Container();
		c.code = "TEST";
		c.categoryCode = "tube";
		c.concentration = new PropertySingleValue(1.0, "ng/µl");
		c.volume = new PropertySingleValue(1.0, "µl");
		c.quantity = new PropertySingleValue(1.0, "ng");
		c.comments = comments(user);
		c.projectCodes.add(proj.code);
		c.sampleCodes.add(s.code);
		c.traceInformation = new TraceInformation(user);
		c.contents = contents(s);
		
		return c;
	}
	
	private static List<Content> contents(Sample s) {
		Content c = new Content(s.code, s.typeCode, s.categoryCode);
		return Arrays.asList(c);
	}

	private static List<Comment> comments(String user){
		return Arrays.asList(new Comment("very usefull comments", user));
	}
}
