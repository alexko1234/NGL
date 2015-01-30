package processes;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.DatatableResponseForTest;
import utils.InitDataHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	protected static ALogger logger=Logger.of("ProcessesTest");
	
	@Test
	public void save() throws JsonParseException, JsonMappingException, IOException{
		Process process = ProcessTestHelper.getFakeProcess("mapping", "opgen-run");
		String supportCode = InitDataHelper.getSupportCodesInContext("tube").get(0);
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));		
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.containerInputCode = cs.code;
		process.comments = null;
		process.currentExperimentTypeCode = null;
		process.newContainerSupportCodes =null;
		process.experimentCodes = null;		
		cs.state.code="IW-P";
		//MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, cs);
		Logger.info("Avant Result save()");
		Result result = callAction(controllers.processes.api.routes.ref.Processes.save(),fakeRequest().withJsonBody(Json.toJson(process)));
		Logger.info("Après Result save()");
		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		ObjectMapper mapper = new ObjectMapper();
		Process processResult = mapper.readValue(play.test.Helpers.contentAsString(result), Process.class);
		assertThat(processResult).isNotNull();
		assertThat(processResult.state.code).isEqualTo("N");
		
		Process processFind =MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processResult.code);
		assertThat(processResult.code).isEqualTo(processFind.code);
		
		Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, processResult.containerInputCode);
		assertThat(container.processTypeCode).isEqualTo(processResult.typeCode);
		assertThat(container.inputProcessCodes.get(0)).isEqualTo(processResult.code);
		assertThat(container.state.code).isEqualTo("A");
		assertThat(container.fromExperimentTypeCodes).isNotNull();
		ContainerSupport containerSupport=MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);
		assertThat(containerSupport.state.code).isEqualTo("A");
		assertThat(containerSupport.fromExperimentTypeCodes).isNotNull();
		assertThat(container.inputProcessCodes.get(0)).isEqualTo(processResult.code);
		
		//result = callAction(controllers.processes.api.routes.ref.Processes.update(processResult.code),fakeRequest().withJsonBody(Json.toJson(processResult)));
		//assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.head(processResult.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.get(processResult.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		result = callAction(controllers.processes.api.routes.ref.Processes.delete(processResult.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);		
		;
	}
	
	@Test
	public void saveOpgenRun() throws JsonParseException, JsonMappingException, IOException{
		Process process = ProcessTestHelper.getFakeProcess("mapping", "opgen-run");
		String supportCode = InitDataHelper.getSupportCodesInContext("tube").get(3);  //jeu de donnée? Vérifier les projets
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));
		cs.state.code="IW-P";
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.properties = new HashMap<String, PropertyValue>();
		Logger.info("Avant Result saveOpgen()");
		Result result = callAction(controllers.processes.api.routes.ref.Processes.saveSupport(supportCode),fakeRequest().withJsonBody(Json.toJson(process)));
		Logger.info("Après Result saveOpgen()");
		ObjectMapper mapper = new ObjectMapper();		
		List<Process> processResult =mapper.readValue(play.test.Helpers.contentAsString(result),new TypeReference<List<Process>>(){});
		//List<Process> processResult = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("projectCode",  cs.projectCodes.get(0))).toList();
		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		assertThat(processResult).isNotNull();
		
		result = callAction(controllers.processes.api.routes.ref.Processes.head(processResult.get(0).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.get(processResult.get(0).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		for(int i=0; i<processResult.size();i++){
		result = callAction(controllers.processes.api.routes.ref.Processes.delete(processResult.get(i).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		}
		
		
	}

	
	@Test
	public void updateNotFound(){
		Result result = callAction(controllers.processes.api.routes.ref.Processes.update("not_found"),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
	}
	
	
	@Test
	public void deleteNotFound(){
		Result result = callAction(controllers.processes.api.routes.ref.Processes.delete("not_found"),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
	}
	
	@Test
	public void query() throws JsonParseException, JsonMappingException, IOException{
		Result result = null;
		
		result = callAction(controllers.processes.api.routes.ref.Processes.list(),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.list(),fakeRequest("GET","?projectCode=ADI"));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		ObjectMapper mapper=new ObjectMapper();
		List<Process> processes=mapper.readValue(play.test.Helpers.contentAsString(result),new TypeReference<List<Process>>(){});
		
		assertThat(processes.size()).isEqualTo(1);
		assertThat(processes.get(0).projectCode).isEqualTo("ADI");
		
		result = callAction(controllers.processes.api.routes.ref.Processes.list(),fakeRequest("GET","?datatable=true&projectCode=XXX"));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		mapper=new ObjectMapper();
		JsonNode jsonNode=Json.parse(play.test.Helpers.contentAsString(result));
		DatatableResponseForTest<Process> datatableResponse=mapper.convertValue(jsonNode,new TypeReference<DatatableResponseForTest<Process>>(){});
		assertThat(datatableResponse.data.size()).isEqualTo(0);
		
	}
}
