package importcns;

import static org.fest.assertions.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Constants;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import services.instance.container.ContainerImportCNS;
import services.instance.sample.UpdateSampleCNS;
import utils.AbstractTests;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ContainerTests extends AbstractTests {


	@BeforeClass
	public static  void initData() throws DAOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException {		
		AllTests.initDataRun();
		AllTests.initDataRunExt();
	}


	@Test 
	public void importPrepaflowcellTest() throws SQLException, DAOException{

		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		String sql="pl_PrepaflowcellToNGL @flowcellNoms=\'"+StringUtils.join(AllTests.prepaCodes,",")+"\'";
		ContainerImportCNS.createContainers(contextValidation,sql,"lane","F","prepa-flowcell","pl_BanquesolexaUneLane @nom_lane=?");
		Assert.assertEquals(contextValidation.errors.size(),0);
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code", AllTests.prepaCodes)).toList();
		for(Container container:containers){
			assertThat(container.contents.get(0).properties.get("libProcessTypeCode")).isNotNull();
			assertThat(container.contents.get(0).properties.get("sequencingProgramType")).isNull();
		}
		Assert.assertTrue(containers.size()>0);
		List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", AllTests.prepaCodes)).toList();
		for(ContainerSupport containerSupport:containerSupports){
			assertThat(containerSupport.properties.get("sequencingProgramType")).isNotNull();
		}
		Assert.assertEquals(containerSupports.size(), AllTests.prepaCodes.size());
	}


	
	
	@Test 
	public void importPrepaflowcellExtTest() throws SQLException, DAOException{

		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		String sql="pl_PrepaflowcellExtToNGL @runhnoms=\'"+StringUtils.join(AllTests.runExtCodes,",")+"\'";
		ContainerImportCNS.createContainers(contextValidation,sql,"lane","F","prepa-flowcell",null);
		Assert.assertEquals(contextValidation.errors.size(),0);
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code", AllTests.prepaExtCodes)).toList();
		for(Container container:containers){
			assertThat(container.contents.get(0).properties.get("libProcessTypeCode")).isNotNull();
		}
		Assert.assertTrue(containers.size()>0);
		List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", AllTests.prepaExtCodes)).toList();
		Assert.assertEquals(containerSupports.size(), AllTests.prepaExtCodes.size());
		
	}
	
	
	@Test 
	public void importSolutionStockTest() throws SQLException, DAOException{

		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		List<String> solutionStocks=new ArrayList<String>();
		solutionStocks.add("AXD_msCCH_d1");
		String sql="pl_SolutionStockToNGL @noms=\'"+StringUtils.join(solutionStocks,",")+"\'";
		ContainerImportCNS.createContainers(contextValidation,sql,"tube","F","solution-stock","pl_ContentFromContainer @matmanom=?");
		Assert.assertEquals(contextValidation.errors.size(),0);
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code", solutionStocks)).toList();
		for(Container container:containers){
			assertThat(container.contents.get(0).properties.get("libProcessTypeCode")).isNotNull();
			assertThat(((PropertySingleValue) container.mesuredConcentration).unit).isEqualTo("nM");
		}
		Assert.assertTrue(containers.size()>0);
		List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", solutionStocks)).toList();
		Assert.assertEquals(containerSupports.size(),solutionStocks.size());
		
	}
	
	
	//@Test
		public void updateSampleTest() throws SQLException, DAOException{

		List<ReadSet> readSetsBefore=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.in("sampleOnContainer.sampleCode", AllTests.sampleCodes)).toList();
		assertThat(readSetsBefore.size()).isEqualTo(AllTests.sampleCodes.size());
		List<Container> containersBefore=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("sampleCodes", AllTests.sampleCodes)).toList();
		assertThat(containersBefore.size()).isNotEqualTo(0);
		List<Sample> samplesBefore=MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.in("code", AllTests.sampleCodes)).toList();

		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.in("sampleOnContainer.sampleCode", AllTests.sampleCodes),
				DBUpdate.set("sampleOnContainer.properties.taxonSize",new PropertySingleValue(23)),true);
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("contents.sampleCode", AllTests.sampleCodes),
				DBUpdate.set("contents.$.properties.taxonSize",new PropertySingleValue(23)),true);
		MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("code", AllTests.sampleCodes),
				DBUpdate.set("properties.taxonSize",new PropertySingleValue(23)),true);


		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		UpdateSampleCNS.updateSampleFromTara(contextValidation, AllTests.sampleCodes);
		List<Sample> samples=MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.in("code",AllTests.sampleCodes)).toList();

		assertThat(samples.size()).isEqualTo(AllTests.sampleCodes.size());


		}
}
