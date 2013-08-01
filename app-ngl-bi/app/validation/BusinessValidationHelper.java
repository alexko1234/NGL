package validation;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;
import static validation.utils.ConstraintsHelper.validateProperties;
import static validation.utils.ConstraintsHelper.validateTraceInformation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import play.data.validation.ValidationError;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Helper to validate MongoDB Object Used before insert or update a MongoDB
 * object
 * 
 * @author galbini
 * 
 */
public class BusinessValidationHelper {
	private static final String ERROR_NOTUNIQUE = "error.codenotunique";
	public static final String FIELD_TRACE_INFORMATION = "traceInformation";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_TYPE_CODE = "typeCode";
	public static final String FIELD_SUPPORT_CODE = "containerSupportCode";

	/**
	 * Validate the code of a mongodb object
	 * Check is code is not null and unique
	 * 
	 * @param errors
	 * @param dbObject
	 * @param collectionName
	 */
	private static void validateCode(Map<String, List<ValidationError>> errors,
			DBObject dbObject, String collectionName, Class<?> type) {
		// validation of unique code
		if(required(errors, dbObject.code, "code")){
			try {
				DBObject o = (DBObject)MongoDBDAO.findByCode(collectionName, type, dbObject.code);
				if(null != o && !o._id.equals(dbObject._id)){
					addErrors(errors, FIELD_CODE, ERROR_NOTUNIQUE, dbObject.code);
				}
							
			} catch (MongoException e) {
				addErrors(errors, FIELD_CODE, ERROR_NOTUNIQUE, dbObject.code);
			}
		}		
	}

	private static void validationType(
			Map<String, List<ValidationError>> errors, String typeCode) {
		if(required(errors, typeCode, "typeCode")){
			//TODO add controles si le type existe intérrogation de la partie sgbd
		}		
	}
	
	private static void validationContainerSupportCode(
			Map<String, List<ValidationError>> errors,
			String containerSupportCode) {		
		if(required(errors, containerSupportCode, "containerSupportCode")){
			//TODO add controles si le container existe dans mongo db
		}		 
	}
	
	private static void validateInstrumentUsed(
			Map<String, List<ValidationError>> errors,
			InstrumentUsed instrumentUsed) {
		if(required(errors, instrumentUsed, "instrumentUsed")){
			if(required(errors, instrumentUsed.code, "instrumentUsed.code")){
				//TODO valid if exist
			}
			if(required(errors, instrumentUsed.categoryCode, "instrumentUsed.categoryCode")){
				//TODO valid if exist
			}
		}		
	}

	/**
	 * @param errors
	 * @param run
	 * @param collectionName
	 */
	public static void validateRun(Map<String, List<ValidationError>> errors,
			Run run, String collectionName) {
		if(null == run ){
			throw new IllegalArgumentException("run is null");
		}
		validateCode(errors, run, collectionName, Run.class);
		validateTraceInformation(errors, run.traceInformation, run._id);
		validationType(errors, run.typeCode);
		validationContainerSupportCode(errors, run.containerSupportCode);
		validateInstrumentUsed(errors, run.instrumentUsed);
		validateLanes(errors, run,run.lanes, collectionName, "lanes");
		validateProperties(errors, run.properties, RunPropertyDefinitionHelper.getRunPropertyDefinitions(), "properties");
	}
	
	public static void validateLanes(Map<String, List<ValidationError>> errors,Run run,
			List<Lane> lanes, String collectionName, String rootKeyName) {		
		//TODO number of lanes (depends of the type run and the mode incremental insert or full insert !!!)
		//TODO validate lane number
		if(null != lanes){
			int index = 0;			
			Set<Integer> laneNumbers = new TreeSet<Integer>();
			for (Lane lane : lanes) {
				validateLane(errors,run, lane, collectionName, rootKeyName+"["+index+++"]");
				if(laneNumbers.contains(lane.number)){
					addErrors(errors,getKey(rootKeyName,"number"),ERROR_NOTUNIQUE,lane.number);
				}				
				laneNumbers.add(lane.number);				
			}
		}
	}

