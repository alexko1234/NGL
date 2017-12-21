package models.laboratory.reagent.description;

import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import validation.ContextValidation;
import validation.reagentCatalogs.instance.KitCatalogValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ReagentCatalog extends AbstractCatalog {
	public String boxCatalogCode;
	public String kitCatalogCode;
	public Double storageConditions;
	
	public Integer possibleUseNumber;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, name, "name");
		ValidationHelper.required(contextValidation, catalogRefCode, "catalogRefCode");
		if(!contextValidation.hasErrors()){
			KitCatalogValidationHelper.validateCode(this, InstanceConstants.REAGENT_CATALOG_COLL_NAME, contextValidation);
			KitCatalogValidationHelper.validateKitCatalogCode(kitCatalogCode, contextValidation);
			KitCatalogValidationHelper.validateBoxCatalogCode(boxCatalogCode, contextValidation);
			if(contextValidation.isCreationMode()){
				if(MongoDBDAO.checkObjectExist(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, DBQuery.and(DBQuery.is("catalogRefCode",catalogRefCode), DBQuery.is("boxCatalogCode",boxCatalogCode)))){
					contextValidation.addErrors("catalogRefCode", ValidationConstants.ERROR_NOTUNIQUE_MSG, catalogRefCode);
				}
			}
		}
	}
}
