package controllers.migration;		

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.LaneOld;
import controllers.migration.models.PropertyValueOld;
import controllers.migration.models.ReadSetOld;
import controllers.migration.models.RunOld;
import fr.cea.ig.MongoDBDAO;

public class Migration extends CommonController {
	
	private static final String RUN_ILLUMINA_COLL_NAME_BCKP = "run.illumina.backup";
	private static final String RUN_ILLUMINA_COLL_NAME_OLD = "run.illumina";
	private static int nbReadSet = 0;
	
	public static Result migration(){
		nbReadSet = 0;
		JacksonDBCollection<RunOld, String> runsCollBck = MongoDBDAO.getCollection(RUN_ILLUMINA_COLL_NAME_BCKP, RunOld.class);
		if(runsCollBck.count() == 0){
			Logger.info("Migration start");
			backup();
			List<RunOld> runs = MongoDBDAO.find(RUN_ILLUMINA_COLL_NAME_BCKP, RunOld.class).toList();
			Logger.debug("migre "+runs.size()+" runs");
			DynamicForm f = Form.form();
			ContextValidation contextValidation = new ContextValidation(f.errors());
			for(RunOld run : runs){
				contextValidation.addKeyToRootKeyName(run.code);
				migre(run, contextValidation);
				contextValidation.removeKeyFromRootKeyName(run.code);
			}
			if(!contextValidation.hasErrors()){
				check();
				Logger.info("Migration finish");
				return ok("Migration Finish");
			}else{
				return badRequest(Json.toJson(f.errorsAsJson()));
			}
			
			
		}else{
			return ok("Migration already execute !");
		}
	}

	private static void check() {
		int nbOldRun = MongoDBDAO.find(RUN_ILLUMINA_COLL_NAME_BCKP, RunOld.class).count();
		int nbRun = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).count();
		if(nbOldRun != nbRun){
			Logger.error("not the same run number "+nbOldRun+" / "+nbRun);
		}else{
			Logger.info("Ok insert "+nbRun+" run");
		}
		
