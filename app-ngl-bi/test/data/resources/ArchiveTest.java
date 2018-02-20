package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.test.WSHelper;
//import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
//import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArchiveTest extends AbstractBIServerTest {

	static List<ReadSet> readSets;
	static ReadSet readSet;
	
	@BeforeClass
	public static void initData() {	

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
		readSet.treatments= new HashMap<String,Treatment>();
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
	public void test1update()
	{
		Logger.debug("update Archive");
		//Get Treatment ngsrg
		readSet.archiveId="testArchive";
		WSHelper.putObject(ws, "/api/archives/readsets/"+readSet.code, readSet, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.archiveId).isEqualTo("testArchive");
	}
	
	@Test
	public void test2list()
	{
		Logger.debug("list Archive");
		WSResponse response = WSHelper.get(ws, "/api/archives/readsets", 200);
		assertThat(response.asJson()).isNotNull();
	}
}
