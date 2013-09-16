package utils;



import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import play.test.FakeApplication;
import play.test.Helpers;



public abstract class AbstractTests {
	
	FakeApplication app;
	@Before
	public void start(){
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
		config.put("mongodb.database", "NGL-TESTU");
		config.put("mongodb.credentials", "testu:testu");
		config.put("mongodb.servers", "mongodev.genoscope.cns.fr:27017");
		
		
		config.put("db.lims.driver", "org.postgresql.Driver");
		config.put("db.lims.url", "jdbc:postgresql://db.cng.fr/solexaprod");
		config.put("db.lims.user", "ngl_bi");
		config.put("db.lims.password", "N3wG3nLim5");
		config.put("db.lims.jndiName", "lims");
		
		config.put("asset.url","http://192.168.243.231:9001");
		
		// to delete errors messages;
		config.put("casUrlValidator", "https://cas.genoscope.cns.fr:8443/cas/serviceValidate");
		config.put("casUrlLogin", "https://cas.genoscope.cns.fr:8443/cas/login");
		config.put("casRenew", "false");
	    config.put("casMode", "debug");
		
		return config;
		
	}
	
	
}
