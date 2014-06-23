package workflows;

import java.util.Date;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTests;
import validation.ContextValidation;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;

public class ContainerStateTests extends AbstractTests {
	
	public static String CONTAINER_DATA="ContainerDataCNS";
	
	
	@Test
	public void setContainerStateCode(){
		Container container=MongoDBDAO.find(CONTAINER_DATA, Container.class,DBQuery.is("state.code", "A")).toList().get(0);
		MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,container.code);
		container=MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
		State state=new State();
		state.code="IW-E";
		state.user="test";
		state.date=new Date();
		ContextValidation contextValidation=new ContextValidation();
		Workflows.setContainerState(container.code,state,contextValidation);
		
		Container containerUpdate=MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", container.code));
		Assert.assertTrue(containerUpdate.state.code.equals(state.code));
		Assert.assertTrue(contextValidation.errors.size()==0);
				
	}
	
	@Test
	public void setContainerStateCodeError(){
		Container container=MongoDBDAO.find(CONTAINER_DATA, Container.class,DBQuery.is("state.code", "A")).toList().get(0);
		MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,container.code);
		container=MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
		State state=new State();
		state.code="TEST";
		state.user="test";
		state.date=new Date();
		ContextValidation contextValidation=new ContextValidation();
		Workflows.setContainerState(container.code,state,contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==1);
	
	}
}
