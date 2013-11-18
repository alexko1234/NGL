package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;



public class LaneValidationHelper extends CommonValidationHelper {
	

	public static void validationLanes(List<Lane> lanes, ContextValidation contextValidation) {
		//TODO number of lanes (depends of the type run and the mode incremental insert or full insert !!!)
		//TODO validate lane number
		if(null != lanes && lanes.size() > 0) {
			int index = 0;
			Set<Integer> laneNumbers = new TreeSet<Integer>();
			for (Lane lane : lanes) {
				if (lane != null) {
					contextValidation.addKeyToRootKeyName("lanes[" + index + "]");
					lane.validate(contextValidation);
					if(laneNumbers.contains(lane.number)){
						contextValidation.addErrors("number", ValidationConstants.ERROR_NOTUNIQUE_MSG, lane.number);
					}
					laneNumbers.add(lane.number);
					contextValidation.removeKeyFromRootKeyName("lanes[" + index + "]");
				}
				index++;
			}
		}
	}
	
	public static void validationLaneNumber(Integer number, ContextValidation contextValidation) {
		if (ValidationHelper.required(contextValidation, number, "number")) {
			//Validate unique lane.number if run already exist
			if (contextValidation.isCreationMode() && isLaneExist(number, contextValidation)) {
				contextValidation.addErrors("number",ValidationConstants.ERROR_NOTUNIQUE_MSG, number);
			}else if(contextValidation.isUpdateMode() && !isLaneExist(number, contextValidation)){
				contextValidation.addErrors("number",ValidationConstants.ERROR_NOTEXISTS_MSG, number);				
			}						
		}
	}

	private static boolean isLaneExist(Integer number, ContextValidation contextValidation) {
		Run run = getRunFromContext(contextValidation);
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", run.code), DBQuery.is("lanes.number", number)));
		
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

	public static void validationLaneProperties(Map<String, PropertyValue> properties,	ContextValidation contextValidation) {
		Run run = getRunFromContext(contextValidation);
		try {
			RunType  runType = RunType.find.findByCode(run.typeCode);
			if(null != runType){
				contextValidation.addKeyToRootKeyName("properties");
				ValidationHelper.validateProperties(contextValidation, properties, runType.getPropertyDefinitionByLevel(Level.CODE.Lane), true);
				contextValidation.removeKeyFromRootKeyName("properties");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	private static Run getRunFromContext(ContextValidation contextValidation) {
		return getObjectFromContext("run", Run.class, contextValidation);
	}

	public static void validateLaneState(State state,
			ContextValidation contextValidation) {
		Run run = getRunFromContext(contextValidation);
		validateState(run.typeCode, state, contextValidation);	
		
	}
	
	public static void validateLaneValidation(Validation validation,
			ContextValidation contextValidation) {
		Run run = getRunFromContext(contextValidation);
		validateValidation(run.typeCode, validation, contextValidation);	
		
	}

}
