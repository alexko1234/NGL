package fr.cea.ig.ngl.test.dao.api.factory;

import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

public class TestContainerFactory {

	public static Container container(String user, Double vol, Double quantity, Project proj, Sample s, ContainerSupport containerSupport) {
		Container c = new Container();
		c.code = "TEST";
		c.categoryCode = "tube";
		c.concentration = new PropertySingleValue(quantity/vol, "ng/µl");
		c.volume = new PropertySingleValue(vol, "µl");
		c.quantity = new PropertySingleValue(quantity, "ng");
		c.comments = comments(user);
		c.projectCodes.add(proj.code);
		c.sampleCodes.add(s.code);
		c.traceInformation = new TraceInformation(user);
		c.contents = contents(s, proj.code);
		c.support = support(containerSupport);
		return c;
	}
	
	private static LocationOnContainerSupport support(ContainerSupport containerSupport) {
		LocationOnContainerSupport s = new LocationOnContainerSupport();
		s.code = containerSupport.code;
		s.categoryCode = containerSupport.categoryCode;
		return s;
	}

	private static List<Content> contents(Sample s, String projectCode) {
		Content c = new Content(s.code, s.typeCode, s.categoryCode);
		c.percentage = new Double(100);
		c.projectCode = projectCode;
		return Arrays.asList(c);
	}

	private static List<Comment> comments(String user){
		return Arrays.asList(new Comment("very usefull comments", user));
	}
	
	
	public static ContainerSupport containerSupport(String user, Project proj, Sample s) {
		ContainerSupport cs = new ContainerSupport();
		cs.code = "TEST";
		cs.categoryCode = "tube";
		cs.projectCodes.add(proj.code);
		cs.comments = comments(user);
		cs.sampleCodes.add(s.code);
		cs.nbContainers = 1;
		cs.nbContents = 1;
		//cs.storageCode = "Bt20_70_A1";
		return cs;
	}
}
