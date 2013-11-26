package validation.run.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;



public class RunValidationHelper extends CommonValidationHelper {
		
	public static void validateRunInstrumentUsed(InstrumentUsed instrumentUsed, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
			contextValidation.addKeyToRootKeyName("instrumentUsed");
			instrumentUsed.validate(contextValidation); 
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
		}
	}

	public static void validateRunType(String typeCode,	Map<String, PropertyValue> properties,	ContextValidation contextValidation) {
		RunType runType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", RunType.find,true);
		if(null != runType){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, runType.getPropertyDefinitionByLevel(Level.CODE.Run), true);
			contextValidation.removeKeyFromRootKeyName("properties");
		}		
	}
	
	
	public static void validationLaneReadSetCodes(Integer number, List<String> readSetCodes, ContextValidation contextValidation) {
		if(readSetCodes != null && readSetCodes.size() > 0){
			List<String> readSetCodesTreat = new ArrayList<String>();
			for(int i=0; i< readSetCodes.size(); i++){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCodes.get(i));
				if(null == readSet || !number.equals(readSet.laneNumber)){
					contextValidation.addErrors("readSetCodes["+i+"]",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG,  readSetCodes.get(i), "ReadSet");
				}
				
				if(readSetCodesTreat.contains(readSetCodes.get(i))){
					contextValidation.addErrors("readSetCodes["+i+"]",ValidationConstants.ERROR_CODE_DOUBLE_MSG,  readSetCodes.get(i));
				}
				readSetCodesTreat.add(readSetCodes.get(i));
			}
		}
		
		
	}
	
	
	
	public static void validateRunProjectCodes(String runCode, Set<String> projectCodes, ContextValidation contextValidation) {
		Logger.debug("in pt0");
		if(projectCodes != null && projectCodes.size() > 0){
			Logger.debug("in pt1");
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", runCode)).toList();
			Logger.debug("readSets.size()=" + readSets.size());
			if (readSets != null) {
				Set<String> readSetProjectCodes = new TreeSet<String>(); 
				for (ReadSet readSet : readSets) {
					readSetProjectCodes.add(readSet.projectCode);
				} 

					int i=0;
					for (Iterator<String> it = projectCodes.iterator(); it.hasNext(); ) {
						 String projectCode = it.next();
						if (! readSetProjectCodes.contains(projectCode) ) {
							contextValidation.addErrors("projectCodes["+i+"]", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, projectCode, "Run");
						}
						i++;
					}

					for (Iterator<String> it = readSetProjectCodes.iterator(); it.hasNext(); ) {
						String readSetProjectCode = it.next();
						if (!projectCodes.contains(readSetProjectCode)) {
							contextValidation.addErrors("projectCodes[]", ValidationConstants.ERROR_CODE_MISSING_MSG, readSetProjectCode, "Run");
						}
					}
			}
			else {
				Logger.debug("in pt2");
				contextValidation.addErrors("projectCodes", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, projectCodes.toString(), "Run");
			}
		}
	}
	
	




}
