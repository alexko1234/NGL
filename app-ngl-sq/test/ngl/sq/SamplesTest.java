package ngl.sq;


import static fr.cea.ig.play.test.DevAppTesting.cr;
import static fr.cea.ig.play.test.DevAppTesting.newCode;
import static fr.cea.ig.play.test.DevAppTesting.rurNeqTraceInfo;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static ngl.sq.SampleFactory.from;
import static ngl.sq.SampleFactory.fresh;
import static ngl.sq.SampleFactory.create_00;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.WSHelper;
import models.laboratory.sample.instance.Sample;

public class SamplesTest extends AbstractSQServerTest {

	private static final String samplesUrl = "/api/samples";
	
	@Test
	public void testCreation_00() {
		JsonNode sample_0 = create_00(newCode());
		cr(ws,samplesUrl,sample_0);
		rurNeqTraceInfo(ws,samplesUrl,sample_0);
	}

	@Test
	public void testTemplateFail() {
		// Template data has the id and the creation date that will
		// make the creation fail. We leave the id that fails.
		Sample sample = from("data/sample_00");
		WSHelper.postObject(ws,samplesUrl,sample,BAD_REQUEST);
	}
	
	@Test
	public void testTraceInfoFail() {
		// Template data has the id and the creation date that will
		// make the creation fail.
		Sample sample = from("data/sample_00");
		sample._id = null;
		WSHelper.postObject(ws,samplesUrl,sample,BAD_REQUEST);
	}
	
	@Test
	public void testFresh() {
		Sample sample = fresh(from("data/sample_00"));
		WSHelper.postObject(ws,samplesUrl,sample,OK);
	}
	
	// We would expect this to fail as there is no taxon code defined.
	// Could be correct though.
	// @Test
	public void testNoTaxon() {
		Sample sample = fresh(from("data/sample_00"));
		sample.taxonCode = null;
		WSHelper.postObject(ws,samplesUrl,sample,BAD_REQUEST);
	}
	
}
