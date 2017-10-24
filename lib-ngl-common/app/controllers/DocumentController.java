package controllers;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import fr.cea.ig.DBObject;

public class DocumentController<T extends DBObject> extends MongoCommonController<T> {

	protected DocumentController(String collectionName, Class<T> type) {
		super(collectionName, type);
		
	}
	
	protected DocumentController(String collectionName, Class<T> type, List<String> defaultKeys) {
		super(collectionName, type, defaultKeys);
		
	}
	
	protected TraceInformation getUpdateTraceInformation(TraceInformation ti) {
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	
}
