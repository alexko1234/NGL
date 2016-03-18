package controllers.migration.cns;

import java.util.List;

import org.mongojack.DBQuery;

import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.instance.SampleHelper;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class MigrationSampleReferenceCollab extends CommonController {

	public static Result migration() {
	
		updateSampleReferenceCollab();
		return ok("Migration Reference Collab Finish");
	}

	private static void updateSampleReferenceCollab() {
		ContextValidation contextError=new ContextValidation("ngl");
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.empty()).toList();
		//List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.is("code","BKP_EB")).toList();
		for(Sample s:samples){
			SampleHelper.updateSampleReferenceCollab(s, contextError);
		}
	}
}
