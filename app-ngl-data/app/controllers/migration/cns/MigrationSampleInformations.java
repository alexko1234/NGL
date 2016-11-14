package controllers.migration.cns;

import java.util.List;

import org.mongojack.DBQuery;

import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;
import services.instance.sample.UpdateSamplePropertiesCNS;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class MigrationSampleInformations extends CommonController {

	public static Result migration() {
	
		updateSampleInformations();
		return ok("Migration Sample properties finish");
	}

	private static void updateSampleInformations() {
		ContextValidation contextError=new ContextValidation("ngl");
		
		MongoDBDAO.find("UpdateSample",Container.class,DBQuery.empty()).cursor.forEach(o -> {
			Sample sample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.is("code",o.code));
			Logger.debug("update "+sample.code);
			UpdateSamplePropertiesCNS.updateOneSample(sample, contextError);
			
		});
		/*
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.empty()).toList();
		//List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.is("code","BKP_EB")).toList();
		for(Sample s:samples){
			UpdateSamplePropertiesCNS.updateOneSample(s, contextError);
		}
		*/
	}
	
	
}


