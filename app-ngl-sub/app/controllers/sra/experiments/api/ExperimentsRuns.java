package controllers.sra.experiments.api;

import org.mongojack.DBQuery;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
import play.libs.Json;
import play.mvc.Result;

public class ExperimentsRuns extends DocumentController<Experiment> {

	
	public ExperimentsRuns() {
		super(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
	}

	public Result get(String code)
	{
		Experiment exp  = MongoDBDAO.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("run.code", code));
		if (exp != null) {
			return ok(Json.toJson(exp.run));
		} else{
			return notFound();
		}		
	}
	
}