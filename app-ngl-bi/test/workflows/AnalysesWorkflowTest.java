package workflows;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.bi.Global.devapp;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import utils.AbstractTests;
import utils.RunMockHelper;

public class AnalysesWorkflowTest extends AbstractTests {

	private static final play.Logger.ALogger logger = play.Logger.of(AnalysesWorkflowTest.class);
	
	static Analysis analysis;

	@BeforeClass
	public static void initData()
	{
		analysis = MongoDBDAO.find("ngl_bi.Analysis_dataWF", Analysis.class).toList().get(0);
		MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);

		for(String codeReadSet : analysis.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, codeReadSet);
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
	}

	@AfterClass
	public static void deleteData()
	{
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		MongoDBDAO.delete(InstanceConstants.ANALYSIS_COLL_NAME, analysis);
		for(String codeReadSet : analysis.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
	}

	@Test
	public void setStateIPBA()
	{
		testInServer(devapp(),
				ws -> {	
					logger.debug("setStateIPS");
					State state = new State("IP-BA","bot");
					//Result r = callAction(controllers.analyses.api.routes.ref.Analyses.state(analysis.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
					//assertThat(status(r)).isEqualTo(OK);
					WSHelper.putAsBot(ws, "/api/analyses/"+analysis.code+"/state", RunMockHelper.getJsonState(state).toString(), 200);
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					logger.debug("state code"+analysis.state.code);
					assertThat(analysis.state.code).isEqualTo("IP-BA");
					for(String codeReadSet : analysis.masterReadSetCodes){
						ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
						assertThat(readSet.state.code).isEqualTo("IP-BA");
					}
				});


	}

	@Test
	public void setStateFBA()
	{
		testInServer(devapp(),
				ws -> {	
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					analysis.valuation.valid=TBoolean.TRUE;
					MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);

					logger.debug("setStateFBA");
					State state = new State("F-BA","bot");
					//Result r = callAction(controllers.analyses.api.routes.ref.Analyses.state(analysis.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
					//assertThat(status(r)).isEqualTo(OK);
					WSHelper.putAsBot(ws, "/api/analyses/"+analysis.code+"/state", RunMockHelper.getJsonState(state).toString(), 200);
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					logger.debug("state code"+analysis.state.code);
					assertThat(analysis.state.code).isEqualTo("F-V");
					for(String codeReadSet : analysis.masterReadSetCodes){
						ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
						assertThat(readSet.state.code).isEqualTo("A");
					}
				});
	}

	@Test
	public void setStateIWV()
	{
		testInServer(devapp(),
				ws -> {	
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					analysis.valuation.valid=TBoolean.UNSET;
					MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);

					logger.debug("setStateFBA");
					State state = new State("IW-V","bot");
					//Result r = callAction(controllers.analyses.api.routes.ref.Analyses.state(analysis.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
					//assertThat(status(r)).isEqualTo(OK);
					WSHelper.putAsBot(ws, "/api/analyses/"+analysis.code+"/state", RunMockHelper.getJsonState(state).toString(), 200);

					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					logger.debug("state code"+analysis.state.code);
					assertThat(analysis.state.code).isEqualTo("IW-V");
					for(String codeReadSet : analysis.masterReadSetCodes){
						ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
						assertThat(readSet.state.code).isEqualTo("IW-VBA");
					}
				});
	}


	@Test
	public void setStateFV()
	{
		testInServer(devapp(),
				ws -> {	
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					analysis.valuation.valid=TBoolean.UNSET;
					MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);

					logger.debug("setStateFBA");
					State state = new State("IW-V","bot");
					//Result r = callAction(controllers.analyses.api.routes.ref.Analyses.state(analysis.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
					//assertThat(status(r)).isEqualTo(OK);
					WSHelper.putAsBot(ws, "/api/analyses/"+analysis.code+"/state", RunMockHelper.getJsonState(state).toString(), 200);
					
					analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
					logger.debug("state code"+analysis.state.code);
					assertThat(analysis.state.code).isEqualTo("IW-V");
					for(String codeReadSet : analysis.masterReadSetCodes){
						ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
						assertThat(readSet.state.code).isEqualTo("IW-VBA");
					}
				});
	}
	
}


