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
		 app = fakeApplication(fakeConfiguration());
		 Helpers.start(app);
		 init();
	}
	
	@After
	public void stop(){
		Helpers.stop(app);
	}
	
	public Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("mongodb.database", "NGL-BI");
		config.put("mongodb.credentials", "ngl-bi:NglBiPassW");
		config.put("mongodb.servers", "gsphere.genoscope.cns.fr:27017");
		
		config.put("db.lims.driver", "org.postgresql.Driver");
		config.put("db.lims.url", "jdbc:postgresql://db.cng.fr/solexaprod");
		config.put("db.lims.user", "ngl_bi");
		config.put("db.lims.password", "N3wG3nLim5");
		config.put("db.lims.jndiName", "lims");
		return config;
		
	}
	
	public abstract void init();
}
