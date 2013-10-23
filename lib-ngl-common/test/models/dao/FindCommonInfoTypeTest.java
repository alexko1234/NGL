package models.dao;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.laboratory.common.description.CommonInfoType;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import play.api.modules.spring.Spring;
import play.test.Helpers;

import java.util.*;

import utils.AbstractTests;

public class FindCommonInfoTypeTest extends AbstractTests {
	
	
	@BeforeClass
	public static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		app = getFakeApplication();
		Helpers.start(app);
	}

	@AfterClass
	public static void endTest(){
		app = getFakeApplication();
		Helpers.stop(app);
	}
	
	
	@Test
	public void testCommonInfoType() throws DAOException {
		
		List<CommonInfoType> lcit = new ArrayList<CommonInfoType>();
		
		lcit = CommonInfoType.find.findAll();
	
		for (CommonInfoType cit : lcit) {
			/*
			Assert.assertNotNull(cit);
			Assert.assertNotNull(cit.id);
			Assert.assertNotNull(cit.name);
			Assert.assertNotNull(cit.code);
			*/
			
			//Assert.assertNotNull(cit.objectType);
			//Assert.assertNotNull(cit.objectType.code);
			
			Assert.assertNotNull(cit.objectType.states.get(0));
		
		}
	}

	//@Test
	public void printObjectStates() throws DAOException {
		
		// states
		Map<String, List<State>> m = new HashMap<String, List<State>>();
		
		
		for ( ObjectType o : ObjectType.find.findAll()) {
				m.put(o.code,  o.states); 
		}

		for (String key : m.keySet()) {
			for (State state : m.get(key)) {
				System.out.println("objectType :" + key + ", state :" + state);
			}
		}
	}
	
}
