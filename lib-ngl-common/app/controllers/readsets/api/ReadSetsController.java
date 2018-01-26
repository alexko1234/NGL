package controllers.readsets.api;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
// import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
// import controllers.APICommonController;
import controllers.CommonController;
// import controllers.MongoCommonController;
// import controllers.NGLBaseController;
// import controllers.NGLControllerHelper;
import fr.cea.ig.MongoDBDAO;
// import fr.cea.ig.MongoDBResult;
// import fr.cea.ig.play.NGLContext;

public class ReadSetsController extends CommonController {
// public class ReadSetsController extends NGLBaseController {
// public class ReadSetsController<T> extends MongoCommonController<T> {

	@Inject
	public ReadSetsController() { // (NGLContext ctx) {
		// super(ctx);
	}
	
	protected static ReadSet getReadSet(String code) {
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code);
		return readSet;
	}

	protected static ReadSet getReadSet(String code, String...keys) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", code), getIncludeKeys(keys));
		return readSet;
	}

	/*protected static TraceInformation getUpdateTraceInformation(ReadSet readSet) {
		TraceInformation ti = readSet.traceInformation;
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}*/
	
	protected static TraceInformation getUpdateTraceInformation(ReadSet readSet) {
		return getUpdateTraceInformation(readSet,getCurrentUser());
	}

	protected static TraceInformation getUpdateTraceInformation(ReadSet readSet, String user) {
		TraceInformation ti = readSet.traceInformation;
		ti.setTraceInformation(user);
		return ti;
	}
	
}
