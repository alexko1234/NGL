package resources;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.bi.Global.devapp;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
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
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;
import utils.AbstractTests;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LaneTest extends AbstractTests{

	static Run run;
	static List<ReadSet> readSets;
	static String laneJson;

	@BeforeClass
	public static void initData()
	{
		//get JSON Run to insert
		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.size("lanes", 8)).toList();
		for(Run runDB : runs){
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code)).toList();
			if(readSets.size()>0){
				run=runDB;
				break;
			}
		}
		//Get lane 8
		List<Lane> newLanes =new ArrayList<Lane>();
		for(Lane lane : run.lanes){
			if(lane.number==8)
				laneJson = Json.toJson(lane).toString();
			else
				newLanes.add(lane);
		}
		run.lanes=newLanes;

		//Insert run
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
					Logger.debug("list Lane");
					WSResponse response = WSHelper.get(ws, "/api/runs/"+run.code+"/lanes", 200);
					assertThat(response.asJson()).isNotNull();
				});
	}

	@Test
	public void test2get()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("get Lane");
					WSResponse response = WSHelper.get(ws, "/api/runs/"+run.code+"/lanes/1", 200);
					assertThat(response.asJson()).isNotNull();
				});
	}

	@Test
	public void test3head()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("head Lane");
					WSResponse response = WSHelper.head(ws, "/api/runs/"+run.code+"/lanes/1", 200);
					assertThat(response).isNotNull();
				});
	}

	@Test
	public void test4deleteNumber()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("delete Lane number");
					WSHelper.delete(ws,"/api/runs/"+run.code+"/lanes/7",200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.lanes.size()).isEqualTo(6);
				});
	}

	@Test
	public void test5save()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("save Lane with json "+laneJson);
					WSHelper.post(ws, "/api/runs/"+run.code+"/lanes", laneJson, 200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.lanes.size()).isEqualTo(7);
				});
	}

	@Test
	public void test6update()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("update Lane");
					//Get Run
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					Date date = new Date();
					Lane lane = run.lanes.get(0);
					lane.valuation.date=date;
					WSHelper.put(ws, "/api/runs/"+run.code+"/lanes/"+run.lanes.get(0).number, Json.toJson(lane).toString(), 200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.lanes.get(0).valuation.date).isEqualTo(date);
				});
	}

	@Test
	public void test7valuation()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("valuation Lane");
					Valuation valuation = new Valuation();
					valuation.comment="test valuation";
					valuation.date=new Date();
					valuation.valid=TBoolean.FALSE;
					WSHelper.put(ws, "/api/runs/"+run.code+"/lanes/"+run.lanes.get(0).number+"/valuation", Json.toJson(valuation).toString(), 200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.lanes.get(0).valuation.comment).isEqualTo("test valuation");
					assertThat(run.lanes.get(0).valuation.valid).isEqualTo(TBoolean.FALSE);
				});
	}

	@Test
	public void test8delete()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("delete Lane number");
					WSHelper.delete(ws,"/api/runs/"+run.code+"/lanes",200);
					run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
					assertThat(run.lanes).isNull();
				});
	}
}
