package processes;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.instance.Process;
import models.laboratory.processes.instance.SampleOnInputContainer;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.instance.ProcessHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.Constants;
import utils.DatatableResponseForTest;
import utils.InitDataHelper;
import utils.MapperHelper;
import validation.ContextValidation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.processes.api.ProcessesSaveQueryForm;
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
/*
	@Test
	public void validateSave() throws JsonParseException, JsonMappingException, IOException{
		String supportCode = InitDataHelper.getSupportCodesInContext("tube").get(0);		
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));			
		cs.state.code="IW-P";
		MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, cs);		
		Process process = ProcessTestHelper.getFakeProcess("mapping", "opgen-run");
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.containerInputCode = cs.code;
		process.comments = null;
		process.currentExperimentTypeCode = null;
		process.newContainerSupportCodes =null;
		process.experimentCodes = null;				
		ProcessesSaveQueryForm psf = ProcessTestHelper.getFakeProcessesSaveForm(supportCode, process);		
		Logger.info("Avant Result save()");
		Result result = callAction(controllers.processes.api.routes.ref.Processes.save(),fakeRequest().withJsonBody(Json.toJson(psf)));
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
	*/
	
/*	@Test
	public void validatesaveFromSupportOpgenRun() throws JsonParseException, JsonMappingException, IOException{
		String supportCode = InitDataHelper.getSupportCodesInContext("tube").get(0);		
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));		
		cs.state.code="IW-P";
		Process process = ProcessTestHelper.getFakeProcess("mapping", "opgen-run");
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.properties = new HashMap<String, PropertyValue>();	
		ProcessesSaveQueryForm psf = ProcessTestHelper.getFakeProcessesSaveForm(supportCode, process);
		//Process process = ProcessTestHelper.getFakeProcess(psf.categoryCode, psf.typeCode);
		
			
		Logger.info("Avant Result  validatesaveFromSupportOpgenRun()");
		Result result = callAction(controllers.processes.api.routes.ref.Processes.save(),fakeRequest().withJsonBody(Json.toJson(psf)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);		
		Logger.info("Après Result  validatesaveFromSupportOpgenRun()");
		
		MapperHelper mh = new MapperHelper();		
		List<Process> processResult = mh.convertValue(mh.resultToJsNode(result), new TypeReference<List<Process>>(){});		
		
		assertThat(processResult).isNotNull();
		
		result = callAction(controllers.processes.api.routes.ref.Processes.head(processResult.get(0).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.get(processResult.get(0).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		for(int i=0; i<processResult.size();i++){
		result = callAction(controllers.processes.api.routes.ref.Processes.delete(processResult.get(i).code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		}
		
		
	}*/
	
	@Test
	public void validateApplyRules(){
		List<Process> processes = new ArrayList<Process>();
		Process process = ProcessTestHelper.getFakeProcess("sequencing", "illumina-run");	
		
		process.properties = new HashMap<String, PropertyValue>();
		Double estimatedPercentValue = 33.0;
		process.properties.put("estimatedPercentPerLane", new PropertySingleValue(estimatedPercentValue));
		process.sampleOnInputContainer = new SampleOnInputContainer();
		process.sampleOnInputContainer.percentage = 25.0 ;
		process.properties.put("sequencingType", new PropertySingleValue("Miseq"));
		process.properties.put("readType", new PropertySingleValue("PE"));
		process.properties.put("readLength", new PropertySingleValue("300"));
		String supportCode = InitDataHelper.getSupportCodesInContext("flowcell-8").get(0);
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));	
		cs.state.code="IW-P";
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		
		process.code = CodeHelper.generateProcessCode(process);
		Process pro1 = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
		processes.add(pro1);
		
		process.code = CodeHelper.generateProcessCode(process);
		Process pro2 = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
		processes.add(pro2);
		
		process.code = CodeHelper.generateProcessCode(process);
		Process pro3 = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
		processes.add(pro3);
		
		process.code = CodeHelper.generateProcessCode(process);
		Process pro4 = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
		processes.add(pro4);
		
		
		
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		contextValidation.setCreationMode();
		
		Double newValue = roundValue(process.sampleOnInputContainer.percentage*estimatedPercentValue/100.0);		
			
		Logger.info("Test validateApplyRules begin............");
		ProcessHelper.applyRules(processes, contextValidation,"processCreation");
		Logger.info(".............Test validateApplyRules end!");
		
		assertThat(pro1.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		assertThat(pro2.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		assertThat(pro3.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		assertThat(pro4.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		
		Process p = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, pro1.code);
		assertThat(p.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);	
		
		p = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, pro2.code);
		assertThat(p.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		
		p = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, pro3.code);
		assertThat(p.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		
		p = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, pro3.code);
		assertThat(p.properties.get("estimatedPercentPerLane").value).isEqualTo(newValue);
		
		
	}
	
	
/*	
	@Test
	public void validatesaveFromSupportIlluminaRun() throws JsonParseException, JsonMappingException, IOException{		
		String supportCode = InitDataHelper.getSupportCodesInContext("tube").get(0);
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));		
		cs.state.code="IW-P";
		Process process = ProcessTestHelper.getFakeProcess("sequencing", "illumina-run");
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.properties = new HashMap<String, PropertyValue>();
		process.properties.put("sequencingType", new PropertySingleValue("Miseq"));
		process.properties.put("readType", new PropertySingleValue("PE"));
		process.properties.put("readLength", new PropertySingleValue("300"));
		process.properties.put("estimatedPercentPerLane", new PropertySingleValue(2.5));		
		ProcessesSaveQueryForm psf = ProcessTestHelper.getFakeProcessesSaveForm(supportCode, process);		
		
		Logger.info("Avant Result  validatesaveFromSupportOpgenRun()");
		Result result = callAction(controllers.processes.api.routes.ref.Processes.save(),fakeRequest().withJsonBody(Json.toJson(psf)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);		
		Logger.info("Après Result  validatesaveFromSupportOpgenRun()");
		
		MapperHelper mh = new MapperHelper();		
		List<Process> processResult = mh.convertValue(mh.resultToJsNode(result), new TypeReference<List<Process>>(){});		
		assertThat(processResult).isNotNull();		
		
		for(Process p:processResult){		
		assertThat(p.properties.get("estimatedPercentPerLane").value).isEqualTo(roundValue(p.sampleOnInputContainer.percentage*2.5/100.0));	
			
		result = callAction(controllers.processes.api.routes.ref.Processes.delete(p.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		}
		
	}*/
	
	@Test
	public void validateCloneProcessProperties(){
		Process process = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("typeCode","illumina-run")).toList().get(0);
		Process p =  new Process();
		p.properties = new HashMap<String, PropertyValue>();
		p.properties = ProcessHelper.cloneProcessProperties(process);
		Iterator<String> i = process.properties.keySet().iterator();
		while(i.hasNext()){
			String s = i.next();
			assertThat(process.properties.get(s)).isEqualTo(p.properties.get(s));
			assertThat(process.properties.get(s).value).isEqualTo(p.properties.get(s).value);
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
	
	private Double roundValue(Double value)
	{
		BigDecimal bg = new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP); 
		return bg.doubleValue();
	}
}
