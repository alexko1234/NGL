package data.resources;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;

import controllers.readsets.api.ReadSetValuation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;
import play.mvc.Result;
import utils.RunMockHelper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReadSetTest extends AbstractBIServerTest{

	static ReadSet readSet;
	static ReadSet readSetExt;
	static List<ReadSet> readSets;
	static String jsonReadSet;
	static String jsonReadSetExt;
	static Run run;

	@BeforeClass
	public static void initData()
	{	

		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.exists("properties.libProcessTypeCodes")).toList();
		for(Run runDB : runs){
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code)).toList();
			if(readSets.size()>0){
				run=runDB;
				break;
			}
		}
		//get JSON Run to insert
		//List<ReadSet> readSets  = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class).toList();
		readSet = readSets.remove(0);
		
		readSet._id=null;
		jsonReadSet = Json.toJson(readSet).toString();
		
		readSetExt = RunMockHelper.newReadSet("rdCode");
		readSetExt.runCode = run.code;
		jsonReadSetExt = Json.toJson(readSetExt).toString();
		
		//get run
		run = MongoDBDAO.findByCode("ngl_bi.RunIllumina_dataWF", Run.class, run.code);
		Logger.debug("RUN CODE "+run.code);
		MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		//insert project in collection
		for(String codeProjet : run.projectCodes){
			Project project = MongoDBDAO.findByCode("ngl_project.Project_dataWF", Project.class, codeProjet);
			MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		}

		//insert sampleCode
		for(String codeSample : run.sampleCodes){
			Sample sample = MongoDBDAO.findByCode("ngl_bq.Sample_dataWF", Sample.class, codeSample);
			MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME,sample);
		}
		for(ReadSet readSet : readSets){
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME,readSet);
		}

	}

	@AfterClass
	public static void deleteData()
	{
		if(MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code)!=null){
			MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		}
		for(ReadSet readSet: readSets){
			if(MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code)!=null){
				MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
			}
		}
		MongoDBDAO.deleteByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);

		for(String codeProjet : run.projectCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, codeProjet);
		}
		for(String sampleCode:run.sampleCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleCode);
		}

	}

	@Test
	public void test1save()
	{
		Logger.debug("save ReadSet");
		WSHelper.post(ws, "/api/readsets", jsonReadSet, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		Logger.debug("ReadSet "+readSet.code);
		assertThat(readSet).isNotNull();
	}
	
//	TODO (doit être mis à jour)
//	@Test
//	public void test1saveExt()
//	{
//		Sample sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "BFB_AABA");
//		Assert.assertNull(sample);
//		
//		
//		readSetExt.sampleCode="BFB_AABA";
//		readSetExt.projectCode="BFB";
//		readSetExt.sampleOnContainer=RunMockHelper.newSampleOnContainer(readSetExt.sampleCode);
//		
//		WSHelper.post(ws, "/api/readsets", jsonReadSetExt, 200);
//		readSetExt = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetExt.code);
//		Logger.debug("ReadSet "+readSetExt.code);
//		assertThat(readSetExt).isNotNull();
//		
//		//Check sample created
//		sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "BFB_AABA");
//		Assert.assertNotNull(sample);
//		
//		//Check sampleOnContainer created
//		readSetExt = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, "rdCode");
//		Logger.debug("Sample on container "+readSetExt.sampleOnContainer);
//		Assert.assertNotNull(readSetExt.sampleOnContainer);
//		Assert.assertNotNull(readSetExt.sampleOnContainer.referenceCollab);
//		Assert.assertNotNull(readSetExt.sampleOnContainer.sampleCategoryCode);
//
//		
//	}

	
	@Test
	public void test2list()
	{
		Logger.debug("list ReadSet");
		WSResponse response = WSHelper.get(ws, "/api/readsets", 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test3get()
	{
		Logger.debug("get ReadSet");
		WSResponse response = WSHelper.get(ws, "/api/readsets/"+readSet.code, 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test4head()
	{
		Logger.debug("head ReadSet");
		WSResponse response = WSHelper.head(ws, "/api/readsets/"+readSet.code, 200);
		assertThat(response).isNotNull();
	}

	@Test
	public void test5update()
	{
		Logger.debug("update ReadSet");
		Date date = new Date();
		readSet.archiveDate=date;
		WSHelper.putObject(ws, "/api/readsets/"+readSet.code,readSet, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.archiveDate).isEqualTo(date);
	}

	@Test
	public void test6valuation()
	{
		Logger.debug("valuation");
		//Create valuation
		
		ReadSetValuation readSetValuation = new ReadSetValuation();
		Valuation valuation = new Valuation();
		valuation.comment="test valuation";
		valuation.date=new Date();
		valuation.valid=TBoolean.FALSE;
		readSetValuation.bioinformaticValuation=valuation;
		readSetValuation.productionValuation=valuation;

		WSHelper.putObject(ws, "/api/readsets/"+readSet.code+"/valuation",readSetValuation, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.bioinformaticValuation.valid).isEqualTo(TBoolean.FALSE);
		assertThat(readSet.bioinformaticValuation.comment).isEqualTo("test valuation");
		assertThat(readSet.productionValuation.valid).isEqualTo(TBoolean.FALSE);
		assertThat(readSet.productionValuation.comment).isEqualTo("test valuation");
	}
	
//	TODO la propriété n'existe plus (doit être mis à jour)
//	@Test
//	public void test7properties()
//	{
//		Logger.debug("properties ReadSet");
//		
//		readSet.properties.put("isSentCCRT", new PropertySingleValue(true));
//		WSHelper.putObject(ws, "/api/readsets/"+readSet.code+"/properties",readSet, 200);
//		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
//		assertThat(readSet.properties.get("isSentCCRT").value).isEqualTo(true);
//	}

	@Test
	public void test8delete()
	{
		Logger.debug("delete ReadSet");
		WSHelper.delete(ws,"/api/readsets/"+readSet.code,200);
		ReadSet readSetDB = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSetDB).isNull();
	}

	@Test
	public void test9deleteByRunCode()
	{
		Logger.debug("delete ReadSet by runCode");
		WSHelper.delete(ws,"/api/runs/"+run.code+"/readsets",200);
		List<ReadSet> readSetDB = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,DBQuery.is("runCode", run.code)).toList();
		assertThat(readSetDB.size()==0);
	}
}
