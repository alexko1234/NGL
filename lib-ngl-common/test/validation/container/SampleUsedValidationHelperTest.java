package validation.container;

import static org.fest.assertions.Assertions.assertThat;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Helpers;
import utils.AbstractTests;
import validation.ContextValidation;
import validation.container.instance.SampleUsedValidationHelper;

public class SampleUsedValidationHelperTest extends AbstractTests{
	
	@BeforeClass
	public static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		app = getFakeApplication();
		Helpers.start(app);
		initData();
	}

	@AfterClass
	public static void endTest(){
		app = getFakeApplication();
		deleteData();
		Helpers.stop(app);
	}


	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
	}

	private static void deleteData() {
	
	}
	
	/**
	 * Sample category code 
	 * @throws DAOException 
	 */
	@Test
	public void validateSampleCategoryCode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleCategoryCode(SampleCategory.find.findAll().get(0).code,contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateSampleCategoryCodeRequired() {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleCategoryCode(null,contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	@Test
	public void validateSampleCategoryNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleCategoryCode("notexist",contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	
	
	/**
	 * 
	 * Sample Type
	 * @throws DAOException 
	 * 
	 */
	@Test
	public void validateSampleTypeCode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleTypeCode(SampleType.find.findAll().get(0).code,contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateSampleTypeCodeRequired() {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleTypeCode(null,contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	@Test
	public void validateSampleTypeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		SampleUsedValidationHelper.validateSampleTypeCode("notexist",contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}	
	

}
