package containers;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.instance.Process;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.Constants;
import utils.ContainerBatchElementHelper;
import utils.InitDataHelper;
import utils.MapperHelper;
import validation.ContextValidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import controllers.containers.api.ContainerBatchElement;
import controllers.containers.api.Containers;
import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;

public class ContainerTest extends AbstractTests {

	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}
	
	@AfterClass
	public static void resetData(){
		InitDataHelper.endTest();
	}
	
	
	protected static ALogger logger=Logger.of("ContainerTest");
	
	@Test
	public void validateCalculPercentageContent() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Container cnt =  ContainerTestHelper.getFakeContainer();
		
		//validate good ContentPercentage values
		
		//good value
		Content c1 = new Content();
		c1.percentage = 2.00;	
		cnt.contents.add(c1);		
		
		//null value
		Content c2 = new Content();
		c2.percentage = null;
		cnt.contents.add(c2);
		
		//empty value
		Content c3 = new Content();			
		cnt.contents.add(c3);
		
		//Float value greater than 100
		Content c4 = new Content();
		c4.percentage = 300.0;	
		cnt.contents.add(c4);
		
		//Float value less than 0		
		Content c5 = new Content();
		c5.percentage = 40.99;	
		cnt.contents.add(c5);
		
		//Float value between 0 and 1
		Content c6 = new Content();
		c6.percentage = 0.45;	
		cnt.contents.add(c6);
		
		Content c7 = new Content();
		c7.percentage = 0.03;	
		cnt.contents.add(c7);
		
		
		
		//good given ContentPercentage
		ContainerHelper.calculPercentageContent(cnt.contents, 80.0);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(1.6);
		assertThat(c2.percentage).isEqualTo(80.0);
		assertThat(c3.percentage).isEqualTo(80.0);
		assertThat(c4.percentage).isEqualTo(240.0);	
		assertThat(c5.percentage).isEqualTo(32.79);
		assertThat(c6.percentage).isEqualTo(0.36);
		assertThat(c7.percentage).isEqualTo(0.02);
		
		
		//Big Percentage Content
		c1.percentage = 10.0;
		c2.percentage = null;
		Content c8 = new Content();
		cnt.contents.add(c8);
		c5.percentage = 0.94;
		c6.percentage = 0.98;		
		ContainerHelper.calculPercentageContent(cnt.contents, 380.0);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(38.0);
		assertThat(c2.percentage).isEqualTo(380.0);
		assertThat(c8.percentage).isEqualTo(380.0);
		assertThat(c5.percentage).isEqualTo(3.57);
		assertThat(c6.percentage).isEqualTo(3.72);
		
		//Zero Percentage Content
		c1.percentage = 10.0;
		c2.percentage = null;
		Content c9 = new Content();
		cnt.contents.add(c9);
		c5.percentage = 0.94;
		c6.percentage = 0.98;
		ContainerHelper.calculPercentageContent(cnt.contents, 0.00);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(0.00);
		assertThat(c2.percentage).isEqualTo(0.00);
		assertThat(c9.percentage).isEqualTo(0.00);
		assertThat(c5.percentage).isEqualTo(0.00);
		assertThat(c6.percentage).isEqualTo(0.00);		
	}
	
	@Test
	public void validateGetCurrentProcesses() {		
		Container cnt =  ContainerTestHelper.getFakeContainer("tube");
		cnt.inputProcessCodes=new ArrayList<String>();
		Process process=new Process();
		process.code="validateGetCurrentProcesses";
		Process p=MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME,process);
		cnt.inputProcessCodes.add("validateGetCurrentProcesses");
		List<Process> processes =  cnt.getCurrentProcesses();
		MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, p);		
		assertThat(processes).isNotNull();
		assertThat(processes).isNotEmpty();		
	}
	
	@Test
	public void validateGetNullCurrentProcesses() {		
		Container cnt =  ContainerTestHelper.getFakeContainer();
		cnt.inputProcessCodes = null;
		assertThat(cnt.getCurrentProcesses()).isNullOrEmpty();				
	}
	
	@Test
	public void valideGet() {			
		Container c = MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, ContainerTestHelper.getFakeContainerWithCode("valideGet"));		
		assertThat(status(Containers.get("valideGet"))).isEqualTo(play.mvc.Http.Status.OK);			
		MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, c);		
	}
	
	@Test
	public void valideGetNotFound() {		
		assertThat(status(Containers.get("not found"))).isEqualTo(play.mvc.Http.Status.NOT_FOUND);		
		
	}
	
	@Test
	public void validateHead() {			
		Container c = MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, ContainerTestHelper.getFakeContainerWithCode("validateHead"));		
		assertThat(status(Containers.head("validateHead"))).isEqualTo(play.mvc.Http.Status.OK);		
		MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, c);
	}
	
	@Test
	public void validateHeadNotFound() {		
		assertThat(status(Containers.head("Not found"))).isEqualTo(play.mvc.Http.Status.NOT_FOUND);			
	}
	
	@Test
	public void validateUpdateBatch() {				
		Container c1 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_1");				
		Container c2 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_2");			
		Container c3 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_3");		
		List<ContainerBatchElement> ld = ContainerTestHelper.getFakeListContainerBatchElements(c1,c2,c3);		
		Result result = callAction(controllers.containers.api.routes.ref.Containers.updateBatch(), fakeRequest().withJsonBody(Json.toJson((ld))));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void validateUpdateBatchBadRequestWithNull() throws JsonProcessingException {
		
		Container c1 = null;			
		Container c2 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_2");			
		Container c3 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_3");			
		List<ContainerBatchElement> lc = ContainerTestHelper.getFakeListContainerBatchElements(c1,c2,c3);	
		Result result = callAction(controllers.containers.api.routes.ref.Containers.updateBatch(), fakeRequest().withJsonBody(Json.toJson(lc)));		
		List<DatatableBatchResponseElementForTest<Container>> ld = ContainerBatchElementHelper.getElementListObjectMapper(result);
		DatatableBatchResponseElementForTest<Container> db1 = ld.get(0);
		DatatableBatchResponseElementForTest<Container> db2 = ld.get(1);
		DatatableBatchResponseElementForTest<Container> db3 = ld.get(2);
		Logger.debug("");
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		assertThat(db1.status).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
		assertThat(db2.status).isEqualTo(play.mvc.Http.Status.OK);
		assertThat(db3.status).isEqualTo(play.mvc.Http.Status.OK);
			
	}
	
	@Test
	public void validateUpdateBatchBadRequestWithErrors() {
		
		//TODO: validation d'un status de container null pour BAD REQUEST
		//Container c1 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_5");
		//c1.state.code = null;	
		Container c1 = null;
		Container c2 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_6");
		c2.code = "Error";
		Container c3 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,"C2EV3ACXX_7");		
		List<ContainerBatchElement> lc = ContainerTestHelper.getFakeListContainerBatchElements(c1,c2,c3);	
		Result result = callAction(controllers.containers.api.routes.ref.Containers.updateBatch(), fakeRequest().withJsonBody(Json.toJson(lc)));		
		List<DatatableBatchResponseElementForTest<Container>> ld = ContainerBatchElementHelper.getElementListObjectMapper(result);
		DatatableBatchResponseElementForTest<Container> db1 = ld.get(0);
		DatatableBatchResponseElementForTest<Container> db2 = ld.get(1);
		DatatableBatchResponseElementForTest<Container> db3 = ld.get(2);
		Logger.debug("");
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		//assertThat(db1.status).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
		assertThat(db1.status).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
		assertThat(db2.status).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
		assertThat(db3.status).isEqualTo(play.mvc.Http.Status.OK);		
	}	
	
	@Test
	public void validateUpdate() {		
		String code = "C2EV3ACXX_8";
		Container c1 = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,code);		
		c1.comments.add(new Comment("TEST UNITAIRE validateUpdate "));		
		Result result = callAction(controllers.containers.api.routes.ref.Containers.update(code),fakeRequest().withJsonBody(Json.toJson(c1)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void validateListWithDatatable() {
		ContainersSearchForm csf = ContainerTestHelper.getFakeContainersSearchForm();
		
		MapperHelper mh = new MapperHelper();
		Container c = new Container();
		List <Container> lc = new ArrayList<Container>();
		DatatableResponseForTest<Container> dr= new DatatableResponseForTest<Container>();
		
		//Test with projectCode (good projectCode)
		csf.datatable=true;
		csf.projectCode = "ADI";
		Result result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&projectCodes="+csf.projectCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
				
		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});		
		lc = dr.data;
		c = (Container) lc.get(0);
		assertThat("ADI").isIn(c.projectCodes);			
		
		//Test with projectCode (bad projectCode)
		csf.projectCode = "validateListWithDatatableBadRequest";
		result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&projectCodes="+csf.projectCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});		
		lc = dr.data;		
		assertThat(lc).isNullOrEmpty();
		

		//Test with samples (good request)
		c = ContainerTestHelper.getFakeContainerWithCode("validateListWithDatatableContainer");
		Sample s1 = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,"AHX_AAV");
		Sample s2 = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,"AHX_AAQ");
		Sample s3 = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,"AHX_AAS");		
		String projectCode = s1.projectCodes.get(0);		
		result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&projectCodes="+projectCode+"&sampleCodes="+s1.code+"&sampleCodes="+s2.code+"&sampleCodes="+s3.code));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});		
		lc = dr.data;
		for(int i=0;i<lc.size();i++){
			c = (Container) lc.get(i);
			for(int j=0;j<c.sampleCodes.size();j++){
				assertThat(c.sampleCodes.get(j)).matches("AHX_.*");
			}
			
		}
		
		//Test with samples (bad request)
		String projectBadCode = "validateListWithDatatableContainerBadprojectCode";
		String st1="validateListWithDatatableContainerBadsampleCode1";
		String st2="validateListWithDatatableContainerBadsampleCode2";
		String st3="validateListWithDatatableContainerBadsampleCode3";
		result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&projectCodes="+projectBadCode+"&sampleCodes="+st1+"&sampleCodes="+st2+"&sampleCodes="+st3));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});		
		lc = dr.data;
		for(int i=0;i<lc.size();i++){
			c = (Container) lc.get(i);
			for(int j=0;j<c.sampleCodes.size();j++){
				assertThat(c.sampleCodes.get(j)).doesNotMatch("validateListWithDatatable.*");
			}

		}		

		//Test with dates (matched period)
		csf.fromDate = new Date(114, 2, 20) ;
		csf.toDate = new Date(114, 2, 20) ;		
		result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&fromDate="+csf.fromDate.getTime()+"&toDate="+csf.toDate.getTime()));		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});
		lc = dr.data;		
		c = (Container) lc.get(0);
		assertThat(c.code).isEqualTo("ADI_RD1");		
		
		//Test with dates (unmatched period)
		csf.fromDate = new Date(114, 0, 1) ;
		csf.toDate = new Date(114, 2, 19) ;		
		result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, "?datatable="+String.valueOf(csf.datatable)+"&fromDate="+csf.fromDate.getTime()+"&toDate="+csf.toDate.getTime()));		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dr = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<Container>>(){});
		lc = dr.data;
		Logger.info("");
		for(int i=0;i<lc.size();i++){
			c = (Container) lc.get(i);
			assertThat(c.code).isNotEqualTo("ADI_RD1");	
		}	
		
		
	}
	
	/*
	 * Use in a outer project
	 * 
	 * 
	@Test
	public void validateListWithCount() {
		ContainersSearchForm csf = ContainerTestHelper.getFakeContainersSearchForm();
		csf.count = true;
		Result result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, "?datatable=false&count="+csf.count));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
	}
	*/
	
	@Test
	public void validateListWithList() {
		ContainersSearchForm csf = ContainerTestHelper.getFakeContainersSearchForm();		
		List <Container> lc = new ArrayList<Container>();
		csf.datatable=false;
		csf.code="";
		
		//result = callAction(controllers.containers.api.routes.ref.Containers.list(), fakeRequest( play.test.Helpers.GET, );
		
		
		
	}
	
	@Test
	public void validateList() {
		ContainersSearchForm csf = ContainerTestHelper.getFakeContainersSearchForm();
		
	}
	
	@Test
	public void validateList_supports() {
		
	}
	
	@Test
	public void validateGetQuery() {
		
	}	
	
	
	/*
	@Test
	public void test() {
		
	}
	*/

}
