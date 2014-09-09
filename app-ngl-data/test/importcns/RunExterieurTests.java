package importcns;

import static org.fest.assertions.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.Logger;

import fr.cea.ig.MongoDBDAO;
import services.instance.container.ContainerImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunImportCNS;
import utils.AbstractTests;
import validation.ContextValidation;

public class RunExterieurTests extends AbstractTests {

	
	static List<String> runCodes=new ArrayList<String>();
	static List<String> prepaCodes=new ArrayList<String>();
	
	
	@AfterClass
	public static  void deleteData() throws DAOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException{


	}

	@BeforeClass
	public static  void initData() throws DAOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException {		
		runCodes.add("140703_EXTMISEQ_M00619");
		prepaCodes.add("M00619");
	}

	
	@Test
	public void importProject() throws SQLException, DAOException{
		ContextValidation contextValidation=new ContextValidation();
		ProjectImportCNS.createProjet(contextValidation);
		Assert.assertEquals(contextValidation.errors.size(),0);
	}


	@Test 
	public void importPrepaflowcellTest() throws SQLException, DAOException{

		ContextValidation contextValidation=new ContextValidation();
		String sql="pl_PrepaflowcellExtToNGL @runhnoms=\'"+StringUtils.join(runCodes,",")+"\'";
		ContainerImportCNS.createContainers(contextValidation,sql,"lane","F","prepa-flowcell",null);
		Assert.assertEquals(contextValidation.errors.size(),0);
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code", prepaCodes)).toList();
		for(Container container:containers){
			assertThat(container.contents.get(0).properties.get("libProcessTypeCode")).isNotNull();
		}
		Assert.assertTrue(containers.size()>0);
		List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", prepaCodes)).toList();
		Assert.assertEquals(containerSupports.size(), prepaCodes.size());
		
	}
	
	@Test 
	public void runExterieurTest() throws SQLException, DAOException{
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("runCode", runCodes));

		ContextValidation contextValidation=new ContextValidation();
		String sql="pl_RunExtToNGL @runhnoms= \'"+StringUtils.join(runCodes,",")+"\'";
		Logger.debug("SQL runs to create :"+sql);
		RunImportCNS.createRuns(sql,contextValidation);
		
		
		contextValidation.displayErrors(Logger.of(this.getClass().getName()));		
		Assert.assertEquals(contextValidation.errors.size(), 0);

		//Verifie que tous les runs ont été créés
		List<Run> runCreate=MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes)).toList();
		Assert.assertEquals(runCreate.size(),runCodes.size());

	}
}
