package utils;


import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;

public abstract class AbstractTests {
	protected static FakeApplication app;

	public static FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}

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


	public static Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("application.secret","0Qj/s6}212-e$B|s6GFQ-`yo[(; 66c2v7z3}U|y[&sn!41eN0{f<49n8Qh.J5OYo.f9-Xj1S1U5YFla");
		config.put("evolutionplugin", "disabled");

		config.put("db.default.driver", "com.mysql.jdbc.Driver");
		config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL");
		config.put("db.default.user", "ngl");
		config.put("db.default.password", "ngl");
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

		config.put("db.tara.driver","com.mysql.jdbc.Driver");
		config.put("db.tara.url","jdbc:mysql://mysqlcns.genoscope.cns.fr:3307/Tara");
		config.put("db.tara.user","lims_user");
		config.put("db.tara.password","Lims_PassWrd");

		config.put("mongodb.database","NGL-TESTU");
		config.put("mongodb.credentials","testu:testu");
		config.put("mongodb.servers","mongodev.genoscope.cns.fr:27017");
		config.put("ehcacheplugin", "disabled");
		config.put("mongodbJacksonMapperCloseOnStop", "disabled");
		
		config.put("rules.key","nglSQ");
		config.put("rules.kbasename","ngl-sq-cns");
		
		config.put("auth.cas.urlvalidator", "https://cas.genoscope.cns.fr:8443/cas/serviceValidate");
		config.put("auth.cas.urllogin", "https://cas.genoscope.cns.fr:8443/cas/login");
		config.put("auth.cas.renew", "false");
	    config.put("auth.mode", "debug");
	    config.put("auth.method", "cas");
	    config.put("auth.application", "ngl-bi");
	    
		config.put("asset.url","http://192.168.243.231:9001");
		config.put("ebean.default", "models.*");
		return config;

	}


}
