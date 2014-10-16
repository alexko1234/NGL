package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunType;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.description.TreatmentTypeContext;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;



public class TreatmentValidationHelper extends CommonValidationHelper {
	private static Level.CODE getLevelFromContext(ContextValidation contextValidation) {
		return getObjectFromContext("level", Level.CODE.class, contextValidation);
	}
	
	public static void validationTreatments(Map<String, Treatment> treatments, ContextValidation contextValidation) {
		if(null != treatments){
			List<String> trNames = new ArrayList<String>();
			contextValidation.addKeyToRootKeyName("treatments");
			for(Treatment t:treatments.values()){
				contextValidation.addKeyToRootKeyName(t.code);
				if(!trNames.contains(t.code) && treatments.containsKey(t.code)){										
					trNames.add(t.code);
					t.validate(contextValidation);					
				}else if(trNames.contains(t.code)){
					contextValidation.addErrors("code", ValidationConstants.ERROR_NOTUNIQUE_MSG, t.code);
				} else{
					contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, t.code);
				}
				contextValidation.removeKeyFromRootKeyName(t.code);
			}
			contextValidation.removeKeyFromRootKeyName("treatments");
		}
		
	}
	
	

	public static void validateCode(TreatmentType treatmentType, String code, ContextValidation contextValidation) {
	 if(ValidationHelper.required(contextValidation, code, "code")){
		if (contextValidation.isCreationMode() && isTreatmentExist(code, contextValidation)) {
	    	contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTUNIQUE_MSG, code);		    	
		}else if (contextValidation.isUpdateMode() && !isTreatmentExist(code, contextValidation)){
			contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
		}
		
		if(!treatmentType.names.contains(code)){
			contextValidation.addErrors("code",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, code);
		}		
	}
		
	}
	
	private static boolean isTreatmentExist(String code, ContextValidation contextValidation) {
		Level.CODE levelCode = getLevelFromContext(contextValidation);
		
		if(Level.CODE.ReadSet.equals(levelCode)){
			ReadSet readSet = (ReadSet) contextValidation.getObject("readSet");
			return MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.exists("treatments."+code)));
		}else if(Level.CODE.Lane.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			Lane lane = (Lane) contextValidation.getObject("lane");
			
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code), 
							DBQuery.elemMatch("lanes", 
									DBQuery.and(
											DBQuery.is("number", lane.number),
											DBQuery.exists("treatments."+code)))));
							
		}else if(Level.CODE.Run.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code),DBQuery.exists("treatments."+code)));			
		}
		return false;
	}

	public static void validateResults(TreatmentType treatmentType, Map<String, Map<String, PropertyValue>> results, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, results, "results")){
			Level.CODE levelCode = getLevelFromContext(contextValidation);
			//validate all treatment key in input
			for(Map.Entry<String, Map<String, PropertyValue>> entry : results.entrySet()){
				TreatmentTypeContext context = getTreatmentTypeContext(entry.getKey(), treatmentType.id);
				if(context == null){
					contextValidation.addErrors(entry.getKey(),ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, entry.getKey());
				}				
			}
			//validate if all treatment context are present
			for(TreatmentTypeContext context : treatmentType.contexts){
				if(results.containsKey(context.code)){
					Map<String, PropertyValue> props = results.get(context.code);
					contextValidation.addKeyToRootKeyName(context.code);					
					ValidationHelper.validateProperties(contextValidation, props, treatmentType.getPropertyDefinitionByLevel(Level.CODE.valueOf(context.name), levelCode));
					contextValidation.removeKeyFromRootKeyName(context.code);
				}else if(context.required){
					contextValidation.addErrors(context.code,ValidationConstants.ERROR_REQUIRED_MSG, context.code);
				}
			}
			
		}
		
	}
	
	private static TreatmentTypeContext getTreatmentTypeContext(String contextCode, Long typeId) {
		try {
			return TreatmentTypeContext.find.findByTreatmentTypeId(contextCode, typeId);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	

	public static void validateTreatmentCategoryCode(TreatmentType treatmentType, String categoryCode, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, categoryCode, "categoryCode")){
			TreatmentCategory tc = validateExistDescriptionCode(contextValidation, categoryCode, "categoryCode", TreatmentCategory.find, true);
			if(!treatmentType.category.equals(tc)){
				contextValidation.addErrors("categoryCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, categoryCode);
			}
		}
		
	}

}
