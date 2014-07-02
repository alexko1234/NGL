package utils;


import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;


import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;

public abstract class AbstractTests {
	
	protected static FakeApplication app;
	
	@BeforeClass
	public  static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		app = getFakeApplication();
		Helpers.start(app);
	}

	@AfterClass
	public  static void endTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
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
		
		config.put("mongodb.defaultWriteConcern", "SAFE");
		
		config.put("institute", "CNS");
	
		return config;
		
	}

}