package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//import controllers.readsets.api.ReadSetValuation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AnalysesTest extends AbstractBIServerTest{

	static Analysis analysis;
	static String jsonAnalysis;
	
	@BeforeClass
	public static void initData()
	{	
		List<Analysis> analysisList = MongoDBDAO.find("ngl_bi.Analysis_dataWF", Analysis.class).toList();
		for(Analysis a : analysisList){
			analysis=a;
			boolean existReadSetCode=true;
			for(String rsCode : a.masterReadSetCodes){
				if(MongoDBDAO.findByCode("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class,rsCode)==null)
					existReadSetCode=false;
			}
			if(existReadSetCode)
				break;
		}
		analysis._id=null;
		analysis.treatments.get("assemblyBA").results.get("pairs").put("software", new PropertySingleValue("software"));
		jsonAnalysis = Json.toJson(analysis).toString();
		//MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);
		
		//Save ReadSetCode
		for(String readSetCode : analysis.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, readSetCode);
			readSet.state.code="IW-BA";
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
		for(String readSetCode : analysis.readSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, readSetCode);
			readSet.state.code="IW-BA";
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
		
		for(String sampleCode : analysis.sampleCodes){
			Sample sample = MongoDBDAO.findByCode("ngl_bq.Sample_dataWF", Sample.class, sampleCode);
			MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME,sample);
		}
		for(String codeProjet : analysis.projectCodes){
			Project project = MongoDBDAO.findByCode("ngl_project.Project_dataWF", Project.class, codeProjet);
			MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		}
	}
	
	@AfterClass
	public static void deleteData()
	{
		if(MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code)!=null){
			MongoDBDAO.deleteByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		}
		
		for(String readSetCode: analysis.masterReadSetCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
		}
		
		for(String readSetCode: analysis.readSetCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
		}

		for(String codeProjet : analysis.projectCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, codeProjet);
		}
		for(String sampleCode:analysis.sampleCodes){
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleCode);
		}
	}
	
	@Test
	public void test1save()
	{
		Logger.debug("save Analysis");
		WSHelper.postAsBot(ws, "/api/analyses", jsonAnalysis, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis).isNotNull();
	}
	
	@Test
	public void test2list()
	{
		Logger.debug("list Analysis");
		WSResponse response = WSHelper.getAsBot(ws, "/api/analyses", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test3get()
	{
		Logger.debug("get Analysis");
		WSResponse response = WSHelper.getAsBot(ws, "/api/analyses/"+analysis.code, 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test4head()
	{
		Logger.debug("head Analysis");
		WSResponse response = WSHelper.headAsBot(ws, "/api/analyses/"+analysis.code, 200);
		assertThat(response).isNotNull();
	}
	
	@Test
	public void test5update()
	{
		Logger.debug("update Analyses");
		analysis.path="test";
		WSHelper.putObjectAsBot(ws, "/api/analyses/"+analysis.code,analysis, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.path).isEqualTo("test");
	}
	
	@Test
	public void test6valuation()
	{
		Logger.debug("valuation");
		//Create valuation
		
		Valuation valuation = new Valuation();
		valuation.comment="test valuation";
		valuation.date=new Date();
		valuation.valid=TBoolean.FALSE;

		WSHelper.putObjectAsBot(ws, "/api/analyses/"+analysis.code+"/valuation",valuation, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.valuation.valid).isEqualTo(TBoolean.FALSE);
		assertThat(analysis.valuation.comment).isEqualTo("test valuation");
	}
	
	@Test
	public void test7properties()
	{
		analysis.properties.put("test", new PropertySingleValue("test"));
		WSHelper.putObjectAsBot(ws, "/api/analyses/"+analysis.code+"/properties",analysis, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.properties.get("test").value).isEqualTo("test");
	}
	
	@Test
	public void test8delete()
	{
		Logger.debug("delete Analysis");
		WSHelper.deleteAsBot(ws,"/api/analyses/"+analysis.code,200);
		Analysis analysisDB = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysisDB).isNull();
	}
}