		int nbReadSetColl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).count();
		if(nbReadSetColl != nbReadSet){
			Logger.error("not the same readset number "+nbReadSet+" / "+nbReadSetColl);
		}else{
			Logger.info("Ok insert "+nbReadSetColl+" readset");
		}
	}

	private static void migre(RunOld runOld, ContextValidation contextValidation) {
		Run run = convert(runOld);
		List<ReadSet> readSets = new ArrayList<ReadSet>();
		if(null != runOld.lanes){
			if(runOld.lanes.size() < 8){
				Logger.warn("RUN :"+run.code+" few lanes = "+runOld.lanes.size()+" instrument = "+runOld.instrumentUsed.categoryCode);
			}
			
			
			for(LaneOld laneOld: runOld.lanes){
				List<String> readsetCodes = new ArrayList<String>();
				for(ReadSetOld readSetOld : laneOld.readsets){
					//readsetCodes.add(readSetOld.code);
					readSets.add(convert(runOld, laneOld, readSetOld));
				}
				run.lanes.add(convert(laneOld, readsetCodes, runOld));
			}
		}else{
			Logger.error("RUN :"+run.code+" without Lanes");
		}
		
		run.validate(contextValidation);
		if(!contextValidation.hasErrors()){
			MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
			Logger.debug("save "+readSets.size()+" readsets");
			nbReadSet += readSets.size();
			for(ReadSet rs : readSets){
				rs.validate(contextValidation);
				if(!contextValidation.hasErrors()){
					MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, rs);
					updateLane(rs);					
				}
			}			
		}
	}

	private static void updateLane(ReadSet rs) {
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", rs.runCode), DBQuery.is("lanes.number", rs.laneNumber)), 
				DBUpdate.push("lanes.$.readSetCodes", rs.code));
	}

	private static Run convert(RunOld runOld) {
		Run run = new Run();
		run._id = runOld._id;
		run.code = runOld.code;
		run.containerSupportCode = runOld.containerSupportCode;
		run.dispatch = runOld.dispatch;
		run.instrumentUsed = runOld.instrumentUsed;
		//run.properties = runOld.properties;
		run.treatments = convertRunPropsToTreatment(runOld.properties);
		run.traceInformation = runOld.traceInformation;
		
		if(run.containerSupportCode.endsWith("DXX")){
			run.typeCode = "RHS2500R";
			run.instrumentUsed.categoryCode ="HISEQ2500";
		}else if(run.instrumentUsed.categoryCode.equals("HISEQ2000")){
			run.typeCode = "RHS2000";
		}else if(run.instrumentUsed.categoryCode.equals("HISEQ2500")){
			run.typeCode = "RHS2500";
		}
		run.lanes = new ArrayList<Lane>();
		if(run.dispatch){
			run.stateCode = "F";
		}else{
			run.stateCode = "IP-RG";
		}
		return run;
	}

	private static Map<String, Treatment> convertRunPropsToTreatment(
			Map<String, controllers.migration.models.PropertyValueOld> properties) {
		Map<String, Treatment> map = new HashMap<String, Treatment>();
		
		Treatment t = new Treatment();
		t.code = "ngsrg";
		t.categoryCode = "ngsrg";
		t.typeCode = "ngsrg-illumina";
		t.results = new HashMap<String, Map<String, PropertyValue>>();
		t.results.put("default", convert(properties));
		map.put(t.code, t);
		return map;
	}

	private static Lane convert(LaneOld laneOld, List<String> readsetCodes, RunOld runOld) {
		Lane lane = new Lane();
		lane.number = laneOld.number;
		//lane.properties = laneOld.properties;
		lane.treatments = convertRunPropsToTreatment(laneOld.properties);
		//lane.readSetCodes = readsetCodes;	
		lane.stateCode = "F";
		
		if(runOld.dispatch){
			lane.stateCode = "F";
		}else{
			lane.stateCode = "IP-RG";
		}
		return lane;
	}

	private static ReadSet convert(RunOld runOld, LaneOld laneOld, ReadSetOld readSetOld) {
		ReadSet readSet = new ReadSet();
		readSet.typeCode = "default-readset";
		readSet.traceInformation = runOld.traceInformation;
		readSet.archiveDate = readSetOld.archiveDate;
		readSet.archiveId = readSetOld.archiveId;
		readSet.code = readSetOld.code;
		
		readSet.path = readSetOld.path;
		readSet.sampleCode = readSetOld.sampleCode;
		readSet.sampleContainerCode = readSetOld.sampleContainerCode;
		readSet.projectCode = readSetOld.projectCode;
		//readSet.properties = readSetOld.properties;
		readSet.treatments = convertReadSetPropsToTreatment(readSetOld.properties);
		readSet.laneNumber = laneOld.number; // new
		readSet.runCode = runOld.code; // new
		readSet.dispatch = runOld.dispatch; // new
		
		if(readSet.dispatch){
			readSet.stateCode = "A";
		}else{
			readSet.stateCode = "IP-RG";
		}
		readSet.files = convert(readSetOld.files);
		for(File file : readSet.files){
			file.stateCode = readSet.stateCode;
		}
		
		return readSet;
	}

	
	private static List<File> convert(
			List<controllers.migration.models.File> fileOlds) {
		List<File> files = new ArrayList<File>();
		
		for(controllers.migration.models.File fileOld : fileOlds){
			File file = new File();
			file.extension = fileOld.extension;
			file.fullname = fileOld.fullname;
			file.typeCode = fileOld.typeCode;
			file.usable = fileOld.usable;
			file.properties = convert(fileOld.properties);
		}
		
		return files;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, models.laboratory.common.instance.PropertyValue> convert(
			Map<String, PropertyValueOld> propertiesOld) {
		
		Map<String, PropertyValue> props = new HashMap<String, PropertyValue>();
		
		for(Entry<String, PropertyValueOld> propOld : propertiesOld.entrySet()){
			PropertyValue prop = new PropertySingleValue();
			prop.value = propOld.getValue().value;
			
			props.put(propOld.getKey(), prop);
		}
		
		
		return props;
	}

	private static Map<String, Treatment> convertReadSetPropsToTreatment(
			Map<String, controllers.migration.models.PropertyValueOld> properties) {
		Map<String, Treatment> map = new HashMap<String, Treatment>();
		
		Treatment ngsrgt = new Treatment();
		ngsrgt.code = "ngsrg";
		ngsrgt.categoryCode = "ngsrg";
		ngsrgt.typeCode = "ngsrg-illumina";
		ngsrgt.results = new HashMap<String, Map<String, PropertyValue>>();
		ngsrgt.results.put("default", new HashMap<String, PropertyValue>());
		
		Treatment globalt = new Treatment();
		globalt.code = "global";
		globalt.categoryCode = "global";
		globalt.typeCode = "global";
		globalt.results = new HashMap<String, Map<String, PropertyValue>>();
		globalt.results.put("default", new HashMap<String, PropertyValue>());
		
		
		
		for(Map.Entry<String, PropertyValue> entry : convert(properties).entrySet()){
			if("nbUsableBase".equals(entry.getKey()) || "nbUsableCluster".equals(entry.getKey())){
				globalt.results.get("default").put(convertKey(entry.getKey()), entry.getValue());
			}else if(!"insertLength".equals(entry.getKey())){
				ngsrgt.results.get("default").put(convertKey(entry.getKey()), entry.getValue());
			}
		}
		
		map.put(ngsrgt.code, ngsrgt);
		map.put(globalt.code, globalt);
		return map;
	}
	
	static Map<String, String> propertyKeys;
	
	private static String convertKey(String key) {
		if(null == propertyKeys){
			propertyKeys = new HashMap<String, String>();
			propertyKeys.put("nbClusterInternalAndIlluminaFilter","nbCluster");
			propertyKeys.put("nbBaseInternalAndIlluminaFilter","nbBases");
			propertyKeys.put("fraction", "fraction");
			propertyKeys.put("nbUsableBase", "usefulBases");
			propertyKeys.put("nbUsableCluster", "usefulSequences");
			propertyKeys.put("q30", "Q30");
			propertyKeys.put("score", "qualityScore");
			propertyKeys.put("nbRead", "nbReadIllumina");						
		}
		if(propertyKeys.containsKey(key)){
			return propertyKeys.get(key);
		}else{
			throw new RuntimeException("not exist "+key);
		}
	}

	private static void backup() {
		Logger.info("\tCopie "+RUN_ILLUMINA_COLL_NAME_OLD+" start");
		MongoDBDAO.save(RUN_ILLUMINA_COLL_NAME_BCKP, MongoDBDAO.find(RUN_ILLUMINA_COLL_NAME_OLD, RunOld.class).toList());
		MongoDBDAO.getCollection(RUN_ILLUMINA_COLL_NAME_OLD, RunOld.class).drop();
		Logger.info("\tCopie "+RUN_ILLUMINA_COLL_NAME_OLD+" end");
	}

}
