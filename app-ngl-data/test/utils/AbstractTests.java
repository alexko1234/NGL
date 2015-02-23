package utils;

import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;

public class AbstractTests {


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
		config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL");
		config.put("db.default.user", "ngl");
		config.put("db.default.password", "ngl");
		config.put("db.default.partitionCount", "1");
		config.put("db.default.maxConnectionsPerPartition", "1");
		config.put("db.default.minConnectionsPerPartition", "1");
		config.put("db.default.logStatements", "true");
		config.put("db.default.jndiName", "ngl");

		config.put("db.tara.driver","com.mysql.jdbc.Driver");
		config.put("db.tara.url","jdbc:mysql://mysqlcns.genoscope.cns.fr:3307/Tara");
		config.put("db.tara.user","lims_user");
		config.put("db.tara.password","Lims_PassWrd");
		config.put("db.tara.jndiName","tara");	
		config.put("db.tara.partitionCount","1");
		config.put("db.tara.maxConnectionsPerPartition","1");
		config.put("db.tara.minConnectionsPerPartition","1");
		config.put("db.tara.logStatements","true");

		config.put("db.lims.driver", "net.sourceforge.jtds.jdbc.Driver");
		config.put("db.lims.url", "jdbc:jtds:sybase://sybaseuat.genoscope.cns.fr:4200/dblims");
		config.put("db.lims.user", "www");
		config.put("db.lims.password", "wawiwo");
		config.put("db.lims.partitionCount", "1");
		config.put("db.lims.maxConnectionsPerPartition", "1");
		config.put("db.lims.minConnectionsPerPartition", "1");
		config.put("db.lims.logStatements", "true");
		config.put("db.lims.jndiName", "lims");	

		config.put("mongodb.database","NGL-TESTU");
		config.put("mongodb.credentials","testu:testu");
		config.put("mongodb.servers","mongodev.genoscope.cns.fr:27017");
		config.put("ehcacheplugin", "disabled");
		
		config.put("mongodb.defaultWriteConcern", "SAFE");

		config.put("institute", "CNS");
		config.put("import.data","false");
	
		config.put("rules.key","nglBI" );
		config.put("rules.changesets","rules/ngl-bi/cns/changesets/changesets.xml");
		
		config.put("auth.cas.urlvalidator", "https://cas.genoscope.cns.fr:8443/cas/serviceValidate");
		config.put("auth.cas.urllogin", "https://cas.genoscope.cns.fr:8443/cas/login");
		config.put("auth.cas.renew", "false");
	    config.put("auth.mode", "debug");
	    config.put("auth.method", "cas");
	    config.put("auth.application", "ngl-bi");
		
		return config;

	}


}
