package controllers.readsets.api;

import org.mongojack.DBQuery;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import controllers.CommonController;
import controllers.NGLControllerHelper;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ReadSetsController extends CommonController {

    protected static ReadSet getReadSet(String code) {
    	ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code);
    	return readSet;
    }
   
    protected static ReadSet getReadSet(String code, String...keys) {
    	ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", code), getIncludeKeys(keys));
    	return readSet;
    }
    
    protected static TraceInformation getUpdateTraceInformation(ReadSet readSet) {
		TraceInformation ti = readSet.traceInformation;
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	
}
