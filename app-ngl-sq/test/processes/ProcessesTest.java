package processes;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.InitDataHelper;
import fr.cea.ig.MongoDBDAO;

public class ProcessesTest extends AbstractTests{
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}
	
	@AfterClass
	public static void resetData(){
		InitDataHelper.endTest();
	}
	
	@Test
	public void save(){
		Process process = ProcessTestHelper.getFakeProcess("sequencing", "illumina-run");
		String supportCode = InitDataHelper.getSupportCodesInContext("flowcell-8").get(0);
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.properties = new HashMap<String, PropertyValue>();
		process.properties.put("sequencingType", new PropertySingleValue("Hiseq 2000"));
		process.properties.put("readType", new PropertySingleValue("PE"));
		process.properties.put("sequencerType", new PropertySingleValue("HISEQ2000"));
		process.properties.put("readLength", new PropertySingleValue("100"));
		Result result = callAction(controllers.processes.api.routes.ref.Processes.saveSupport(supportCode),fakeRequest().withJsonBody(Json.toJson(process)));
		List<Process> processResult = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("projectCode",  cs.projectCodes.get(0))).toList();
		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		assertThat(processResult).isNotNull();
		assertThat(processResult).isNotEmpty();
	}
}
