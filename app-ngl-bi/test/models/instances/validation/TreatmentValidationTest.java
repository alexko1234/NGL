package models.instances.validation;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;
import static validation.utils.ValidationConstants.ERROR_BADTYPE_MSG;
import static validation.utils.ValidationConstants.ERROR_CODE_NOTEXISTS_MSG;
import static validation.utils.ValidationConstants.ERROR_CODE_NOTUNIQUE_MSG;
import static validation.utils.ValidationConstants.ERROR_NOTDEFINED_MSG;
import static validation.utils.ValidationConstants.ERROR_REQUIRED_MSG;
import static validation.utils.ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertyImgValue;
import models.laboratory.common.instance.property.PropertyObjectListValue;
import models.laboratory.common.instance.property.PropertyObjectValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.Play;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;


public class TreatmentValidationTest extends AbstractTests {	
	
	static Container c;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		ContainerSupport cs = new ContainerSupport();
		cs.code = "containerName";
		cs.categoryCode = "lane";
		   
		MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, cs);
		
	   Container c = new Container();
	   c.code ="containerTest1";
	   c.support = new LocationOnContainerSupport(); 
	   c.support.code = cs.code; 
	   
	   MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
	}
	
	
	@AfterClass
	public static void deleteData(){
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport cs : containerSupports) {
			if (cs.code.equals("containerName")) {
				MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME, cs);
			}
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
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
	
	 private PropertyImgValue getPropertyImgValue() {
			PropertyImgValue pImg = new PropertyImgValue();
			pImg.value = getData();
			pImg.fullname = "titi.jpg";
			pImg.extension = "jpg";
			pImg.width = 400;
			pImg.height = 300;
			return pImg;
	 }
	 
	 private List<Float> getListFloat() {
			List<Float> lf = new ArrayList<Float>();
			Float f = 2.F;
			for (int i=0; i<101; i++) {
				lf.add(f);
			}
			return lf;
	 }
	 
	private Treatment getNewTreatmentPropertyDetailsOK() {			
		Treatment t = new Treatment();
		t.code =  "readQualityRaw";		
		t.typeCode =  "read-quality";
		t.categoryCode = "quality";
		
		//define map of property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();				 
		
		m.put("sampleInput", new PropertySingleValue(100));
		m.put("qualScore",getPropertyImgValue());
		m.put("nuclDistribution", getPropertyImgValue());
		m.put("readWithNpercent", getPropertyImgValue());
		m.put("readSizeDistribution", getPropertyImgValue());
		m.put("adapterContamination", getPropertyImgValue());
		m.put("GCDistribution", getPropertyImgValue());
		m.put("positionN", getPropertyImgValue());
		m.put("maxSizeReads", new PropertySingleValue(100));
		m.put("maxSizeReadsPercent", new PropertySingleValue(100));

		PropertyObjectListValue lpObj = new PropertyObjectListValue();
		List l = new ArrayList();
		
		HashMap<String, Object> m2 = new HashMap<String, Object>(); 
		m2.put("adapterName", "RNA_PCR_MK1(rev)");
		m2.put("contaminationIntensities", getListFloat());

		HashMap<String, Object> m3 = new HashMap<String, Object>();
		m3.put("adapterName", "totoAdaptateur");
		m3.put("contaminationIntensities", getListFloat());
		
		l.add(m2);
		l.add(m3);
		lpObj.value=l;				
		m.put("adapterContaminationDetails", lpObj);
		
		PropertyObjectListValue lpObj2 = new PropertyObjectListValue();
		List l2 = new ArrayList();
		
		HashMap<String, Object> m4 = new HashMap<String, Object>();
		m4.put("numberOfN",1);
		m4.put("percentOfReads",2.F);
		
		HashMap<String, Object> m5 = new HashMap<String, Object>();
		m5.put("numberOfN",2);
		m5.put("percentOfReads",2.F);
		
		l2.add(m4);
		l2.add(m5);
		lpObj2.value=l2;
		m.put("readWithNpercentDetails", lpObj2);
		
		/***/
		PropertyObjectListValue lpObj3 = new PropertyObjectListValue();
		List l3 = new ArrayList();
		
		HashMap<String, Object> m6 = new HashMap<String, Object>();
		m6.put("positionInReads",1);
		m6.put("numberOfN",2);
		
		l3.add(m6);
		lpObj3.value=l3;
		m.put("positionNdetails", lpObj3);
		
		/***/
		PropertyObjectListValue lpObj4 = new PropertyObjectListValue();
		List l4 = new ArrayList();
		
		HashMap<String, Object> m7 = new HashMap<String, Object>();
		m7.put("readsLength",1);
		m7.put("percentOfReads",2.F);
		
		l4.add(m7);
		lpObj4.value=l4;
		m.put("readSizeDistributionDetails", lpObj4);
		
		/***/
		PropertyObjectListValue lpObj5 = new PropertyObjectListValue();
		List l5 = new ArrayList();
		
		HashMap<String, Object> m8 = new HashMap<String, Object>();
		m8.put("position",1);
		m8.put("minQualityScore",2);
		m8.put("maxQualityScore",50);
		m8.put("meanQualityScore",17.F);
		m8.put("Q1",21);
		m8.put("medianQualityScore",16.F);
		m8.put("Q3",34);
		m8.put("lowerWhisker",18);
		m8.put("upperWhisker",37);
		
		l5.add(m8);
		lpObj5.value=l5;
		m.put("qualScoreDetails", lpObj5);
		
		/***/
		PropertyObjectListValue lpObj6 = new PropertyObjectListValue();
		List l6 = new ArrayList();
		
		HashMap<String, Object> m9 = new HashMap<String, Object>();
		m9.put("readPosition",1);
		m9.put("APercent",20.F);
		m9.put("TPercent",20.F);
		m9.put("CPercent",20.F);
		m9.put("GPercent",20.F);
		m9.put("NPercent",20.F);
		
		l6.add(m9);
		lpObj6.value=l6;
		m.put("nuclDistributionDetails", lpObj6);
		
		/***/
		PropertyObjectListValue lpObj7 = new PropertyObjectListValue();
		List l7 = new ArrayList();
		
		HashMap<String, Object> m10 = new HashMap<String, Object>();
		m10.put("percentGCcontent",1.F);
		m10.put("percentOfReads",50.F);
		
		l7.add(m10);
		lpObj7.value=l7;
		m.put("GCDistributionDetails", lpObj7);

		
		/*set the context*/
		t.set("read1", m);
		return t; 
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
	
	private Treatment getNewTreatmentSampleControlOK() {	
		Treatment t = new Treatment();
		t.code =  "sampleControl";		
		t.typeCode =  "sample-control";
		t.categoryCode = "quality";
		//define map of property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("sampleInput", new PropertySingleValue(100));
		m.put("sampleSexe",new PropertySingleValue("M"));
		m.put("samplesComparison",new PropertySingleValue(0.50));	
		t.set("pairs", m);
		return t;
	}
	
	private byte[] getData() {
		byte[] data = new byte[] { (byte)0xe0, 0x4f, (byte)0xd0,
			    0x20, (byte)0xea, 0x3a, 0x69, 0x10, (byte)0xa2, (byte)0xd8, 0x08, 0x00, 0x2b,
			    0x30, 0x30, (byte)0x9d };
		return data;
	}
	
	private Treatment getNewTreatmentTaxonomyOK() {
		Treatment t = new Treatment();
		t.code =  "taxonomy";		
		t.typeCode =  "taxonomy";
		t.categoryCode = "quality";
		
		//define map of property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		
		m.put("sampleInput", new PropertySingleValue(100));
		m.put("organism",new PropertySingleValue("titi"));
		m.put("taxonomy",new PropertySingleValue("trucBizarre"));		
		
		PropertyObjectValue p = new PropertyObjectValue();
		HashMap<String, Object> m2 = new HashMap<String, Object>(); 
		m2.put("taxon", "leTaxon");
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("taxonBilan", p);
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("division", "eukaryota"); 
		m2.put("nbSeq", 1000000);
		m2.put("percent", 10.52);
		p.value=m2;
		m.put("divisionBilan", p);
		
		p = new PropertyObjectValue();
		m2 = new HashMap<String, Object>(); 
		m2.put("keyword", "virus"); 
		m2.put("nbSeq", 1000000);
		m2.put("percent", 1320.52);
		p.value=m2;
		m.put("keywordBilan", p);

		byte[] data = getData(); 
		
		PropertyFileValue pf = null;
		pf = new PropertyFileValue();
		pf.value = data;
		pf.fullname = "krona.html";
		pf.extension = "html";
		m.put("krona",pf);
		
		PropertyImgValue pi = null;
		pi = new PropertyImgValue();
		pi.value = data;
		pi.fullname = "phylogeneticTree.jpg";
		pi.extension = "jpg";
		pi.width = 400;
		pi.height = 300;
		m.put("phylogeneticTree",pi);
		
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
		
		m.put("sampleInput", new PropertySingleValue(100));
		m.put("organism",new PropertySingleValue("titi"));
		m.put("taxonomy",new PropertySingleValue("trucBizarre"));
				
		PropertyObjectValue p = new PropertyObjectValue();
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

		PropertyFileValue pf = null;
		pf = new PropertyFileValue();
		pf.value = getData();
		pf.fullname = "krona.html";
		pf.extension = "html";
		m.put("krona",pf);
					
		PropertyImgValue pi = null;
		pi = new PropertyImgValue();
		pi.value = getData();
		pi.fullname = "phylogeneticTree.jpg";
		pi.extension = "jpg";
		pi.width = 400;
		pi.height = 300;
		m.put("phylogeneticTree",pi);
		
		t.set("read1", m);
		
		return t;
	}
	
	/*** tests begins here ! *************************************/
	
	@Test
	public void testValidatePropertyChoiceInListOK() {
		Treatment t = null;
		if (Play.application().configuration().getString("institute").toUpperCase().equals("CNS")) {
			t = getNewTreatmentTaxonomyOK();
		}
		else {
			t = getNewTreatmentSampleControlOK();
		}
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(0);
	}
	
	
	
	@Test
	public void testValidatePropertyChoiceInListBad() {
		if (Play.application().configuration().getString("institute").toUpperCase().equals("CNS")) {
			Treatment t = getNewTreatmentTaxonomyBad();
			
			ContextValidation ctxVal = new ContextValidation(); 			
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(2);
			assertThat(ctxVal.errors.toString()).contains(ERROR_VALUENOTAUTHORIZED_MSG);
		}
	}
	
	
		
	
	@Test
	 public void testValidateTreatmentCreationOK() {
			Treatment t = getNewTreatmentForReadSet();
					
			ContextValidation ctxVal = new ContextValidation(); 
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
	}
	
	
	
    @Test
	 public void testValidateTreatmentUpdatedOK() { 	 
		Treatment t = getNewTreatmentForReadSet();
		
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		ctxVal.setUpdateMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
			assertThat(t.results().get("default").get("nbReadIllumina").value.toString()).contains("100");
		}
		else {
			System.out.println("Missing readSet rdCode !");
		}
	}
	 
	
	
    @Test
	 public void testValidateTreatmentErrorMissingLevel() {
		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
				
		ContextValidation ctxVal = new ContextValidation(); 
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
	}
	
    
    
	 @Test
	 public void testValidationTreatmentErrorMissingCode() {	
		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
		t.code =  null;	// NO CODE!	
				
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors.size()).isGreaterThan(0);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("code");
	}
	
	
	
	 @Test
	 public void testValidateTreatmentErrorCodeRequired() {	
		Treatment t = getNewTreatmentForReadSet();
		t.code =  ""; //empty!		
		
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors.size()).isGreaterThan(0);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("code");
	}
	
	
	
	 @Test
	 public void testValidationTreatmentErrorTypeCodeRequired() {	
		deleteRdCode();
		
		Treatment t = getNewTreatmentForReadSet();	
		t.typeCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("typeCode");
	}
	
	
	
	 @Test
	 public void testValidateTreatmentErrorCategoryCodeRequired() {
		deleteRdCode(); 
		
		Treatment t = getNewTreatmentForReadSet();
		t.categoryCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("categoryCode");
	}
	 
	 
	
	 @Test
	 public void testValidateTreatmentErrorCodeNotUnique() {
		Treatment t = getNewTreatmentForReadSet();
		
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setCreationMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTUNIQUE_MSG);
		}
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
		
		//result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
        //assertThat(status(result)).isEqualTo(OK);
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCodeNotExists() {
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		deleteRdCode();
				
		ContextValidation ctxVal = new ContextValidation(); 
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);		
		ctxVal.setUpdateMode(); //in this case, we must be in update mode
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists==null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTEXISTS_MSG);
		}
		else {
			System.out.println("method deleteRdCode() doesn't run normally !");
		}

	}
	


	 @Test
	 public void testValidateTreatmentErrorValueNotDefined() {
			Treatment t = getNewTreatmentForReadSet();
	
			t.results().get("default").put("bad", new PropertySingleValue("Ouh la la"));
								
			ContextValidation ctxVal = new ContextValidation(); 
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_NOTDEFINED_MSG);
	}
	
	

	 //@Test(expected=java.lang.NumberFormatException.class)
	 @Test
	 public void testValidateTreatmentErrorBadTypeValue() {
	    	Treatment t = null; 
    		ContextValidation ctxVal = new ContextValidation();  
    		Level.CODE levelCode = Level.CODE.ReadSet; 
    		ctxVal.putObject("level", levelCode);
    		ReadSet readset = RunMockHelper.newReadSet("rdCode");
    		ctxVal.putObject("readSet", readset);	    		
    		ctxVal.setCreationMode();
					   
			t = getNewTreatmentForReadSet();
			
			t.results().get("default").remove("nbReadIllumina");
			//must generate a error (because of a bad value)
			t.results().get("default").put("nbReadIllumina", new PropertySingleValue("un"));	
    		
    		t.validate(ctxVal);

    		assertThat(ctxVal.errors).hasSize(1);
    		assertThat(ctxVal.errors.toString()).contains(ERROR_BADTYPE_MSG);
	}
	
	
	
	 @Test
	 public void testValidateTreatmentErrorBadContext() {
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
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_VALUENOTAUTHORIZED_MSG);	
	}
	 

	 
		@Test
		public void testValidatePropertyDetailsOK() {
			Treatment t = getNewTreatmentPropertyDetailsOK();
						
			ContextValidation ctxVal = new ContextValidation(); 			
			Level.CODE levelCode = Level.CODE.ReadSet; 
			ctxVal.putObject("level", levelCode);
			ReadSet readset = RunMockHelper.newReadSet("rdCode");
			ctxVal.putObject("readSet", readset);
			
			ctxVal.setCreationMode();
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
		}
		
		

}
