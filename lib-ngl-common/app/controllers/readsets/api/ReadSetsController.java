package controllers.readsets.api;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class ReadSetsController extends CommonController {

	@Inject
	public ReadSetsController() {
	}
	
	protected static ReadSet getReadSet(String code) {
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code);
		return readSet;
	}

	protected static ReadSet getReadSet(String code, String...keys) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", code), getIncludeKeys(keys));
		return readSet;
	}

	
	protected static TraceInformation getUpdateTraceInformation(ReadSet readSet) {
		return getUpdateTraceInformation(readSet,getCurrentUser());
	}

	protected static TraceInformation getUpdateTraceInformation(ReadSet readSet, String user) {
		TraceInformation ti = readSet.traceInformation;
		ti.setTraceInformation(user);
		return ti;
	}
	
}
