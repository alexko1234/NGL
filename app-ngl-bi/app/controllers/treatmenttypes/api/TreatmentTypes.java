 package controllers.treatmenttypes.api;

import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
import controllers.authorisation.Permission;



public class TreatmentTypes extends CommonController {

	//@Permission(value={"reading"})
	public static Result get(String code) {
		TreatmentType treatmentType =  getTreatmentType(code);		
		if(treatmentType != null) {
			return ok(Json.toJson(treatmentType));	
		} 		
		else {
			return notFound();
		}	
	}

	private static TreatmentType getTreatmentType(String code) {
		try {
			return TreatmentType.find.findByCode(code);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}		
	}
}
