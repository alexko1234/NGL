package workflows;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.List;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import utils.AbstractTestsCNS;
import utils.RunMockHelper;
import play.mvc.Result;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunWorkflowsTest extends AbstractTestsCNS{

	static Run run;
	static ContainerSupport cs;
	static List<Container> containers;
	@BeforeClass
	public static void initData()
	{
		//get run to test
		run = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.is("state.code", "N")).toList().iterator().next();
		//Remove libProcessTypeCode for rule IP-S
		//Remove projectCodes/sampleCodes for rule IP-S
		run.properties.remove("libProcessTypeCodes");
		run.sampleCodes= new TreeSet<String>();
		run.projectCodes = new TreeSet<String>();
		
		//run.state.code="N";
		MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		
		//Get containerSupport
		cs = MongoDBDAO.findByCode("ngl_sq.ContainerSupport_dataWF", ContainerSupport.class, run.containerSupportCode);
		MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, cs);
		//Get container
		containers = MongoDBDAO.find("ngl_sq.Container_dataWF", Container.class, DBQuery.is("support.code", run.containerSupportCode)).toList();
		for(Container c : containers){
			MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
		}
		
	}
	
	@AfterClass
	public static void deleteData()
	{
		//get run to test
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		cs = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, cs.code);
		MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, cs);
		for(Container c : containers){
			c = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, c.code);
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, c);
		}
	}
	
	@Test
	public void checkData()
	{
		Logger.debug("CheckData");
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		assertThat(run).isNotNull();
		assertThat(run.state.code).isEqualTo("N");
	}
	
	@Test
	public void setStateIPS() throws InterruptedException
	{
		Logger.debug("setStateIPS");
		State state = new State("IP-S","bot");
		Result r = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
		assertThat(status(r)).isEqualTo(OK);
	
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		Logger.debug("state code"+run.state.code);
		assertThat(run.state.code).isEqualTo("IP-S");
	}
	
	@Test
	public void setStateFS()
	{
		State state = new State("F-S","bot");
		Result r = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
		assertThat(status(r)).isEqualTo(OK);
	
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		Logger.debug("state code"+run.state.code);
		assertThat(run.state.code).isEqualTo("IW-RG");
	}
	
	@Test
	public void setStateFRG()
	{
		State state = new State("F-RG","bot");
		Result r = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
		assertThat(status(r)).isEqualTo(OK);
	
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		Logger.debug("state code"+run.state.code);
		assertThat(run.state.code).isEqualTo("F-V");
	}
}
