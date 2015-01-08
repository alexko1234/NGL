package supports;

import static org.assertj.core.api.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.DatatableResponseForTest;
import utils.InitDataHelper;
import utils.MapperHelper;
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
		
		//Test with experimentTypeCode (good experimentTypeCode)
		/*
		ssf.categoryCode="";
		ssf.experimentTypeCode="prepa-flowcell";
		result = callAction(controllers.supports.api.routes.ref.Supports.list(), fakeRequest(play.test.Helpers.GET, "?datatable="+String.valueOf(ssf.datatable)+"&experimentTypeCode="+ssf.experimentTypeCode));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		dcs = mh.convertValue(mh.resultToJsNode(result), new TypeReference<DatatableResponseForTest<ContainerSupport>>(){});
		lcs = dcs.data;
		Logger.info("");
		
		for(int i=0; i<lcs.size();i++){
			assertThat("solution-stock").isIn(lcs.get(i).fromExperimentTypeCodes);
			Logger.info("");
		}		
		
		*/
		//Test with experimentTypeCode (bad experimentTypeCode)
		
		//Test with processTypeCode (good processTypeCode)		
		
		//Test with processTypeCode (bad processTypeCode)
				
		//Test with stateCode (good stateCode)		
				
		//Test with stateCode (bad stateCode)
		
		//Test with projectCodes (good projectCodes)
		
		//Test with projectCodes (bad projectCodes)
		
		//Test with dates (matched period)
		
		//Test with dates (unmatched period)		
		
		//Test with regex (matched pattern)
		
		//Test with regex (unmatched pattern)
	}
	
	@Test
	public void validateListWithList() {
		SupportsSearchForm ssf = SupportTestHelper.getFakeSupportsSearchForm();
		ssf.list = true;
		
		//Test with categoryCode (good categoryCode)		
		
		//Test with categoryCode (bad categoryCode)
		
		//Test with experimentTypeCode (good experimentTypeCode)		
		
		//Test with experimentTypeCode (bad experimentTypeCode)
		
		//Test with processTypeCode (good processTypeCode)		
		
		//Test with processTypeCode (bad processTypeCode)
				
		//Test with stateCode (good stateCode)		
				
		//Test with stateCode (bad stateCode)
		
		//Test with projectCodes (good projectCodes)
		
		//Test with projectCodes (bad projectCodes)
		
		//Test with dates (matched period)
		
		//Test with dates (unmatched period)		
		
		//Test with regex (matched pattern)
		
		//Test with regex (unmatched pattern)
	}
	
	@Test
	public void validateList() {
		SupportsSearchForm ssf = SupportTestHelper.getFakeSupportsSearchForm();
		ssf.datatable = true;
		
		//Test with categoryCode (good categoryCode)		
		
		//Test with categoryCode (bad categoryCode)
		
		//Test with experimentTypeCode (good experimentTypeCode)		
		
		//Test with experimentTypeCode (bad experimentTypeCode)
		
		//Test with processTypeCode (good processTypeCode)		
		
		//Test with processTypeCode (bad processTypeCode)
				
		//Test with stateCode (good stateCode)		
				
		//Test with stateCode (bad stateCode)
		
		//Test with projectCodes (good projectCodes)
		
		//Test with projectCodes (bad projectCodes)
		
		//Test with dates (matched period)
		
		//Test with dates (unmatched period)		
		
		//Test with regex (matched pattern)
		
		//Test with regex (unmatched pattern)
	}
	
	/*
	@Test
	public void test() {
		
	}
	*/
	
}
