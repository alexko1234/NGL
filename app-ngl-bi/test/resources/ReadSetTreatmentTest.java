package resources;

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
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReadSetTreatmentTest extends AbstractBIServerTest{

	static List<ReadSet> readSets;
	static ReadSet readSet;
	static String jsonTaxonomy;
	
	@BeforeClass
	public static void initData()
	{	

		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.exists("properties.libProcessTypeCodes")).toList();
		for(Run runDB : runs){
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code).exists("treatments.ngsrg").exists("treatments.taxonomy")).toList();
			if(readSets.size()>0){
				break;
			}
		}
		//get JSON Run to insert
		//List<ReadSet> readSets  = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class).toList();
		readSet = readSets.remove(0);
		jsonTaxonomy=Json.toJson(readSet.treatments.get("taxonomy")).toString();
		readSet.treatments.remove("taxonomy");
		MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		
	}
	
	@AfterClass
	public static void deleteData()
	{
		if(MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code)!=null){
			MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		}
	}
	
	@Test
	public void test1list()
	{
		Logger.debug("list ReadSetTreatment");
		WSResponse response = WSHelper.getAsBot(ws, "/api/readsets/"+readSet.code+"/treatments", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test2get()
	{
		Logger.debug("get ReadSetTreatment");
		WSResponse response = WSHelper.getAsBot(ws, "/api/readsets/"+readSet.code+"/treatments/ngsrg", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test3head()
	{
		Logger.debug("head ReadSetTreatment");
		WSResponse response = WSHelper.headAsBot(ws, "/api/readsets/"+readSet.code+"/treatments/ngsrg", 200);
		assertThat(response).isNotNull();
	}
	
	@Test
	public void test4save()
	{
		Logger.debug("save ReadSetTreatment");
		WSHelper.postAsBot(ws, "/api/readsets/"+readSet.code+"/treatments", jsonTaxonomy, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		Logger.debug("ReadSet "+readSet.code);
		assertThat(readSet.treatments.get("taxonomy")).isNotNull();
	}
	
	@Test
	public void test5update()
	{
		Logger.debug("update ReadSetTreatment");
		//Get Treatment ngsrg
		Treatment taxonomy = readSet.treatments.get("taxonomy");
		taxonomy.results.get("read1").put("software", new PropertySingleValue("kraken"));
		WSHelper.putObjectAsBot(ws, "/api/readsets/"+readSet.code+"/treatments/taxonomy", taxonomy, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.treatments.get("taxonomy").results.get("read1").get("software").getValue()).isEqualTo("kraken");
	}
	
	@Test
	public void test6delete()
	{
		Logger.debug("delete ReadSetTreatment");
		WSHelper.deleteAsBot(ws,"/api/readsets/"+readSet.code+"/treatments/taxonomy",200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.treatments.get("taxonomy")).isNull();
	}
}
