package controllers.readsets.api;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class ReadSetsController extends CommonController {

    protected static ReadSet getReadSet(String code) {
    	ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code);
    	return readSet;
    }
   
    
    protected static TraceInformation getUpdateTraceInformation(ReadSet readSet) {
		TraceInformation ti = readSet.traceInformation;
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	
}
