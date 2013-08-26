package utils;


import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import play.test.FakeApplication;

public abstract class AbstractTests {
	protected static FakeApplication app;
	
	public static FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}

	
	public static Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("application.secret","0Qj/s6}212-e$B|s6GFQ-`yo[(; 66c2v7z3}U|y[&sn!41eN0{f<49n8Qh.J5OYo.f9-Xj1S1U5YFla");
		config.put("evolutionplugin", "disabled");
		config.put("db.default.driver", "com.mysql.jdbc.Driver");
		config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL_TEST");
		config.put("db.default.user", "NGL_user");
		config.put("db.default.password", "NGL_passwd");
		config.put("db.default.partitionCount", "1");
		config.put("db.default.maxConnectionsPerPartition", "10");
		config.put("db.default.minConnectionsPerPartition", "1");
		config.put("db.default.logStatements", "true");
		config.put("db.default.jndiName", "model");
		
		config.put("db.lims.driver", "net.sourceforge.jtds.jdbc.Driver");
		config.put("db.lims.url", "jdbc:jtds:sybase://sybasedev.genoscope.cns.fr:3015/dblims");
		config.put("db.lims.user", "mhaquell");
		config.put("db.lims.password", "cmoexhdr");
		config.put("db.lims.partitionCount", "1");
		config.put("db.lims.maxConnectionsPerPartition", "6");
		config.put("db.lims.minConnectionsPerPartition", "1");
		config.put("db.lims.logStatements", "true");
		config.put("db.lims.jndiName", "lims");			
		
		config.put("casUrlValidator","https://cas.genoscope.cns.fr:8443/cas/serviceValidate");
		config.put("casUrlLogin","https://cas.genoscope.cns.fr:8443/cas/login");
		config.put("casRenew","false");
		config.put("casMode","debug");
		
		config.put("mongodb.database","NGL-SQ-TEST");
		config.put("mongodb.credentials","ngl:ngl");
		config.put("mongodb.servers","gsphere.genoscope.cns.fr:27017");
		config.put("ehcacheplugin", "disabled");
		config.put("mongodbJacksonMapperCloseOnStop", "disabled");
		
		config.put("asset.url","http://192.168.243.231:9001");
		
		return config;
		
	}
	
	
}
