package validation.sample;

import static org.fest.assertions.Assertions.assertThat;
import models.laboratory.sample.description.SampleCategory;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Helpers;
import utils.AbstractTests;
import validation.ContextValidation;
import validation.sample.instance.SampleValidationHelper;

public class SampleValidationHelperTest extends AbstractTests {

	
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
	 *  SampleCategory
	 * 
	 */
	@Test
	public void validateSampleCategoryCode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleCategoryCode(SampleCategory.find.findAll().get(0).code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateSampleCategoryCodeRequired() {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	@Test
	public void validateSampleCategoryCodeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}
	
	/**
	 *  SampleCategory
	 * 
	 */
	@Test
	public void validateSampleTypeCodesRequired() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleType(null,null,null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(2);
	}

	@Test
	public void validateSampleTypeCodesNotExist() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleType("notexist","notexist",null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(2);
	}

	
	//@Test 
	//TODO
	public void validateSampleTypeValidateProperties() {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}
	
}
