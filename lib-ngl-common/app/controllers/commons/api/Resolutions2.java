package controllers.commons.api;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Controller;

import play.libs.Json;
import play.mvc.Result;

import models.laboratory.common.instance.ResolutionConfigurations;
import models.laboratory.common.instance.Resolution;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import controllers.SubDocumentController;
import fr.cea.ig.MongoDBDAO;

@Controller
public class Resolutions2 extends SubDocumentController<ResolutionConfigurations, Resolution>{

	public Resolutions2() {
		super(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, Resolution.class);
	}
	
	protected Query getSubObjectQuery(String parentCode, String code){
		return DBQuery.and(DBQuery.is("code", parentCode), DBQuery.exists("resolutions." + code));
	}
	
	protected Query getSubObjectQuery(String code){
		return DBQuery.is("resolutions.code", code);
	}

	protected Collection<Resolution> getSubObjects(ResolutionConfigurations object) {
		// TODO Auto-generated method stub
		return (Collection<Resolution>) object.resolutions; 
	}

	protected Resolution getSubObject(ResolutionConfigurations object, String code){
		int k=0;
		for (int i=0; i<object.resolutions.size(); i++) {
			if (object.resolutions.get(i).code.equals(code)) {
				k=i;	
			}
		}
		return object.resolutions.get(k);
	}
	
	public Result list(String objectTypeCode){
		ResolutionConfigurations objectInDB = getObjectByObjectTypeCode(objectTypeCode);
		if (objectInDB != null) {
			return ok(Json.toJson(getSubObjects(objectInDB)));
		} else{
			return notFound();
		}		
	}
	
	public Result list(String objectTypeCode, List<String> typeCodes){
		ResolutionConfigurations objectInDB = getObjectByObjectTypeCodeAndTypeCode(objectTypeCode, typeCodes);
		if (objectInDB != null) {
			return ok(Json.toJson(getSubObjects(objectInDB)));
		} else{
			return notFound();
		}		
	}
	
	private ResolutionConfigurations getObjectByObjectTypeCode(String objectTypeCode) {
		ResolutionConfigurations rc = MongoDBDAO.findOne(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, DBQuery.is("objectTypeCode", objectTypeCode)); 
		return rc;
	}
	
	private ResolutionConfigurations getObjectByObjectTypeCodeAndTypeCode(String objectTypeCode, List<String> typeCodes) {
		ResolutionConfigurations rc = MongoDBDAO.findOne(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, DBQuery.and(DBQuery.is("objectTypeCode", objectTypeCode), DBQuery.in("typeCodes", typeCodes) )); 
		return rc;
	}
	


}
