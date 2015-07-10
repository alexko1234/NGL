package validation.reagentCatalogs.instance;

import java.util.List;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class KitCatalogValidationHelper extends CommonValidationHelper{

	public static void validateExperimentTypes(List<String> experimentTypes, ContextValidation contextValidation){
		for(String et : experimentTypes){
			BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, et, "experimentTypeCodes", ExperimentType.find,true);
		}
	}

	public static void validateKitCatalogCode(String kitCatalogCode, ContextValidation contextValidation){
		if(ValidationHelper.required(contextValidation, kitCatalogCode, "kitCatalogCode")){
			if(!MongoDBDAO.checkObjectExist(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.is("code",kitCatalogCode))){
				contextValidation.addErrors("kitCatalogCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, kitCatalogCode);
			}
		}
	}

	public static void validateBoxCatalogCode(String boxCatalogCode, ContextValidation contextValidation){
		if(ValidationHelper.required(contextValidation, boxCatalogCode, "boxCatalogCode")){
			if(!MongoDBDAO.checkObjectExist(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, DBQuery.and(DBQuery.is("code",boxCatalogCode)))){
				contextValidation.addErrors("boxCatalogCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, boxCatalogCode);
			}
		}
	}
}
