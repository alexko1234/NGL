package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunTest extends AbstractBIServerTest{

	static Run run;
	static ContainerSupport containerSupport;
	static List<ReadSet> readSets;
	static List<Container> containers;
	static String jsonRun;

	@BeforeClass
	public static void initData()
	{
		//get JSON Run to insert
		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.exists("properties.libProcessTypeCodes")).toList();
		for(Run runDB : runs){
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code)).toList();
			if(readSets.size()>0){
				run=runDB;
				break;
			}
		}
		run._id=null;
		run.state.code="IP-S";
		jsonRun = Json.toJson(run).toString();
		Logger.debug("Run code "+run.code);
		//insert project in collection
		for(String codeProjet : run.projectCodes){
			Project project = MongoDBDAO.findByCode("ngl_project.Project_dataWF", Project.class, codeProjet);
			MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		}
		//insert containerSupportCode
		containerSupport = MongoDBDAO.findByCode("ngl_sq.ContainerSupport_dataWF", ContainerSupport.class, run.containerSupportCode);
		MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,containerSupport);
		//insert sampleCode
		for(String codeSample : run.sampleCodes){
			Sample sample = MongoDBDAO.findByCode("ngl_bq.Sample_dataWF", Sample.class, codeSample);
			MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME,sample);
		}

		//insert containers
		containers = MongoDBDAO.find("ngl_sq.Container_dataWF", Container.class, DBQuery.is("support.code", containerSupport.code)).toList();
		for(Container container: containers){
			MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,container);
		}
		//insert readSets
		//readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", run.code)).toList();
		for(ReadSet readSet : readSets){
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME,readSet);
		}

	}

	@AfterClass
	public static void deleteData()
	{
		if(MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code)!=null){
			MongoDBDAO.deleteByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
			for(ReadSet readSet: readSets){
				MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
			}
		}
		for(String codeProjet : run.projectCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, codeProjet);
		}
		MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, containerSupport.code);
		for(Container container : containers){
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		for(String sampleCode:run.sampleCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleCode);
		}

	}

	@Test
	public void test1saveRun() throws InterruptedException
	{
		Logger.debug("save Run");
		WSHelper.postAsBot(ws, "/api/runs", jsonRun, 200);
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		Logger.debug("Run "+run.code);
		assertThat(run).isNotNull();
		
	}

	@Test
	public void test2updateRun()
	{
		Logger.debug("save Run");
		Date date = new Date();
		run.sequencingStartDate=date;
		//run.properties.remove("libProcessTypeCodes");
		WSHelper.putObjectAsBot(ws, "/api/runs/"+run.code,run, 200);
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		assertThat(run.sequencingStartDate).isEqualTo(date);
		//assertThat(run.properties.get("libProcessTypeCodes")).isNull();
	}

	@Test
	public void test3listRun()
	{
		Logger.debug("save Run");
		WSResponse response = WSHelper.getAsBot(ws, "/api/runs", 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test4getRun()
	{
		Logger.debug("get Run");
		WSResponse response = WSHelper.getAsBot(ws, "/api/runs/"+run.code, 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test5headRun()
	{
		Logger.debug("head Run");
		WSResponse response = WSHelper.headAsBot(ws, "/api/runs/"+run.code, 200);
		assertThat(response).isNotNull();
	}

	@Test
	public void test6GetState()
	{
		Logger.debug("get State");
		WSResponse response = WSHelper.getAsBot(ws, "/api/runs/"+run.code+"/state", 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test7GetStateHistorical()
	{
		Logger.debug("get State historical");
		WSResponse response = WSHelper.getAsBot(ws, "/api/runs/"+run.code+"/state/historical", 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test8Valuation()
	{
		Logger.debug("valuation");
		//Create valuation
		Valuation valuation = new Valuation();
		valuation.comment="test valuation";
		valuation.date=new Date();
		valuation.valid=TBoolean.FALSE;

		WSHelper.putObjectAsBot(ws, "/api/runs/"+run.code+"/valuation",valuation, 200);
		run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		assertThat(run.valuation.valid).isEqualTo(TBoolean.FALSE);
		assertThat(run.valuation.comment).isEqualTo("test valuation");
	}

	/**
	 * TODO Ne peut etre testé
	 */
	//@Test
	/*public void test9ApplyRules()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("apply rules");
					//Set state to IPS
					State state = new State("IP-S","bot");
					WSHelper.put(ws, "/api/runs/"+run.code+"/state", RunMockHelper.getJsonState(state).toString(), 200);
					WSResponse response = WSHelper.put(ws, "/api/runs/"+run.code+"/apply-rules/IP_S_1","{}",200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					Logger.debug("Run "+Json.toJson(run.properties));
					//assertThat(run.properties.get("libProcessTypeCodes")).isNotNull();
				});
	}*/

	@Test
	public void test9DeleteRun()
	{
		Logger.debug("delete Run");
		WSHelper.deleteAsBot(ws,"/api/runs/"+run.code,200);
		Run runDB = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
		assertThat(runDB).isNull();
	}




}
