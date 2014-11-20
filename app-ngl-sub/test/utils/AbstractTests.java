package utils;



import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import models.utils.dao.DAOException;

import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public abstract class AbstractTests {
	
	protected static FakeApplication app;
	
	@BeforeClass
	public  static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		app = getFakeApplication();
		Helpers.start(app);
	}

	@AfterClass
	public  static void endTest() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		app = getFakeApplication();
		Helpers.stop(app);
	}

	
	public static FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}
	
	
	public static Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		
		config.put("evolutionplugin", "disabled");
		config.put("db.default.driver", "com.mysql.jdbc.Driver");
		config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL_TEST");
		config.put("db.default.user", "NGL_user");
		config.put("db.default.password", "NGL_passwd");
		config.put("db.default.partitionCount", "1");
		config.put("db.default.maxConnectionsPerPartition", "10");
		config.put("db.default.minConnectionsPerPartition", "1");
		config.put("db.default.logStatements", "true");
		config.put("db.default.jndiName", "ngl");
				
		config.put("mongodb.database","CNS-NGL");
		config.put("mongodb.credentials","ngl:ngl");
//		config.put("mongodb.servers","mongodev.genoscope.cns.fr:27017");
		config.put("mongodb.servers","mongouat.genoscope.cns.fr:27018");
		config.put("ehcacheplugin", "disabled");
		
		config.put("institute", "CNS");
	
		return config;
		
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
