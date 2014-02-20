package models.dao.type;

import static play.test.Helpers.fakeApplication;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import models.laboratory.experiment.description.ExperimentType;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;
import utils.AbstractTests;

public class NoInstituteDAOTypeTest extends AbstractTypeDAOTest{
	
	protected static FakeApplication app;
	
	@BeforeClass
	public  static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		app = getFakeApplication();
		Helpers.start(app);
		DescriptionHelper.initInstitute();
	}

	@AfterClass
	public  static void endTest() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		app = getFakeApplication();
		Helpers.stop(app);
	}
	
	public static FakeApplication getFakeApplication(){
		return fakeApplication(fakeConfiguration());
	}
	
	
	public static Map<String,String> fakeConfiguration(){
		Map<String,String> config = AbstractTests.fakeConfiguration();
		config.remove("institute");
		config.put("institute","");
		return config;

	}
	
	
	@Test
	public void noInstituteTest(){
		List<String> institute=DescriptionHelper.getInstitute();
		Assert.assertEquals(institute.size(),0);
	}

}
