 package controllers.runs.api;
 
/**
 * dnoisett, v1 
 */

import static play.data.Form.form;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class HistoricalStates extends CommonController {

	final static Form<HistoricalStates> historicalStatesForm = form(HistoricalStates.class);

	//@Permission(value={"reading"})
	public static Result list(String runCode) {

		Run run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", runCode));
		if (null == run) {
			return badRequest();
		}
		return ok(Json.toJson(run.state.historical)); 
	}

	

	
	
	public static Result delete(String runCode, Integer index) { //delete a transientState

		Run run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", runCode));
		if (null == run) {
			return badRequest();
		}
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("state.historical.index", index)), DBUpdate.unset("state.historical.$"));
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",runCode), DBUpdate.pull("state.historical", null));
		
		return ok();
	}
	
	
	
}

