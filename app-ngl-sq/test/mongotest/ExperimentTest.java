package mongotest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.*;
import org.junit.Test;

import play.Application;
import play.Environment;
import play.libs.Json;
import play.data.validation.ValidationError;
import play.test.*;
import static play.test.Helpers.*;
import javax.inject.*;
import play.inject.guice.GuiceApplicationBuilder;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

// import models.laboratory.common.instance.property.
import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyImgValue;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.instance.ExperimentHelper;
import validation.ContextValidation;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;


public class ExperimentTest { // extends WithApplication {

	// @Inject
	// Application application;
	
	// @Test
	public void testSum() {
		int a = 1 + 1;
		assertEquals(2, a);
	}

	// @Test
	public void testString() {
		String str = "Hello world";
		assertFalse(str.isEmpty());
	}
	
	static GuiceApplicationBuilder applicationBuilder;
	static Application application;
	public static Application devapp() {
		if (applicationBuilder == null) {
			System.setProperty("config.file", "c:\\projets\\config\\ngl-sq-dev.conf");
			System.setProperty("logger.file", "c:\\projets\\config\\logger.xml");
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
		    applicationBuilder = new GuiceApplicationBuilder().in(env);
		    application = applicationBuilder.build();
		}
		// return applicationBuilder.build();
		// This does not properly sets the Play.application() instance.
		return application;
	}
	
	// @Test
	public void startApp() {
		// Try to emulate the -D that should land in system.properties.
		// -Dconfig.file=c:\projets\config\ngl-sq-dev.conf -Dlogger.file=c:\projets\config\logger.xml -Dplay.server.netty.maxInitialLineLength=16384
	    Application app = devapp(); 
	    System.out.println("created app");
	    System.out.println("class loader " + app.injector()); // classLoader());
	    
	}
	
	
	// @Test
	public void badRequest() throws Exception {
		Application app = devapp();
		// http://localhost:9000/api/experiments/CHIP-MIGRATION-20170915_144939CDA
		Experiment exp = MongoDBDAO.findOne(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", "CHIP-MIGRATION-20170915_144939CDA"));
		assertNotNull(exp);
		// Experiment exp2 = serdeserJackson(exp);
		
		// This modifies the date and the user
		/*
		public void setTraceInformation(String user) {
			if (createUser == null) {
				createUser   = user;
				creationDate = new Date();
			} else {
				modifyUser = user;
				modifyDate = new Date();
			}				
		}
        */
		// input.traceInformation = getUpdateTraceInformation(input.traceInformation);
		
		// Updates stuff i'd say 
		// ExperimentHelper.doCalculations(exp2, "calculations");
		
		// Play serialization/deserialization
		// String s = Json.toJson(exp);
		// We expect some hidden json mapper as the Json.mapper() one does not trigger the error.
		System.out.println("Json mapper " + Json.mapper());
		System.out.println("  " + Json.mapper().isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS));
		
		// Confirmed that enabling DeserializationFeature.USE_BIG_INTEGER_FOR_INTS raises the error.
		// Json.mapper().enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
		// Experiment exp2 = Json.fromJson(Json.toJson(exp),Experiment.class);
				
		// Try to use the jsonnode version of the thing.
		// APICOmmonController : P input = Json.fromJson(json, clazz); (JsonNode json)
		// this is the mapper call : Json.mapper().treeToValue(json, clazz);
		Experiment exp2 = Json.fromJson(Json.toJson(exp),Experiment.class);
		
		// Trying to fire error
		// at fr.cea.ig.MongoDBDAO.update(MongoDBDAO.java:260) ~[mongodbplugin_2.11.jar:2.4-1.7.1-SNAPSHOT]
		// exp2 is a clone of exp.
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, exp2);
		
