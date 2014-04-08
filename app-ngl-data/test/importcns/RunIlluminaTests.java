package importcns;

import static org.fest.assertions.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.LimsCNSDAO;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.api.modules.spring.Spring;
import services.instance.container.ContainerImportCNS;
import services.instance.container.UpdateTaraPropertiesCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunImportCNS;
import utils.AbstractTests;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class RunIlluminaTests extends AbstractTests{


	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	static List<String> runCodes=new ArrayList<String>();
	static List<String> prepaCodes=new ArrayList<String>();
	static List<String> runDelete=new ArrayList<String>();

	@AfterClass
	public static  void deleteData() throws DAOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException{


	}

	@BeforeClass
	public static  void initData() throws DAOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException {		
		//Miseq
		runCodes.add("140127_MIMOSA_A7PE4");
		runCodes.add("140116_FLUOR_C39MEACXX");
		runCodes.add("140124_MIMOSA_A72F0");
		runCodes.add("111018_PHOSPHORE_C05T4ACXX");
		//Run abandonne sans readSets
		runCodes.add("130910_MERCURE_D2G9NACXX");	
		//Pas de solution pour l'instant car prepaflowcell n'est pas en adequation avec le run
		//runCodes.add("080724_HELIUM_201WFAAXX");
	
		//Run Tara pour tester udpdate Tara
		runCodes.add("131205_MERCURE_C3959ACXX");
		
		runDelete.addAll(runCodes);
		// Miseq
		prepaCodes.add("A7PE4");
		prepaCodes.add("A72F0"); 
		prepaCodes.add("C37T3ACXX");
		prepaCodes.add("C39MEACXX");
		prepaCodes.add("C05T4ACXX");
		prepaCodes.add("D2G9NACXX");
		// prepaflowcell tag=null
		prepaCodes.add("C3K2AACXX");
		prepaCodes.add("C3959ACXX");
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
		String sql="pl_PrepaflowcellToNGL @flowcellNoms=\'"+StringUtils.join(prepaCodes,",")+"\'";
		ContainerImportCNS.createContainers(contextValidation,sql,"lane","F","prepa-flowcell","pl_BanquesolexaUneLane @nom_lane=?");
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
	public void importRunCNSTest() throws SQLException, DAOException{
		
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runDelete));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("runCode", runCodes));
		
		ContextValidation contextValidation=new ContextValidation();
		String sql="pl_RunToNGL @runhnoms= \'"+StringUtils.join(runCodes,",")+"\'";
		Logger.debug("SQL runs to create :"+sql);
		RunImportCNS.createRuns(sql,contextValidation);
		//Import sans erreurs
		contextValidation.displayErrors(Logger.of(this.getClass().getName()));		
	//	Logger.debug("Error import run "+contextValidation.errors.size());
		Assert.assertEquals(contextValidation.errors.size(), 0);
		
		//Verifie que tous les runs ont été créés
		List<Run> runCreate=MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes)).toList();
		Assert.assertEquals(runCreate.size(),runCodes.size());
		
		// Pour chaque run verifie la cohérence
		for(Run run :runCreate){
			for(Lane lane  :run.lanes){
				if(lane.readSetCodes!=null){
				for(String readCode:lane.readSetCodes){
					List<ReadSet> readSets=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,DBQuery.is("code", readCode)).toList();
					Assert.assertTrue(readSets.size()==1);
					Assert.assertNotNull(readSets.get(0).sampleOnContainer);
					Assert.assertTrue(readSets.get(0).projectCode!=null);
					Assert.assertTrue(readSets.get(0).sampleCode!=null);
					Assert.assertTrue(readSets.get(0).runSequencingStartDate!=null);
				}
				if(lane.readSetCodes!=null){
					Assert.assertNotNull(lane.treatments.get("ngsrg").results().get("default").get("percentClusterIlluminaFilter"));
					Assert.assertNotNull(lane.treatments.get("ngsrg").results().get("default").get("seqLossPercent"));
					Assert.assertTrue(run.projectCodes.size()>0);
					Assert.assertTrue(run.sampleCodes.size()>0);
				}
				}
			}
		}

	}
	
	
	@Test
	public void updateTaraTest() throws SQLException, DAOException{
		
		Map<String,String> taraSampleCodes=new HashMap<String,String>();
		taraSampleCodes.put("ARD_BLIA","20736");
	//	taraSampleCodes.put("ARC_BLHB","21285");
		
		List<ReadSet> readSetsBefore=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.in("sampleOnContainer.sampleCode", new ArrayList<String>(taraSampleCodes.keySet()))).toList();
		assertThat(readSetsBefore.size()).isEqualTo(taraSampleCodes.size());
		List<Container> containersBefore=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("sampleCodes", new ArrayList<String>(taraSampleCodes.keySet()))).toList();
		assertThat(containersBefore.size()).isNotEqualTo(0);
		List<Sample> samplesBefore=MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.in("code", new ArrayList<String>(taraSampleCodes.keySet()))).toList();

		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.in("sampleOnContainer.sampleCode", new ArrayList<String>(taraSampleCodes.keySet())),
					DBUpdate.set("sampleOnContainer.properties.taraStation",new PropertySingleValue(23)),true);
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("contents.sampleCode", new ArrayList<String>(taraSampleCodes.keySet())),
				DBUpdate.set("contents.$.properties.taraStation",new PropertySingleValue(23)),true);
		MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("code", new ArrayList<String>(taraSampleCodes.keySet())),
				DBUpdate.set("properties.taraStation",new PropertySingleValue(23)),true);
	
		
		ContextValidation contextValidation=new ContextValidation();
		UpdateTaraPropertiesCNS.updateSampleFromTara(contextValidation, new ArrayList<String>(taraSampleCodes.values()));
		
		List<ReadSet> readSetsAfter=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class
				,DBQuery.in("sampleOnContainer.sampleCode", taraSampleCodes.keySet().toArray())).toList();

		List<Container> containersAfter=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("sampleCodes", new ArrayList<String>(taraSampleCodes.keySet()))).toList();
		assertThat(containersBefore.size()).isNotEqualTo(0);
		List<Sample> samplesAfter=MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.in("code", new ArrayList<String>(taraSampleCodes.keySet()))).toList();

		assertThat(readSetsBefore.size()).isEqualTo(taraSampleCodes.size());
		for(int i=0;i<readSetsBefore.size();i++){
			//readset validation
			assertThat(readSetsBefore.get(i).sampleOnContainer.sampleCode).isEqualTo(readSetsAfter.get(i).sampleOnContainer.sampleCode);
			assertThat(readSetsBefore.get(i).sampleOnContainer.properties.get("taraStation").value).isEqualTo(readSetsAfter.get(i).sampleOnContainer.properties.get("taraStation").value);				
			
		}
		
		for(int i=0;i<samplesBefore.size();i++){
			//sample validation
			assertThat(samplesBefore.get(i).code).isEqualTo(samplesAfter.get(i).code);
			assertThat(samplesBefore.get(i).properties.get("taraStation").value).isEqualTo(samplesAfter.get(i).properties.get("taraStation").value);				
		}
		
		for(int i=0;i<containersBefore.size();i++){
			//container validation
			assertThat(containersBefore.get(i).code).isEqualTo(containersAfter.get(i).code);
			for(Entry<String,String> entry : taraSampleCodes.entrySet()){
				
				List<Content> contentsBefore = ContainerHelper.contentFromSampleCode(containersBefore.get(i).contents,entry.getKey());
				List<Content> contentsAfter = ContainerHelper.contentFromSampleCode(containersBefore.get(i).contents,entry.getKey());
				
				for(Content contentB : contentsBefore){
					for(Content contentA:contentsAfter){
						assertThat(contentB.properties.get("taraStation").value).isEqualTo(contentA.properties.get("taraStation").value);
					}

				}
			}
		}

	}
	
	//@Test
	/*public void rulesRunCNSTest() throws RulesException{
		List<Object> list=new ArrayList<Object>();
		list.addAll(MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes)).toList());
		new RulesServices().callRules(ConfigFactory.load().getString("rules.key"),"rg_1",list);
		
		//calcul percentClusterIlluminaFilter
		List<Run> run=MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes).notExists("treatments.ngsrg.default.percentClusterIlluminaFilter")).toList();
		Assert.assertTrue(run.size()==0);
		
		run=MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,DBQuery.in("code", runCodes).notExists("lanes.treatments.ngsrg.default.seqLossPercent")).toList();
		Assert.assertTrue(run.size()==0);
		
		List<ReadSet> readSets=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,DBQuery.in("runCode", runCodes).notExists("treatments.ngsrg.default.validSeqPercent")).toList();
		Assert.assertTrue(readSets.size()==0);
	}*/

}
