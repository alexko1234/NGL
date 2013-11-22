package models.instances.validation;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;
import static play.test.Helpers.contentAsString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.property.PropertyObjectValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import play.Logger;
import play.Play;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import static validation.utils.ValidationConstants.*;


public class TreatmentValidationTest extends AbstractTests {
	
	
	static Container c;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		   Container c = new Container();
		   c.code ="containerTest1";
		   c.support = new ContainerSupport(); 
		   c.support.barCode = "containerName"; 
		   
		   MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
		       }}); 
	}
	
	
	@AfterClass
	public static void deleteData(){
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		       }}); 
	}
	
	
	private void deleteRdCode() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","DIDIER_TESTFORTRT"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,readSetDelete._id);
		}
	}	
	
	private Treatment getNewTreatmentForReadSet() {
		Treatment t = new Treatment();
		t.code =  "ngsrg";		
		t.typeCode = "ngsrg-illumina";
		t.categoryCode = "ngsrg";
		//define map of single property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("nbCluster", new PropertySingleValue(100)); // valeur simple
		m.put("nbBases", new PropertySingleValue(100));
		m.put("fraction", new PropertySingleValue(100));
		m.put("Q30", new PropertySingleValue(100));
		m.put("qualityScore", new PropertySingleValue(100));
		m.put("nbReadIllumina", new PropertySingleValue(100));
		t.set("default", m);	
		return t;
	}
	
	private Treatment getNewTreatmentTaxonomyOK() {
		Treatment t = new Treatment();
		t.code =  "taxonomy";		
		t.typeCode =  "taxonomy";
		t.categoryCode = "quality";
		
		//define map of property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		
		m.put("sampleInput", new PropertySingleValue(100)); //Long.class, required=true
		m.put("organism",new PropertySingleValue("titi")); // String.class, true
		m.put("taxonomie",new PropertySingleValue("trucBizarre")); // String.class, true
		
		
		PropertyObjectValue p = new PropertyObjectValue(); // Map<String, ?>
		HashMap<String, Object> m2 = new HashMap<String, Object>(); 
		m2.put("taxon", "leTaxon");
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("taxonBilan", p);
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("division", "eukaryota"); //value OK
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("divisionBilan", p);
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("keyword", "virus"); //value OK
		m2.put("nbSeq", 1000000);
		m2.put("percent", 1320.52);
		p.value=m2;
		m.put("keywordBilan", p);

		
		File f = new File("krona");
		m.put("krona",new PropertySingleValue(f)); // File.class, true
		
		t.set("read1", m);
		
		return t;
	}
	
	
	private Treatment getNewTreatmentTaxonomyBad() {
		Treatment t = new Treatment();
		t.code =  "taxonomy";		
		t.typeCode =  "taxonomy";
		t.categoryCode = "quality";
		
		//define map of property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		
		m.put("sampleInput", new PropertySingleValue(100)); //Long.class, required=true
		m.put("organism",new PropertySingleValue("titi")); // String.class, true
		m.put("taxonomie",new PropertySingleValue("trucBizarre")); // String.class, true
		
		
		PropertyObjectValue p = new PropertyObjectValue(); // Map<String, ?>
		HashMap<String, Object> m2 = new HashMap<String, Object>(); 
		m2.put("taxon", "leTaxon");
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("taxonBilan", p);
		
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("division", "toto"); // not authorized, possible value : "eukaryota"
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("divisionBilan", p);
		
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("keyword", "titi"); // not authorized, possible value : "virus"
		m2.put("nbSeq", 1000000);
		m2.put("percent", 1320.52);
		p.value=m2;
		m.put("keywordBilan", p);

		
		File f = new File("krona");
		m.put("krona",new PropertySingleValue(f)); // File.class, true
		
		t.set("read1", m);
		
		return t;
	}
	
	
	
	@Test
	public void testValidatePropertyChoiceInListOK() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Treatment t = getNewTreatmentTaxonomyOK();
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(0);
		       }});
	}
	
	
	
	@Test
	public void testValidatePropertyChoiceInListBad() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Treatment t = getNewTreatmentTaxonomyBad();
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(2);
		
		assertThat(ctxVal.errors.toString()).contains(ERROR_VALUENOTAUTHORIZED_MSG);
		       }});
	}
	
	
		
	
	@Test
	 public void testValidateTreatmentCreationOK() {
			running(fakeApplication(fakeConfiguration()), new Runnable() {
			       public void run() {
			Treatment t = getNewTreatmentForReadSet();
					
			ContextValidation ctxVal = new ContextValidation(); 
			
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			
			//add readset to ctxVal
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
			       }});
	}
	
	
	
    @Test
	 public void testValidateTreatmentUpdatedOK() { 	 
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setUpdateMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
			assertThat(t.results().get("default").get("nbReadIllumina").value.toString()).contains("1");
		}
		else {
			System.out.println("Missing readSet rdCode !");
		}
		       }});
	}
	 
	
	
	@Test
	 public void testValidateTreatmentErrorMissingLevel() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		//Level.CODE levelCode = Level.CODE.ReadSet; 
		//ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		try {
			t.validate(ctxVal);
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.toString());
			b = true;
			msgErreur = "missing level parameter";
		}		
		assertThat(b).isEqualTo(true);
		assertThat(msgErreur).isEqualTo("missing level parameter");
		       }});
	}
	
	@Test
	 public void testValidationTreatmentErrorMissingCode() {	
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
		t.code =  null;	// NO CODE!	
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		assertThat(ctxVal.errors.size()).isGreaterThan(0);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("code");
		       }});
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCodeRequired() {	
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
	
		Treatment t = getNewTreatmentForReadSet();
		t.code =  ""; //empty!		
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors.size()).isGreaterThan(0);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("code");
		       }});
	}
	
	
	
	@Test
	 public void testValidationTreatmentErrorTypeCodeRequired() {	
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		deleteRdCode();
		
		Treatment t = getNewTreatmentForReadSet();	
		t.typeCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("typeCode");
		       }});
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCategoryCodeRequired() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		deleteRdCode(); 
		
		Treatment t = getNewTreatmentForReadSet();
		t.categoryCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("categoryCode");
		       }});
	}
	 
	 
	
	@Test
	 public void testValidateTreatmentErrorCodeNotUnique() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {		    	   
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTUNIQUE_MSG);
		}
		
		}});
	}
	
	private void createSameTrt() {

		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","DIDIER_TESTFORTRT"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,readSetDelete._id);
		}
		Sample sample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code","SampleCode"));
		if (sample!= null) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,sample._id);
		}
		Project project = MongoDBDAO.findOne(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code","ProjectCode"));
		if (project!= null) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, Project.class, project._id);
		}
		
		sample = RunMockHelper.newSample("SampleCode");
		project = RunMockHelper.newProject("ProjectCode");
		
		
		Run run = RunMockHelper.newRun("DIDIER_TESTFORTRT");
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		

		
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		readset.runCode = run.code;
		
		// add t1 to the readset
		Treatment t1 = new Treatment();
		t1.code =  "ngsrg";	
		t1.typeCode = "ngsrg-illumina";
		t1.categoryCode = "ngsrg";
		//define map of single property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("nbCluster", new PropertySingleValue(10));
		m.put("nbBases", new PropertySingleValue(100));
		m.put("fraction", new PropertySingleValue(33.33));
		m.put("Q30", new PropertySingleValue(33.33));
		m.put("qualityScore", new PropertySingleValue(33.33));
		m.put("nbReadIllumina", new PropertySingleValue(10));
		t1.set("default", m);
				
		Map<String, Treatment> mT = new HashMap<String, Treatment>();  
		mT.put("ngsrg", t1);
		
		readset.treatments = mT; 
		
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		
		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		
		assertThat(status(result)).isEqualTo(OK);
		
		
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
        assertThat(status(result)).isEqualTo(OK);
		
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCodeNotExists() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {		    	   
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		deleteRdCode();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		//in this case, we must be in update mode
		ctxVal.setUpdateMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists==null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTEXISTS_MSG);
		}
		else {
			System.out.println("method deleteRdCode() doesn't run normally !");
		}
		
		       }});

	}
	


	@Test
	 public void testValidateTreatmentErrorValueNotDefined() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Treatment t = getNewTreatmentForReadSet();
	
			t.results().get("default").put("bad", new PropertySingleValue("Ouh la la"));
			
					
			ContextValidation ctxVal = new ContextValidation(); 
			
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			
			//add readset to ctxVal
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_NOTDEFINED_MSG);
		       }});
	}
	
	

	
	
	//@Test(expected=java.lang.NumberFormatException.class)
	@Test
	 public void testValidateTreatmentErrorBadTypeValue() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	Treatment t = null; 
	    		ContextValidation ctxVal = new ContextValidation();  
						   
				t = getNewTreatmentForReadSet();
				
				t.results().get("default").remove("nbReadIllumina");
				//must generate a error (because of a bad value)
				t.results().get("default").put("nbReadIllumina", new PropertySingleValue("un"));	
				

	    		Level.CODE levelCode = Level.CODE.ReadSet; 
	    		ctxVal.putObject("level", levelCode);
	    		
	    		//add readset to ctxVal
	    		ReadSet readset = RunMockHelper.newReadSet("rdCode");
	    		ctxVal.putObject("readSet", readset);
	    		
	    		ctxVal.setCreationMode();
	    		
	    		t.validate(ctxVal);

	    		assertThat(ctxVal.errors).hasSize(1);
	    		assertThat(ctxVal.errors.toString()).contains(ERROR_BADTYPE_MSG);
		 }});

	}
	
	


	
	@Test
	 public void testValidateTreatmentErrorBadContext() {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Treatment t = getNewTreatmentForReadSet();
			
			// new bad context
			Map<String,PropertyValue> m3 = new HashMap<String,PropertyValue>();
			m3.put("nbCluster", new PropertySingleValue(10));
			m3.put("nbBases", new PropertySingleValue(100));
			m3.put("fraction", new PropertySingleValue(33.33));
			m3.put("Q30", new PropertySingleValue(33.33));
			m3.put("qualityScore", new PropertySingleValue(33.33));
			m3.put("nbReadIllumina", new PropertySingleValue(1));
	
			t.set("read3", m3);
			
			ContextValidation ctxVal = new ContextValidation(); 
			
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			
			//add readset to ctxVal
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_VALUENOTAUTHORIZED_MSG);	
		       }});
	}
	 
}
