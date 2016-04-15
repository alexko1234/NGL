package validation.container.instance;

import java.util.Set;
import java.util.stream.Collectors;

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

import com.mongodb.BasicDBObject;

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
		if(!contextValidation.hasErrors() && !nextState.code.equals(container.state.code)){
			String nextStateCode = nextState.code;
			String currentStateCode = container.state.code;
			
			String context = (String) contextValidation.getObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
			
			switch (context) {
			case "workflow":
				
				if("IW-P".equals(currentStateCode) && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if(currentStateCode.startsWith("A") && !"IW-E".equals(nextStateCode) && !"IU".equals(nextStateCode) && !"IW-D".equals(nextStateCode) ){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IW-E".equals(currentStateCode) && !"IU".equals(nextStateCode) && !"IW-D".equals(nextStateCode) && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IU".equals(currentStateCode) && !"IW-D".equals(nextStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}
				
				break;
			case "controllers":
				
				if("IW-P".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IS")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if(currentStateCode.startsWith("A") && 
						(!nextStateCode.startsWith("A") || (!"A".equals(nextStateCode) && !getContainerStates(container).contains(nextStateCode)))){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IS".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IW-P")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("UA".equals(currentStateCode) && 
						!nextStateCode.equals("IW-P") && !nextStateCode.equals("IS")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("N".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IW-P") && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IW-E".equals(currentStateCode) || "IU".equals(currentStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}
				//!!! No need rules for state IW-D because when we manage a plate the support state is depending of container states
				break;

			default:
				throw new RuntimeException("FIELD_STATE_CONTAINER_CONTEXT : "+context+" not manage !!!");
				
			}
			
			/*
			if(("IS".equals(currentStateCode) || "UA".equals(currentStateCode)) && !nextStateCode.equals("IW-P")){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
			}
			*/
		}
				
	}
	
	public static Set<String> getContainerStates(ContainerSupport containerSupport){
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("state", 1);
		
		return MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("support.code", containerSupport.code), keys).toList()
				.stream()
				.map(c -> c.state.code)
				.collect(Collectors.toSet());
	}
	
}
