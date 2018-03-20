package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
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
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import ngl.bi.AbstractBIServerTest;
//import play.Logger;
import play.libs.Json;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileReadSetTest extends AbstractBIServerTest {

	private static final play.Logger.ALogger logger = play.Logger.of(FileReadSetTest.class);
	
	static ReadSet       readSet;
	static File          file;
	static String        jsonFile;
	static List<ReadSet> readSets;
	static Run           run;
	
	@BeforeClass
	public static void initData() {	

		List<Run> runs  = MongoDBDAO.find("ngl_bi.RunIllumina_dataWF", Run.class, DBQuery.exists("properties.libProcessTypeCodes")).toList();
		for(Run runDB : runs){
			run = runDB;
			readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class, DBQuery.is("runCode", runDB.code).size("files", 2)).toList();
			if (readSets.size()>0) {
				break;
			}
		}
		
		//get JSON Run to insert
		//List<ReadSet> readSets  = MongoDBDAO.find("ngl_bi.ReadSetIllumina_dataWF", ReadSet.class).toList();
		readSet  = readSets.remove(0);
		file     = readSet.files.remove(0);
		jsonFile = Json.toJson(file).toString();
		
		MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		for (ReadSet r : readSets) {
			MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME,r);
		}
		
	}
	
	@AfterClass
	public static void deleteData()	{
		if (MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code) != null) {
			MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		}
		for (ReadSet readSet: readSets) {
			if (MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code) != null) {
				MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
			}
		}
		MongoDBDAO.deleteByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
	}
	
	@Test
	public void test1list() {
		logger.debug("list File");
		WSResponse response = WSHelper.getAsBot(ws, "/api/readsets/"+readSet.code+"/files", 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test2get() {
		logger.debug("get File");
		WSResponse response = WSHelper.getAsBot(ws, "/api/readsets/"+readSet.code+"/files/"+readSet.files.get(0).fullname, 200);
		assertThat(response.asJson()).isNotNull();
	}
	
	@Test
	public void test3head()	{
		logger.debug("head File");
		WSResponse response = WSHelper.headAsBot(ws, "/api/readsets/"+readSet.code+"/files/"+readSet.files.get(0).fullname, 200);
		assertThat(response).isNotNull();
	}
	
	// failed : POST /api/readsets/ARG_AENOSW_1_64MD3AAXX.IND12/files 
	// {"properties.label.value":["PropriÚtÚ obligatoire"],"properties.asciiEncoding.value":["PropriÚtÚ obligatoire"]}
	@Test
	public void test4save()	{
		logger.debug("save File");
		logger.debug("json {}", jsonFile);
		WSHelper.postAsBot(ws, "/api/readsets/" + readSet.code + "/files", jsonFile, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		logger.debug("ReadSet "+readSet.code);
		assertThat(readSet.files.size()).isEqualTo(2);
	}
	
	// failed: expected:<'[test]'> but was:<'[fastq.gz]'>
	@Test
	public void test5update() {
		logger.debug("update File");
		//Get Treatment ngsrg
		File file = readSet.files.get(0);
		file.extension = "test";
		WSHelper.putObjectAsBot(ws, "/api/readsets/" + readSet.code + "/files/" + file.fullname, file, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		for (File f : readSet.files)
			logger.debug("readSet file {}",f.fullname);
		assertThat(readSet.files.get(0).extension).isEqualTo("test");
	}
	
	// failed: DELETE /api/readsets/ARG_AENOSW_1_64MD3AAXX.IND12/files/ARG_AENOSW_1_1_64MD3AAXX.IND12.fastq.gz  expected:<200> but was:<400>
	@Test
	public void test6Delete() {
		logger.debug("delete File");
		WSHelper.deleteAsBot(ws,"/api/readsets/" + readSet.code + "/files/" + file.fullname, 200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.files.size()).isEqualTo(1);
	}
	
	@Test
	public void test7DeleteByReadSet() {
		logger.debug("delete File");
		WSHelper.deleteAsBot(ws,"/api/readsets/"+readSet.code+"/files",200);
		readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSet.code);
		assertThat(readSet.files).isNull();
	}
	
	@Test
	public void test8DeleteByRun() {
		logger.debug("delete File");
		WSHelper.deleteAsBot(ws,"/api/runs/"+run.code+"/files",200);
		for(ReadSet readSetList : readSets){
			ReadSet readSetDB = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetList.code);
			assertThat(readSetDB.files).isNull();
		}
	}
	
}
