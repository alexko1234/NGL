package supports;

import static org.assertj.core.api.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.DatatableResponseForTest;
import utils.InitDataHelper;
import utils.MapperHelper;

import com.fasterxml.jackson.core.type.TypeReference;

import controllers.supports.api.Supports;
import controllers.supports.api.SupportsSearchForm;
import fr.cea.ig.MongoDBDAO;

public class SupportTest extends AbstractTests {

	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}
	
	@AfterClass
	public static void resetData(){
		InitDataHelper.endTest();
	}	
	
	protected static ALogger logger=Logger.of("SupportTest");
	
	/**********************************Tests of ContainerSupportHelper (DAO Helper)***************************************************/	
	
	@Test
	public void validateGetContainerSupportTube(){
		LocationOnContainerSupport locs = ContainerSupportHelper.getContainerSupportTube("TEST_GetContainerSupportTube");
		
		assertThat(locs.categoryCode).isEqualTo("tube");
		assertThat(locs.code).isEqualTo("TEST_GetContainerSupportTube");
		assertThat(locs.column).isEqualTo("1");
		assertThat(locs.line).isEqualTo("1");
	}
	
	@Test
	public void validationGetContainerSupport() {
		LocationOnContainerSupport locs = null;
		try {
			locs = ContainerSupportHelper
					.getContainerSupport("lane", 5, "TEST_GetContainerSupport","1","8");
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertThat(locs.categoryCode).isEqualTo("flowcell-8");
		assertThat(locs.code).isEqualTo("TEST_GetContainerSupport");
		assertThat(locs.column).isEqualTo("1");
		assertThat(locs.line).isEqualTo("8");		
	}
	
	@Test
	public void validateCreateSupport(){
		PropertySingleValue  testProperty = new PropertySingleValue("testValue");
		ContainerSupport cs = ContainerSupportHelper.createSupport("TEST_CreateSupport", testProperty, "flowcell-2", "TEST_User");
		
		assertThat(cs.code).isEqualTo("TEST_CreateSupport");
		assertThat(cs.categoryCode).isEqualTo("flowcell-2");
		assertThat(cs.state.code).isEqualTo("N");
		assertThat(cs.state.user).isEqualTo("TEST_User");
		Date d = new Date();
		
		assertThat(d).isEqualTo(cs.state.date);
		assertThat(cs.valuation.valid).isEqualTo(TBoolean.UNSET);		
	}
	
	/**********************************Tests of ContainerSupport class methods (DBObject)***************************************************/		
	//None			
	/**********************************Tests of Supports class methods (Controller)***************************************************/	
	
	@Test
	public void validateGet() {
		ContainerSupport cs = MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, SupportTestHelper.getFakeSupportWithCode("validateGetTEST"));
		assertThat(status(Supports.get("validateGetTEST"))).isEqualTo(play.mvc.Http.Status.OK);	
		MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME,cs);
	}
	
	@Test
	public void valideGetNotFound() {		
		assertThat(status(Supports.get("not found"))).isEqualTo(play.mvc.Http.Status.NOT_FOUND);		
	}
	
	@Test
	public void validateHead() {
		ContainerSupport cs = MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, SupportTestHelper.getFakeSupportWithCode("validateHeadTEST"));
		assertThat(status(Supports.head("validateHeadTEST"))).isEqualTo(play.mvc.Http.Status.OK);	
		MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME,cs);
	}
	
	@Test
	public void validateHeadNotFound() {		
		assertThat(status(Supports.head("Not found"))).isEqualTo(play.mvc.Http.Status.NOT_FOUND);			
	}
	
	@Test
	public void validateListWithDatatable() {
		SupportsSearchForm ssf = SupportTestHelper.getFakeSupportsSearchForm();
		DatatableResponseForTest<ContainerSupport> dcs = new DatatableResponseForTest<ContainerSupport>();
		List<ContainerSupport> lcs = new ArrayList<ContainerSupport>();
		MapperHelper mh = new MapperHelper();
		ssf.datatable = true;
		
		//Test with categoryCode (good categoryCode)
		ssf.categoryCode = "tube";
		Result result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&categoryCode="+ssf.categoryCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;		
		for(int i=0; i<lcs.size();i++){
			assertThat(lcs.get(i).categoryCode).isEqualTo("tube");
		}		
		
		//Test with categoryCode (bad categoryCode)
		ssf.categoryCode = "badCategoryCode";
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&categoryCode="+ssf.categoryCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;		
		assertThat(lcs).isNullOrEmpty();
		
		//Test with fromExperimentTypeCodes (good fromExperimentTypeCodes)		
		ssf.fromExperimentTypeCodes = new ArrayList<String>();
		ssf.fromExperimentTypeCodes.add("solution-stock");		
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&fromExperimentTypeCodes="+ssf.fromExperimentTypeCodes.get(0)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);	
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;
		Logger.info("");
		for(int i=0; i<lcs.size();i++){
			assertThat(lcs.get(i).fromExperimentTypeCodes).contains("solution-stock");
			Logger.info("");
		}	
		
		//Test with fromExperimentTypeCodes (bad fromExperimentTypeCodes)
		ssf.fromExperimentTypeCodes = new ArrayList<String>();
		ssf.fromExperimentTypeCodes.add("badFromExperimentTypeCodes");		
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&fromExperimentTypeCodes="+ssf.fromExperimentTypeCodes.get(0)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;		
		assertThat(lcs).isEmpty();
		
		
		//Test with experimentTypeCode (good experimentTypeCode)		
		ssf.experimentTypeCode="prepa-flowcell";
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&experimentTypeCode="+ssf.experimentTypeCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;
		Logger.info("");
		
		for(int i=0; i<lcs.size();i++){
			assertThat("solution-stock").isIn(lcs.get(i).fromExperimentTypeCodes);			
		}		
		
		/*
		//Test with experimentTypeCode (bad experimentTypeCode)
		ssf.experimentTypeCode="badExperimentTypeCode";
		ssf.stateCode="";
		ssf.processTypeCode="";
		ssf.valuations=null;
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&experimentTypeCode="+ssf.experimentTypeCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;
		Logger.info("");
		assertThat(lcs).isEmpty();	*/	
		
	}
	
	@Test
	public void validateListWithList() {
		SupportsSearchForm ssf = SupportTestHelper.getFakeSupportsSearchForm();
		ssf.list = true;	
		MapperHelper mh = new MapperHelper();
		ListObject lo = new ListObject();
		List <ListObject> lc = new ArrayList<ListObject>();
		
		//Test with projectCodes (good projectCodes)
		ssf.projectCodes = new ArrayList<String>();
		ssf.projectCodes.add("BBA");
		Result result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&projectCodes="+ssf.projectCodes.get(0)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lc = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ListObject>>(){});
		for (int i=0;i<lc.size();i++){
			lo = lc.get(i);
			assertThat("BBA").isIn((MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, lo.code)).projectCodes);					
			Logger.info("");
		}
		
		//Test with projectCodes (bad projectCodes)	
		ssf.projectCodes = new ArrayList<String>();
		ssf.projectCodes.add("badProjectCodes");
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&projectCodes="+ssf.projectCodes.get(0)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lc = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ListObject>>(){});
		assertThat(lc).isNullOrEmpty();	
		
		
		
		//Test with processTypeCode (good processTypeCode)		
		
		//Test with processTypeCode (bad processTypeCode)		
		
					
		
	
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void validateList() {
		SupportsSearchForm ssf = SupportTestHelper.getFakeSupportsSearchForm();
		ssf.datatable = false;
		ssf.list = false;
		MapperHelper mh = new MapperHelper();
		ContainerSupport cs = new ContainerSupport();
		List<ContainerSupport> lcs = new ArrayList<ContainerSupport>();		
			
		//Test with dates (matched period)
		ssf.fromDate = new Date(2014-1900, 11, 16);
		ssf.toDate = new Date(2014-1900, 11, 18);
		
		Result result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&fromDate="+ssf.fromDate.getTime()+"&toDate="+ssf.toDate.getTime()));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ContainerSupport>>(){});
		for (int i=0;i<lcs.size();i++){
			cs = lcs.get(i);
			assertThat(cs.traceInformation.creationDate).isBetween(ssf.fromDate, ssf.toDate, true,true);					
			Logger.info("");
		}
		
		//Test with dates (unmatched period)
		ssf.fromDate = new Date(2014-1900, 11, 24);
		ssf.toDate = new Date(2015-1900, 0, 4);
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&fromDate="+ssf.fromDate.getTime()+"&toDate="+ssf.toDate.getTime()));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ContainerSupport>>(){});
		assertThat(lcs).isNullOrEmpty();	
		
		//Test with regex (matched pattern)
		ssf.codeRegex="^B";
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&codeRegex="+ssf.codeRegex));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ContainerSupport>>(){});
		Pattern p = Pattern.compile("^B.*");
		for (int i=0;i<lcs.size();i++){
			cs = lcs.get(i);
			assertThat(cs.code).matches(p);					
			Logger.info("");
		}
		
		//Test with regex (unmatched pattern)
		ssf.codeRegex="unmatched";
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?list="+String.valueOf(ssf.list)+"&codeRegex="+ssf.codeRegex));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		lcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<ArrayList<ContainerSupport>>(){});
		assertThat(lcs).isNullOrEmpty();
		
		//Test with stateCode (good stateCode)		
		
		//Test with stateCode (bad stateCode)	
	}
	
	/*
	@Test
	public void test() {
		
	}
	*/
	
}
