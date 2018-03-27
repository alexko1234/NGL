package data.resources;

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
// import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
//import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileAnalysesTest extends AbstractBIServerTest{

	private static final play.Logger.ALogger logger = play.Logger.of(FileAnalysesTest.class);
	
	static Analysis analysis;
	static File file;
	static String jsonFile;
	
	@BeforeClass
	public static void initData() {	
		List<Analysis> analysisList = MongoDBDAO.find("ngl_bi.Analysis_dataWF", Analysis.class, DBQuery.size("files", 2)).toList();
		for (Analysis a : analysisList) {
			analysis=a;
			boolean existReadSetCode=true;
			for(String rsCode : a.masterReadSetCodes){
				if(MongoDBDAO.findByCode("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class,rsCode)==null)
					existReadSetCode=false;
			}
			if(existReadSetCode)
				break;
		}
		file = analysis.files.remove(0);
		jsonFile = Json.toJson(file).toString();
		MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);
	}
	
	@AfterClass
	public static void deleteData()
	{
		if (MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code) != null) {
			MongoDBDAO.deleteByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		}
	}
	
	@Test
	public void test1list()
	{
		logger.debug("list File");
//		WSResponse response = WSHelper.get(ws, "/api/analyses/"+analysis.code+"/files", 200);
		WSResponse response = ws.get("/api/analyses/"+analysis.code+"/files", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test2get()
	{
		logger.debug("get File");
//		WSResponse response = WSHelper.get(ws, "/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname, 200);
		WSResponse response = ws.get("/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname, 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test3head()
	{
		logger.debug("head File");
//		WSResponse response = WSHelper.head(ws, "/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname, 200);
		WSResponse response = ws.head("/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname, 200);
		assertThat(response).isNotNull();
	}
	
	@Test
	public void test4save()
	{
		logger.debug("save File");
//		WSHelper.postAsBot(ws, "/api/analyses/"+analysis.code+"/files", jsonFile, 200);
		wsBot.post("/api/analyses/"+analysis.code+"/files", jsonFile, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		logger.debug("Analysis "+analysis.code);
		assertThat(analysis.files.size()).isEqualTo(2);
	}
	
	@Test
	public void test5update()
	{
		logger.debug("update File");
		//Get Treatment ngsrg
		File file = analysis.files.get(0);
		file.extension="test";
//		WSHelper.putObjectAsBot(ws, "/api/analyses/"+analysis.code+"/files/"+file.fullname, file, 200);
		wsBot.putObject("/api/analyses/"+analysis.code+"/files/"+file.fullname, file, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.files.get(0).extension).isEqualTo("test");
	}
	
	@Test
	public void test6Delete()
	{
		logger.debug("delete File");
//		WSHelper.deleteAsBot(ws,"/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname,200);
		wsBot.delete("/api/analyses/"+analysis.code+"/files/"+analysis.files.get(0).fullname,200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.files.size()).isEqualTo(1);
	}
	
	@Test
	public void test7DeleteByAnalyses()
	{
		logger.debug("delete File");
//		WSHelper.deleteAsBot(ws,"/api/analyses/"+analysis.code+"/files",200);
		wsBot.delete("/api/analyses/"+analysis.code+"/files",200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.files).isNull();
	}
	
}
