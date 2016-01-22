package validation.container.instance;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.storage.instance.Storage;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.MongoDBDAO;

public class ContainerSupportValidationHelper extends CommonValidationHelper{

	public static void validateUniqueContainerSupportCodePosition(LocationOnContainerSupport containerSupport,ContextValidation contextValidation) {

		if(contextValidation.isCreationMode()){
			Query query=DBQuery.and(DBQuery.is("support.line",containerSupport.line),
					DBQuery.is("support.column", containerSupport.column),
					DBQuery.is("support.code",containerSupport.code));
			if (MongoDBDAO.getCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class).getCount(query)!=0 ) {
				//TODO revoir le message d'erreur
				contextValidation.addErrors("supportCode.line.column", ValidationConstants.ERROR_NOTUNIQUE_MSG, containerSupport.code,containerSupport.line,containerSupport.column);		
			}
		}
	}

	public static void validateContainerSupportCategoryCode(
			String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerSupportCategory.find,false);

	}

	public static void validateStorageCode(String storageCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, storageCode, "storageCode",Storage.class,InstanceConstants.STORAGE_COLL_NAME ,false);

	}
	
	public static void validateNextState(ContainerSupport container, State nextState, ContextValidation contextValidation) {
		CommonValidationHelper.validateState(ObjectType.CODE.Container, nextState, contextValidation);
		if(!contextValidation.hasErrors()){
			String nextStateCode = nextState.code;
			String currentStateCode = container.state.code;
			if(("IS".equals(currentStateCode) || "UA".equals(currentStateCode)) && !nextStateCode.equals("IW-P")){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
			}
		}
				
	}
	

}
