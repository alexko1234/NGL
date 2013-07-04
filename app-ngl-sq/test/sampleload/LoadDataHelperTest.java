package sampleload;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

import org.junit.BeforeClass;
import org.junit.Test;

import play.data.validation.ValidationError;
import play.test.Helpers;
import utils.AbstractTests;

import data.utils.LoadDataHelper;

public class LoadDataHelperTest extends AbstractTests {

	static String sampleHeader;
	static String importHeader;
	Map<String, List<play.data.validation.ValidationError>> errors= new HashMap<String, List<ValidationError>>();			
	static SampleType sampleType=null;
	static ImportType importType=null;


	@BeforeClass
	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{		 
		app = getFakeApplication();
		Helpers.start(app);
		sampleType= DescriptionHelper.getSampleType("bac","Bac", "ADNClone", FirstData.getPropertyDefinitionsADNClone());
		importType= DescriptionHelper.getImportType("importBanqueSolexa","Importation Sample Bq Solexa","importSample",FirstData.getPropertyDefinitionsImportBq());

		sampleHeader= LoadDataHelper.getHeaderProperties(Sample.HEADER, sampleType.propertiesDefinitions, Sample.class);
		importHeader= LoadDataHelper.getHeaderProperties(Container.HEADER, importType.propertiesDefinitions, ImportType.class);
	}

	@Test
	public void validateHeaderMissColumn(){
		String[] firstLineDiff = sampleHeader.concat(";Test.column1;").concat(importHeader).concat(";Test.column2").split(String.valueOf(LoadDataHelper.SEPARATOR));
		LoadDataHelper.validateHeader(firstLineDiff, sampleType, importType, errors);
		assertThat(errors.size()).isEqualTo(2);
	}

	@Test
	public void validateHeader(){
		String[] firstLineSame = sampleHeader.concat(";").concat(importHeader).split(String.valueOf(LoadDataHelper.SEPARATOR));
		LoadDataHelper.validateHeader(firstLineSame, sampleType, importType, errors);
		assertThat(errors.size()).isEqualTo(0);
	}

	@Test
	public void validateHeaderFirstLineNull(){
		errors.clear();
		LoadDataHelper.validateHeader(null, sampleType, importType, errors);
		assertThat(errors.size()).isEqualTo(1);
	}



}
