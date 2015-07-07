package models.laboratory.reagent.description;

import java.util.List;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.reagentCatalogs.instance.KitCatalogValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class KitCatalog extends AbstractCatalog{

	public String providerRefName;
	public String providerCode;
	public List<String> experimentTypeCodes;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, name, "name");
		ValidationHelper.required(contextValidation, providerRefName, "providerRefName");
		ValidationHelper.required(contextValidation, providerCode, "providerCode");
		ValidationHelper.required(contextValidation, catalogRefCode, "catalogRefCode");
		ValidationHelper.required(contextValidation, experimentTypeCodes, "experimentTypeCodes");
		if(!contextValidation.hasErrors()){
			KitCatalogValidationHelper.validateCode(this, InstanceConstants.REAGENT_CATALOG_COLL_NAME, contextValidation);
			KitCatalogValidationHelper.validateExperimentTypes(experimentTypeCodes, contextValidation);
			if(contextValidation.isCreationMode()){
				if(MongoDBDAO.checkObjectExist(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.and(DBQuery.is("name",name)))){
					contextValidation.addErrors("name", ValidationConstants.ERROR_NOTUNIQUE_MSG, name);
				}
				
				if(MongoDBDAO.checkObjectExist(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.and(DBQuery.is("providerRefName",providerRefName)))){
					contextValidation.addErrors("providerRefName", ValidationConstants.ERROR_NOTUNIQUE_MSG, providerRefName);
				}
			}
		}
	}
}