		// Try higher in the trace as r.cea.ig.MongoDBDAO.update(MongoDBDAO.java:260) does not trigger the error
		// controllers.experiments.api.Experiments.update(Experiments.java:365) ~[classes/:na]
		//controllers.experiments.api.Experiments.update(exp2);
	}
	
	public final static String EXP_CODE = "TESTYANNEXP";
	
	public static Experiment getFakeExperiment(){
		Experiment exp = new Experiment(EXP_CODE);
		exp.state = new State("N","ngsrg");
		exp.atomicTransfertMethods = new ArrayList<AtomicTransfertMethod>();
		exp.instrument = new InstrumentUsed();
		exp.instrument.outContainerSupportCategoryCode="tube";
		exp.experimentProperties = new HashMap<String, PropertyValue>();
		exp.instrumentProperties = new HashMap<String, PropertyValue>();
		
		return exp;
		
	}
	
	public PropertyDefinition getPropertyImgDefinition() {
		PropertyDefinition pDef = new PropertyDefinition();
		pDef.code = "restrictionEnzyme";
		pDef.name = "restrictionEnzyme";		
		pDef.active = true;
		pDef.required = true;
		pDef.valueType = "File";
		//pDef.propertyType = "Img";
		
		return pDef;
	}

	// @Test
	public void validatePropertiesFileImgErr() throws Exception {
		Application app = devapp();
		Experiment exp = getFakeExperiment();

		PropertyImgValue pImgValue = new  PropertyImgValue();
		byte[] data = new byte[] { (byte)0xe0, 0x4f, (byte)0xd0,
				0x20, (byte)0xea, 0x3a, 0x69, 0x10, (byte)0xa2, (byte)0xd8, 0x08, 0x00, 0x2b,
				0x30, 0x30, (byte)0x9d };
		pImgValue.value = data;
		pImgValue.fullname = "phylogeneticTree2.jpg";
		pImgValue.extension = "jpg";
		pImgValue.width = 250;
		pImgValue.height = 250;

		ContextValidation cv = new ContextValidation(Constants.TEST_USER); 
		cv.putObject("stateCode", "IP");

		PropertyDefinition pDef = getPropertyImgDefinition();

		Map<String, PropertyDefinition> hm = new HashMap<String, PropertyDefinition>();
		hm.put("restrictionEnzyme", pDef);

		cv.putObject("propertyDefinitions", hm.values());

		pImgValue.validate(cv);

		exp.instrumentProperties.put("enzymeChooser", pImgValue);

		showErrors(cv);

		// Save instead of insert ?
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);

		Experiment expBase = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, EXP_CODE);

		// ?
		// assertThat(expBase.instrumentProperties.get("enzymeChooser").value);

		// ExperimentValidationHelper.validateInstrumentUsed(exp.instrument,exp.instrumentProperties,cv);

		pImgValue.fullname = "test";

		expBase.instrumentProperties.clear();
		expBase.instrumentProperties.put("enzymeChooser", pImgValue);

		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("code", expBase.code),
				DBUpdate.set("instrumentProperties",expBase.instrumentProperties));

		Experiment expBase2 = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, EXP_CODE);

		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, EXP_CODE);

		// huh ?
		//assertThat(expBase2.instrumentProperties.get("enzymeChooser").value);
	}
	
	private <T> T serdeserJackson(T e) throws Exception {
		// 1.x code
		ObjectMapper mapper = new ObjectMapper();
        // mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        String jsonString = mapper.writeValueAsString(e);
        // System.out.println("************ KICK ME *********************");
        // System.out.println(jsonString);
        return mapper.readValue(jsonString,(Class<T>)e.getClass());
	}
	
	private void showErrors(ContextValidation cv) {
		if(cv.errors.size() > 0){
			for(Entry<String, List<ValidationError>> e : cv.errors.entrySet()){
				System.out.println(e);
			}
		}
	}
	
	public static class Constants {
		
		public static final String TEST_USER = "ngl-test";

	}

	
	// 2017-10-23 13:03:05,187 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5 
	// - v0 PropertySingleValue[value=44, unit=�L, class=java.lang.Integer]:class models.laboratory.common.instance.property.PropertySingleValue
	// 2017-10-23 13:03:05,187 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5 
	// -  v1:PropertySingleValue[value=44, unit=�L, class=java.math.BigInteger]:class models.laboratory.common.instance.property.PropertySingleValue
	// 2017-10-23 13:03:05,200 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5 
	// - jsv0:{"_type":"single","value":44,"unit":"�L"}
	// 2017-10-23 13:03:05,202 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5 
	// - jsv1:{"_type":"single","value":44,"unit":"�L"}
	// 2017-10-23 13:03:05,202 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5 
	// - v0':PropertySingleValue[value=44, unit=�L, class=java.lang.Integer]
	// 2017-10-23 13:03:05,202 [INFO] from controllers.experiments.api.Experiments in application-akka.actor.default-dispatcher-5
	// - v1':PropertySingleValue[value=44, unit=�L, class=java.math.BigInteger]
	@Test
	public void jsonAndRe() throws Exception {
		Application app = devapp();
		// copy/paste from log
		String v0s = "{\"_type\":\"single\",\"value\":44,\"unit\":\"�L\"}";
		String v1s = "{\"_type\":\"single\",\"value\":44,\"unit\":\"�L\"}";
		// Waste of time as the string deserialization is already correct.
	}
	
}
