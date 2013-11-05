package validation.sample;

import static org.fest.assertions.Assertions.assertThat;
import models.laboratory.sample.description.SampleCategory;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.Play;
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
	
	
	@Test
	public void validateSampleTypeCodeExistsByInstitute() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		SampleValidationHelper.validateSampleType("BAC","default-import",null, contextValidation);
		
		if (Play.application().configuration().getString("institute").equals("CNS")) {
			// the sampleType and the importType are defined in the db
			assertThat(contextValidation.errors.size()).isEqualTo(3);
			assertThat(contextValidation.errors.toString()).contains("isAdapters");
			assertThat(contextValidation.errors.toString()).contains("isFragmented");
			assertThat(contextValidation.errors.toString()).contains("taxonSize");
		}
		else {
			//only the import type exists so the sample type generate a error 
			assertThat(contextValidation.errors.size()).isEqualTo(1);
			assertThat(contextValidation.errors.toString()).contains("codenotexists");
			assertThat(contextValidation.errors.toString()).contains("BAC");
		}
	}
	
	
}
