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
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AnalysesTreatmentTest extends AbstractBIServerTest{

	static Analysis analysis;
	static String jsonAssemblyBA;
	
	@BeforeClass
	public static void initData()
	{	
		List<Analysis> analysisList = MongoDBDAO.find("ngl_bi.Analysis_dataWF", Analysis.class, DBQuery.exists("treatments.mergingBA").exists("treatments.assemblyBA")).toList();
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
		analysis.treatments.get("assemblyBA").results.get("pairs").put("software", new PropertySingleValue("software"));
		jsonAssemblyBA = Json.toJson(analysis.treatments.get("assemblyBA")).toString();
		analysis.treatments.remove("assemblyBA");
		MongoDBDAO.save(InstanceConstants.ANALYSIS_COLL_NAME, analysis);
		
		
		
	}
	
	@AfterClass
	public static void deleteData()
	{
		if(MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code)!=null){
			MongoDBDAO.deleteByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		}
		
		
		
	}
	
	@Test
	public void test1list()
	{
		Logger.debug("list AnalysesTreatment");
		WSResponse response = WSHelper.get(ws, "/api/analyses/"+analysis.code+"/treatments", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test2get()
	{
		Logger.debug("get AnalysesTreatment");
		WSResponse response = WSHelper.get(ws, "/api/analyses/"+analysis.code+"/treatments/mergingBA", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test3head()
	{
		Logger.debug("head AnalysesTreatment");
		WSResponse response = WSHelper.head(ws, "/api/analyses/"+analysis.code+"/treatments/mergingBA", 200);
		assertThat(response).isNotNull();
	}
	@Test
	public void test4save()
	{
		Logger.debug("save AnalysesTreatment");
		WSHelper.post(ws, "/api/analyses/"+analysis.code+"/treatments", jsonAssemblyBA, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.treatments.get("assemblyBA")).isNotNull();
	}
	
	@Test
	public void test5update()
	{
		Logger.debug("update AnalysesTreatment");
		//Get Treatment ngsrg
		Treatment assemblyBA = analysis.treatments.get("assemblyBA");
		assemblyBA.results.get("pairs").put("GCpercent", new PropertySingleValue(new Double(0)));
		WSHelper.putObject(ws, "/api/analyses/"+analysis.code+"/treatments/assemblyBA", assemblyBA, 200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.treatments.get("assemblyBA").results.get("pairs").get("GCpercent").getValue()).isEqualTo(new Double(0));
	}
	
	@Test
	public void test6delete()
	{
		Logger.debug("delete AnalysesTreatment");
		WSHelper.delete(ws,"/api/analyses/"+analysis.code+"/treatments/assemblyBA",200);
		analysis = MongoDBDAO.findByCode(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, analysis.code);
		assertThat(analysis.treatments.get("assemblyBA")).isNull();
	}
}
