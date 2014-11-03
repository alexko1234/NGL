package controllers.migration;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class UpdateFileNameFromReadSetName extends CommonController {


		
private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_20140827";
	
	
	public static Result migration(){
		
		Logger.info("Migration start");
		
		//JacksonDBCollection<ReadSet, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSet.class);
		//if(readSetsCollBck.count() == 0){
			Logger.info("Migration readset start");
			//backupReadSet();
			BasicDBObject keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("files", 1);
			keys.put("runCode", 1);
			keys.put("path", 1);
			keys.put("traceInformation.creationDate", 1);
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("code"), keys)
					.toList();
			Logger.debug("migre "+readSets.size()+" readSets");
			int j = 0;
			int k = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			for(ReadSet readSet : readSets){
				boolean isOk = true;
				String code = readSet.code;
				String[] codePart = code.split("_");
				List<File> files = readSet.files;
				//Pattern p = Pattern.compile("^"+codePart[0]+"_"+codePart[1]+"_"+codePart[2]+"(_[1,2])*_"+codePart[3]+".*");
				Pattern goodPattern = Pattern.compile("^"+codePart[0]+"_"+codePart[1]+"_"+codePart[2]+"_"+codePart[3]+"(_[1,2])*_"+codePart[4]+"((\\.[a-z]+){1,2})");
				Pattern badPattern = Pattern.compile("^"+codePart[0]+"_"+codePart[1]+"_"+codePart[2]+"_"+codePart[3]+"(_[1,2])*(_\\w+)((\\.[a-z]+){1,2})");
				
				for(File f : files){
					try{
						Matcher goodM = goodPattern.matcher(f.fullname);
						Matcher badM = badPattern.matcher(f.fullname);
						if(f.typeCode.equals("RAW") && !goodM.matches() && badM.matches()){
							isOk = false;
							String goodFileName =  codePart[0]+"_"+codePart[1]+"_"+codePart[2]+"_"+codePart[3]+(badM.group(1) !=null ?badM.group(1):"")+"_"+codePart[4]+badM.group(3);
							goodM = goodPattern.matcher(goodFileName);
							if(!goodM.matches()){
								Logger.error(readSet.runCode+","+code+","+f.fullname+","+goodFileName);
							}else{
								//Logger.info(code+","+f.fullname+","+goodFileName);
								f.fullname = goodFileName;
								Logger.info(readSet.runCode+","+code+","+readSet.path+f.fullname+","+sdf.format(readSet.traceInformation.creationDate));
							}
							
							j++;
							//Logger.info(readSet.runCode+","+code+","+readSet.path+f.fullname+","+sdf.format(readSet.traceInformation.creationDate));						
						}else if(!goodM.matches() && badM.matches()){						
							Logger.info(readSet.runCode+","+code+","+readSet.path+f.fullname+","+sdf.format(readSet.traceInformation.creationDate));
							j++;
						}
					}catch(Throwable t){
						Logger.error(code);
					}
				}
				
				if(!isOk){
					k++;
					//Logger.info("migre file of readset " +code+", nb files ="+readSet.files.size());
					migreReadSet(readSet);
				}				
				//				
			}
			Logger.info("Nb bad files "+j+" for nb readset "+k);
			Logger.info("Migration readset end");
						
		//}else{
		//	Logger.info("Migration readset already execute !");
		//}
		
		Logger.info("Migration finish");
		return ok("Migration Finish");

	}

	private static void migreReadSet(ReadSet readSet) {		
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("files", readSet.files));		
	}
	
	private static void backupReadSet() {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
		
	}
	
}

