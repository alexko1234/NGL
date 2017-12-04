package resources;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.bi.Global.devapp;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;
import utils.AbstractTests;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunTreatmentTest extends AbstractTests{

	static Run run;
	static List<ReadSet> readSets;
	static String jsonTopIndex;

	@BeforeClass
	public static void initData()
	{
		//get JSON Run to insert
		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.exists("treatments.ngsrg").exists("treatments.topIndex")).toList();
		for(Run runDB : runs){
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code)).toList();
			if(readSets.size()>0){
				run=runDB;
				break;
			}
		}
		//Insert run
		jsonTopIndex=Json.toJson(run.treatments.get("topIndex")).toString();
		run.treatments.remove("topIndex");
		MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);


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

	}

	@Test
	public void test1list()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("list RunTreatment");
					WSResponse response = WSHelper.get(ws, "/api/runs/"+run.code+"/treatments", 200);
					assertThat(response.asJson()).isNotNull();
				});
	}

	@Test
	public void test2get()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("get RunTreatment");
					WSResponse response = WSHelper.get(ws, "/api/runs/"+run.code+"/treatments/ngsrg", 200);
					assertThat(response.asJson()).isNotNull();
				});
	}

	@Test
	public void test3head()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("head RunTreatment");
					WSResponse response = WSHelper.head(ws, "/api/runs/"+run.code+"/treatments/ngsrg", 200);
					assertThat(response).isNotNull();
				});
	}

	@Test
	public void test4save()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("save RunTreatment");
					WSHelper.post(ws, "/api/runs/"+run.code+"/treatments", jsonTopIndex, 200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					Logger.debug("Run "+run.code);
					assertThat(run.treatments.get("topIndex")).isNotNull();
				});
	}

	@Test
	public void test5update()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("update RunTreatment");
					//Get Treatment ngsrg
					Treatment ngsrg = run.treatments.get("ngsrg");
					ngsrg.results.get("default").put("flowcellVersion", new PropertySingleValue("testVersion"));
					WSHelper.put(ws, "/api/runs/"+run.code+"/treatments/ngsrg", Json.toJson(ngsrg).toString(), 200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.treatments.get("ngsrg").results.get("default").get("flowcellVersion").getValue()).isEqualTo("testVersion");
				});
	}

	
	@Test
	public void test6delete()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("delete RunTreatment");
					WSHelper.delete(ws,"/api/runs/"+run.code+"/treatments/topIndex",200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.treatments.get("topIndex")).isNull();
				});
	}
}
