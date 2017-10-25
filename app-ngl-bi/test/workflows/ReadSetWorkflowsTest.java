package workflows;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;
import utils.AbstractTestsCNS;
import utils.RunMockHelper;

public class ReadSetWorkflowsTest extends AbstractTestsCNS{

	static ReadSet readSet;
	
	@BeforeClass
	public static void initData()
	{
		readSet = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("state.code","N"), getReadSetKeys()).limit(1).toList().get(0);
		readSet.sampleOnContainer=null;
		
		MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
	}
	
	@AfterClass
	public static void deleteData()
	{
		//get readSet to test
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
	}
	
	
	@Test
	public void setStateIPRG()
	{
		Logger.debug("setStateIPRG");
		State state = new State("IP-RG","bot");
		Result r = callAction(controllers.readsets.api.routes.ref.ReadSets.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(state)).withHeader("User-Agent", "bot"));
		assertThat(status(r)).isEqualTo(OK);
	
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		Logger.debug("state code"+run.state.code);
		assertThat(run.state.code).isEqualTo("IP-S");
	}
	
	
	private static BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
	}
}
