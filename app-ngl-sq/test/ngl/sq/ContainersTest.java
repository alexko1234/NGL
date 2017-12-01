package ngl.sq;

import static fr.cea.ig.play.test.DevAppTesting.cr;
import static fr.cea.ig.play.test.DevAppTesting.newCode;
import static fr.cea.ig.play.test.DevAppTesting.rurNeqTraceInfo;
import static fr.cea.ig.play.test.DevAppTesting.savage;
import static ngl.sq.SampleFactory.samplesUrl;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.DevAppTesting;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;

public class ContainersTest extends AbstractSQServerTest {

	private static final String containersUrl = "/api/containers";
	
	@Test
	public void testCreation() {
		// Need to create a new sample, or fetch one
		Sample sample = SampleFactory.freshInstance(ws, SampleFactory.res_00);
		Container container = ContainerFactory.freshInstance(ws,ContainerFactory.res_00,sample);
		// Check that the container instance at least passes a RUR test.  
		DevAppTesting.rurNeqTraceInfo(ws, containersUrl, container);
	}

	@Test
	public void testNoStateTransition() {
		Sample sample = SampleFactory.freshInstance(ws, SampleFactory.res_00);
		Container container = ContainerFactory.freshInstance(ws,ContainerFactory.res_00,sample);
		container = WSHelper.getObject(ws,containersUrl + "/" + container.getCode(),Container.class);
		container.state.code = "ZZZZ";
		WSHelper.putObject(ws,containersUrl + "/" + container.getCode(),container,BAD_REQUEST);
	}
	
}
