package validation.run.instance;


import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.AnalysisType;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;


public class AnalysisValidationHelper extends CommonValidationHelper {
	
	public static void validateAnalysisType(String typeCode,	
			                                Map<String, PropertyValue<?>> properties, 
			                                ContextValidation contextValidation) {
		AnalysisType analysisType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", AnalysisType.find,true);
		if (analysisType != null) {
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, analysisType.getPropertyDefinitionByLevel(Level.CODE.Analysis), true);
			contextValidation.removeKeyFromRootKeyName("properties");
		}		
	}
	
	public static void validateReadSetCodes(Analysis analysis, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, analysis.masterReadSetCodes, "masterReadSetCodes", ReadSet.class,InstanceConstants.READSET_ILLUMINA_COLL_NAME,false);
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, analysis.readSetCodes, "masterReadSetCodes", ReadSet.class,InstanceConstants.READSET_ILLUMINA_COLL_NAME,false);
		
		if("N".equals(analysis.state.code)){
			//validateReadSetsState(analysis.masterReadSetCodes, "masterReadSetCodes", "IW-BA", contextValidation);
			BasicDBObject keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("state", 1);
			int i=0;
			for(String code : analysis.masterReadSetCodes){
				//Get readSet
				ReadSet readSetMaster = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code, keys);
				if(!readSetMaster.state.code.equals("IW-BA") && !readSetMaster.state.code.equals("IP-BA")){
					contextValidation.addErrors("masterReadSetCodes["+i+"]", ValidationConstants.ERROR_BADSTATE_MSG, code);
				}else if(readSetMaster.state.code.equals("IP-BA") && MongoDBDAO.checkObjectExist(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, 
						DBQuery.and(DBQuery.is("state.code","IP-BA"),DBQuery.in("masterReadSetCodes", code)))){
					contextValidation.addErrors("masterReadSetCodes["+i+"]", ValidationConstants.ERROR_BADSTATE_MSG, code);
				}
				i++;
				
			}
		}else if("IP-BA".equals(analysis.state.code)){
			validateReadSetsState(analysis.masterReadSetCodes, "masterReadSetCodes", "IP-BA", contextValidation);
		}
		
	}

	private static void validateReadSetsState(List<String> readSetCodes, String pName, String waitingState, ContextValidation contextValidation) {
		int i = 0;
		for(String code: readSetCodes){
			if(!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.is("state.code", waitingState)))){
				contextValidation.addErrors(pName+"["+i+"]", ValidationConstants.ERROR_BADSTATE_MSG, code);
			}
			i++;
		}		
	}

		
	
		
	

}
