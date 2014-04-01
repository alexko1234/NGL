package validation.container;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.AbstractTests;
import validation.ContextValidation;
import validation.container.instance.ContainerSupportValidationHelper;
import validation.container.instance.ContainerValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ContainerValidationHelperTest extends AbstractTests {

	static ContainerCategory containerCategory;
	static ContainerSupportCategory containerSupportCategory;
	static ProcessType processType;
	static ExperimentType experimentType;
	static ExperimentType experimentType1;
	
	static Experiment experiment;
	static Experiment experiment1;

	static Sample sample;
	static Sample sample1;
	
	static SampleType sampleType;
	
	static Content sampleUsed;
	static Content sampleUsed1;

	static LocationOnContainerSupport containerSupport;

	@BeforeClass
	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		containerCategory=ContainerCategory.find.findAll().get(0);

		containerSupportCategory=ContainerSupportCategory.find.findAll().get(0);

		processType=ProcessType.find.findAll().get(0);

		sampleType=SampleType.find.findAll().get(0);
		
		experimentType=ExperimentType.find.findAll().get(0);
		experimentType1=ExperimentType.find.findAll().get(1);

		experiment=saveDBOject(Experiment.class,InstanceConstants.EXPERIMENT_COLL_NAME,"experiment");
		experiment1=saveDBOject(Experiment.class,InstanceConstants.EXPERIMENT_COLL_NAME,"experiment1");
		
		sample=saveDBOject(Sample.class,InstanceConstants.SAMPLE_COLL_NAME,"sample");
		sample.typeCode=sampleType.code;
		sample.categoryCode=sampleType.category.code;
		
		sample1=saveDBOject(Sample.class,InstanceConstants.SAMPLE_COLL_NAME,"sample1");
		sample1.typeCode=sampleType.code;
		sample1.categoryCode=sampleType.category.code;
				
		sampleUsed=new Content(sample.code,sample.typeCode,sample.categoryCode);
		sampleUsed1=new Content(sample1.code,sample1.typeCode, sample1.categoryCode);
		
		containerSupport=new LocationOnContainerSupport();
		containerSupport.code="test";
		containerSupport.categoryCode=ContainerSupportCategory.find.findAll().get(0).code;
		containerSupport.line="1";
		containerSupport.column="1";
	}

	@AfterClass
	public static void deleteData() {
		MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class).drop();
		MongoDBDAO.getCollection(InstanceConstants.SAMPLE_COLL_NAME,Experiment.class).drop();
	}

	/**
	 *  Process Type 
	 */

	@Test
	public void validateProcessTypeCode() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateProcessTypeCode(processType.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateProcessTypeCodeNotRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateProcessTypeCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateProcessTypeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateProcessTypeCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	

	/**
	 * Experiment Type 
	 * 
	 */

	@Test
	public void validateExperimentTypeCodes() {
		ContextValidation contextValidation=new ContextValidation();
		List<String> listCodes=new ArrayList<String>();

		listCodes.add(experimentType.code);
		listCodes.add(experimentType1.code);
		ContainerValidationHelper.validateExperimentTypeCodes(listCodes, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateExperimentTypeCodesNotRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateExperimentTypeCodes(null, contextValidation);
		ContainerValidationHelper.validateExperimentTypeCodes(new ArrayList<String>(), contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	/**
	 * Experiment
	 */

	@Test
	public void validateExperimentTypeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		List<String> listCodes=new ArrayList<String>();
		listCodes.add("notexist");
		ContainerValidationHelper.validateExperimentTypeCodes(listCodes, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	
	
	@Test
	public  void validateExperimentCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateExperimentCode(experiment.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}


	@Test
	public  void validateExperimentNotRequiredCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateExperimentCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validateExperimentNotExistCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateExperimentCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}	
	
	
	/**
	 * 
	 * Content
	 * 
	 */
	
	@Test
	public  void validationContentTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<Content> localContents=new ArrayList<Content>(); 
		localContents.add(sampleUsed);
		localContents.add(sampleUsed1);
		ContainerValidationHelper.validateContents(localContents, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}


	@Test
	public  void validationContentRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateContents(null, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	
	@Test
	public void validationContentSampleUsedTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<Content> localContents=new ArrayList<Content>();
		localContents.add(new Content("","",""));
		ContainerValidationHelper.validateContents(localContents, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(3);
	}
	

	/**
	 * 
	 * Container ContainerSupport
	 * 
	 */
	@Test
	public  void validateContainerSupportTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateContainerSupport(containerSupport,contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validateContainerSupportRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		LocationOnContainerSupport localContainerSupport=new LocationOnContainerSupport();
		ContainerValidationHelper.validateContainerSupport(localContainerSupport,contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}


	/**
	 * ContainerCategory 
	 */
	@Test
	public void validationContainerCategoryCode() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateContainerCategoryCode(containerCategory.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validationContainerCategoryRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateContainerCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}

	@Test
	public void validationContainerCategoryNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerValidationHelper.validateContainerCategoryCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	


	/**
	 * ContainerSupportCategory
	 * 
	 */

	@Test
	public void validationContainerSupportCategoryCode() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(containerSupportCategory.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validationContainerSupportCategoryRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}

	@Test
	public void validationContainerSupportCategoryNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	


}
