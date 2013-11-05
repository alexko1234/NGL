package utils;


import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import play.test.FakeApplication;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

//@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTests {
	
	//@Mock
	//private Http.Request request;

	protected static FakeApplication app;
	
	
	
	
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
	
		config.put("db.lims.driver", "net.sourceforge.jtds.jdbc.Driver");
		config.put("db.lims.url", "jdbc:jtds:sybase://sybasedev.genoscope.cns.fr:3015/dblims");
		config.put("db.lims.user", "mhaquell");
		config.put("db.lims.password", "cmoexhdr");
		config.put("db.lims.partitionCount", "1");
		config.put("db.lims.maxConnectionsPerPartition", "6");
		config.put("db.lims.minConnectionsPerPartition", "1");
		config.put("db.lims.logStatements", "true");
		config.put("db.lims.jndiName", "lims");	
				
		config.put("mongodb.database","NGL-TESTU");
		config.put("mongodb.credentials","testu:testu");
		config.put("mongodb.servers","mongodev.genoscope.cns.fr:27017");
		config.put("ehcacheplugin", "disabled");
		
		
		config.put("institute", "CNG");
	
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
