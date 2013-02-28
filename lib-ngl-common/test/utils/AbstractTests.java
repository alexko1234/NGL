package utils;


import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import play.test.FakeApplication;
import play.test.Helpers;

public abstract class AbstractTests{
	FakeApplication app;
	
	@Before
	public void start() throws Exception{
		
		 app = getFakeApplication();
		 Helpers.start(app);
		 
	}
	
	@After
	public void stop(){
		Helpers.stop(app);
	}
	
	public FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}
	
	
	public Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		
		
		/**
		 * Config base dev
		 * config.put("evolutionplugin", "disabled");
		config.put("db.default.driver", "com.mysql.jdbc.Driver");
		config.put("db.default.url", "jdbc:mysql://mysql.genoscope.cns.fr:3307/NGL");
		config.put("db.default.user", "ngl");
		config.put("db.default.password", "ngl@dmin");
		config.put("db.default.partitionCount", "1");
		config.put("db.default.maxConnectionsPerPartition", "10");
		config.put("db.default.minConnectionsPerPartition", "1");
		config.put("db.default.logStatements", "true");
		config.put("db.default.jndiName", "ngl");*/
		config.put("db.default.driver", "com.mysql.jdbc.Driver");
        config.put("db.default.url", "jdbc:mysql://mysqldev.genoscope.cns.fr:3306/NGL_TEST");
        config.put("db.default.user", "NGL_user");
        config.put("db.default.password", "NGL_passwd");
        config.put("db.default.partitionCount", "1");
        config.put("db.default.maxConnectionsPerPartition", "10");
        config.put("db.default.minConnectionsPerPartition", "1");
        config.put("db.default.logStatements", "true");
        config.put("db.default.jndiName", "ngl"); 
		return config;
		
	}
	
	
}
