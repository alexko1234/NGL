package utils;


// import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

// import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;

//import play.Logger;
// import play.test.FakeApplication;
import play.Application;
import play.test.Helpers;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public abstract class AbstractTests {
	
	// protected static FakeApplication app;
	protected static Application app;
	
	protected static Map<String,String> config = new HashMap<String,String>();
	
	@BeforeClass
	public  static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		// System.setProperty("config.file", TestHelper.getConfigFilePath("ngl-common-test.conf"));
		app = getFakeApplication();
		// Helpers.start(app);
		DescriptionHelper.initInstitute();
	}

	@AfterClass
	public  static void endTest() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		// app = getFakeApplication();
		Helpers.stop(app);
		app.asScala().stop();
		DescriptionHelper.initInstitute();
	}

	
	// public static FakeApplication getFakeApplication(){
	public static Application getFakeApplication(){
		// return fakeApplication();
		// throw new RuntimeException("fake application is not yet replaced");
		return fr.cea.ig.play.test.DevAppTesting.devapp("conf/ngl-common-test.conf","conf/logger.xml");
	}
	
	public static <T extends DBObject> T saveDBOject(Class<T> type, String collectionName,String code)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		String collection=type.getSimpleName();
		T object = (T) Class.forName (type.getName()).newInstance();
		object.code=code;
		object=MongoDBDAO.save(collectionName, object);
		return object;
	}


}