	public static void validateLane(Map<String, List<ValidationError>> errors,
			Run run,Lane lane, String collectionName, String rootKeyName) {
		if(null == run || null == lane){
			throw new IllegalArgumentException("run or lane is null");
		}
		if(required(errors, lane.number, getKey(rootKeyName,"number"))){
			//Validate unique lane.number if run already exist
			if(null != run._id){
				Run runExist = MongoDBDAO.findOne(collectionName, Run.class, DBQuery.is("_id", run._id).is("lanes.number", lane.number));
				if(runExist != null){
					addErrors(errors,getKey(rootKeyName,"code"),ERROR_NOTUNIQUE,lane.number);
				}
			}			
		}
		validateProperties(errors, lane.properties, RunPropertyDefinitionHelper.getLanePropertyDefinitions(), getKey(rootKeyName,"properties"));
		validateReadSets(errors, run,lane,lane.readsets, collectionName,  getKey(rootKeyName,"readsets"));		
	}

	public static void validateReadSets(Map<String, List<ValidationError>> errors, Run run,Lane laneValue,List<ReadSet> readSets,
			String collectionName, String rootKeyName) {
		int index = 0;
		if(null != readSets){
			for (ReadSet readSet : readSets) {
				validateReadSet(errors, run,laneValue.number,readSet, collectionName, rootKeyName+"["+index+++"]");
			}
		}
	}

	public static void validateReadSet(Map<String, List<ValidationError>> errors, Run run,int laneNumber, ReadSet readSet,
			String collectionName, String rootKeyName) {
		if(null == run || null == readSet){
			throw new IllegalArgumentException("run or readset is null");
		}
		if(required(errors, readSet.code, getKey(rootKeyName,"code"))){
			//Validate unique readSet.code if not already exist
			Run runExist = MongoDBDAO.findOne(collectionName, Run.class, DBQuery.is("lanes.readsets.code", readSet.code));
			if(runExist != null && run._id == null){ //when new run
				addErrors(errors,getKey(rootKeyName,"code"),ERROR_NOTUNIQUE,readSet.code);
			} else if(runExist != null && run._id != null) { //when run exist
				if(!runExist.code.equals(run.code) || !runExist._id.equals(run._id)) {
						addErrors(errors,getKey(rootKeyName,"code"),ERROR_NOTUNIQUE,readSet.code);
				}else if(laneNumber != -1){
					for(Lane lane:run.lanes){
						if(lane.readsets!=null){
							for(ReadSet r:lane.readsets){
								if(r.code.equals(readSet.code)){
									if(lane.number != laneNumber){
										addErrors(errors,getKey(rootKeyName,"code"),ERROR_NOTUNIQUE,readSet.code);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if(required(errors, readSet.projectCode, getKey(rootKeyName,"projectCode"))){
			//TODO validate if exist readSet.projectCode
		}
		if(required(errors, readSet.sampleCode, getKey(rootKeyName,"sampleCode"))){
			//TODO validate if exist
		}
		if(required(errors, readSet.sampleContainerCode, getKey(rootKeyName,"sampleContainerCode"))){
			//TODO validate if exist
		}
		required(errors, readSet.path, getKey(rootKeyName,"path"));
		
		validateProperties(errors, readSet.properties, RunPropertyDefinitionHelper.getReadSetPropertyDefinitions(), getKey(rootKeyName,"properties"));
		validateFiles(errors, readSet.files, collectionName, getKey(rootKeyName,"files"));
	}

	private static void validateFiles(Map<String, List<ValidationError>> errors, List<File> files,String collectionName, String rootKeyName) {
		int index = 0;
		if(null != files){
			for (File file : files) {
				validateFile(errors, file, rootKeyName+"["+index+++"]");
			}
		}
		
	}

	public static void validateFile(Map<String, List<ValidationError>> errors,	File file, String rootKeyName) {
		if(null == file){
			throw new IllegalArgumentException("file is null");
		}
		required(errors, file.extension, getKey(rootKeyName,"extension"));
		required(errors, file.fullname, getKey(rootKeyName,"fullname"));
		required(errors, file.typeCode, getKey(rootKeyName,"typeCode"));
		required(errors, file.usable, getKey(rootKeyName,"usable"));		
		validateProperties(errors, file.properties, RunPropertyDefinitionHelper.getFilePropertyDefinitions(), getKey(rootKeyName,"properties"));		
	}
	
	/* dno
	public static void validateStat(Map<String, List<ValidationError>> errors,
			Stat stat, String collectionName) {
		if(null == stat ){
			throw new IllegalArgumentException("stat is null");
		}
		validateCode(errors, stat, collectionName, Run.class);
		validateTraceInformation(errors, stat.traceInformation, stat._id);
		validationType(errors, stat.typeCode);
		validateProperties(errors, stat.properties, StatPropertyDefinitionHelper.getStatQCPropertyDefinitions().propertiesDefinitions, "properties");
	}
	
	*/
	

	
}
