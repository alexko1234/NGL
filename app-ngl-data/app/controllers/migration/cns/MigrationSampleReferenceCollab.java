package controllers.migration.cns;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.ReadSet;
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
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
					 DBQuery.is("contents.sampleCode", s.code),
					DBUpdate.set("contents.$.taxonCode",s.taxonCode)
					.set("contents.$.ncbiScientificName", s.ncbiScientificName),true);					
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
					DBQuery.is("sampleOnContainer.sampleCode", s.code),
					DBUpdate.set("sampleOnContainer.taxonCode",s.taxonCode)
					.set("sampleOnContainer.ncbiScientificName", s.ncbiScientificName),true);
			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
					DBQuery.is("sampleOnInputContainer.sampleCode", s.code),
					DBUpdate.set("sampleOnInputContainer.taxonCode",s.taxonCode)
					.set("sampleOnInputContainer.ncbiScientificName", s.ncbiScientificName),true);
			
		}
	}
}
