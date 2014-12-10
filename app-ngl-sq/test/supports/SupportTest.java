package supports;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.InitDataHelper;

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
	
	/**********************************Tests of  (DAO Helper)***************************************************/	
	
}
