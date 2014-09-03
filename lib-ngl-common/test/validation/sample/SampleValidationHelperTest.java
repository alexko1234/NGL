package validation.sample;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Map;

import models.laboratory.sample.description.SampleCategory;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

import org.junit.Test;

import play.Logger;
import play.Play;
import utils.AbstractTests;
import validation.ContextValidation;
import validation.sample.instance.SampleValidationHelper;

public class SampleValidationHelperTest extends AbstractTests {

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
		SampleValidationHelper.validateSampleCategoryCode("notexist", contextValidation);
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
		
		Map<String,String> config = AbstractTests.fakeConfiguration();
		config.remove("institute");
		config.put("institute","CNS");	
		DescriptionHelper.initInstitute();
		
		// the sampleType and the importType are defined in the db
		assertThat(contextValidation.errors.size()).isEqualTo(3);
		assertThat(contextValidation.errors.toString()).contains("isAdapters");
		assertThat(contextValidation.errors.toString()).contains("isFragmented");
		assertThat(contextValidation.errors.toString()).contains("taxonSize");
		
		config = AbstractTests.fakeConfiguration();
		config.remove("institute");
		config.put("institute","CNG");
		DescriptionHelper.initInstitute();
		
		//only the import type exists so the sample type generate a error 
		assertThat(contextValidation.errors.size()).isEqualTo(3);
		assertThat(contextValidation.errors.toString()).contains("isAdapters");
		assertThat(contextValidation.errors.toString()).contains("isFragmented");
		assertThat(contextValidation.errors.toString()).contains("taxonSize");

		
		config = AbstractTests.fakeConfiguration();
		config.remove("institute");
		config.put("institute","CNS,CNG");
		DescriptionHelper.initInstitute();
	}
	
	
}
