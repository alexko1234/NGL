package utils;



import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;



public abstract class AbstractTests {
	
	protected static FakeApplication app;
	@BeforeClass
	public static void start(){
		 app = getFakeApplication();
		 Helpers.start(app);
	}
	
	@AfterClass
	public static void stop(){
		Helpers.stop(app);
	}
	
	public static FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}
	
	
	public static  Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("application.secret", "ezfzzefzefz");
		config.put("evolutionplugin","disabled");
		config.put("ebean.default","models.*");
		config.put("mongodb.database", "NGL-TESTU");
		config.put("mongodb.credentials", "testu:testu");
		config.put("mongodb.servers", "mongodev.genoscope.cns.fr:27017");
		config.put("mongodb.defaultWriteConcern", "SAFE"); 
		
		config.put("db.default.driver", "com.mysql.jdbc.Driver");
		//TESTU must be updated
		//config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL");
		//config.put("db.default.user", "ngl");
		//config.put("db.default.password", "ngl");
		config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL_TEST");
		config.put("db.default.user", "NGL_user");
		config.put("db.default.password", "NGL_passwd");
		
		config.put("db.default.partitionCount", "1");
		config.put("db.default.maxConnectionsPerPartition", "10");
		config.put("db.default.minConnectionsPerPartition", "1");
		config.put("db.default.logStatements", "true");
		config.put("db.default.jndiName", "ngl");
		
		//config.put("db.lims.driver", "org.postgresql.Driver");
		//config.put("db.lims.url", "jdbc:postgresql://db.cng.fr/solexadev");
		//config.put("db.lims.user", "ngl_bi_dev");
		//config.put("db.lims.password", "toto");
		//config.put("db.lims.jndiName", "lims");

		
		
		config.put("db.lims.driver", "net.sourceforge.jtds.jdbc.Driver");
		config.put("db.lims.url", "jdbc:jtds:sybase://sybasedev.genoscope.cns.fr:3015/dblims");
		config.put("db.lims.user", "www");
		config.put("db.lims.password", "wawiwo");
		config.put("db.lims.jndiName", "lims");
		
		
		//config.put("asset.url","http://192.168.243.231:9001");
		
		config.put("asset.url","http://localhost:9000");
		
		// to delete errors messages;
		config.put("auth.cas.urlvalidator", "https://cas.genoscope.cns.fr:8443/cas/serviceValidate");
		config.put("auth.cas.urllogin", "https://cas.genoscope.cns.fr:8443/cas/login");
		config.put("auth.cas.renew", "false");
	    config.put("auth.mode", "debug");
	    config.put("auth.method", "cas");
	    config.put("auth.application", "ngl-bi");
	    
	    config.put("institute", "CNS");
	    config.put("mail.smtp.host", "smtp.genoscope.cns.fr");
	    config.put("rules.key", "nglBI");
	    config.put("rules.changesets", "rules/cng/changesets/changesets.xml");
	    config.put("play.spring.context-path", "cns-application-context.xml");
		return config;
		
	}
	
	
}
