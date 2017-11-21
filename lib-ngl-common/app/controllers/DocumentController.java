package controllers;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import fr.cea.ig.DBObject;
import fr.cea.ig.play.NGLContext;

public class DocumentController<T extends DBObject> extends MongoCommonController<T> {

	protected DocumentController(NGLContext ctx, String collectionName, Class<T> type) {
		super(ctx,collectionName, type);
		
	}
	
	protected DocumentController(NGLContext ctx, String collectionName, Class<T> type, List<String> defaultKeys) {
		super(ctx,collectionName, type, defaultKeys);
		
	}
	
	protected TraceInformation getUpdateTraceInformation(TraceInformation ti) {
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	
}
