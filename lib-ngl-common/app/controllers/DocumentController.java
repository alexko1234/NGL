package controllers;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import fr.cea.ig.DBObject;
import fr.cea.ig.play.NGLContext;


/**
 * Root class for DBObject subclasses api controllers. The name does not
 * match any NGL concept at the moment.  
 *  
 * @author vrd
 *
 * @param <T> DBObject subclass to provide controller implementation for
 */
public abstract class DocumentController<T extends DBObject> extends MongoCommonController<T> {

	
	protected DocumentController(NGLContext ctx, String collectionName, Class<T> type) {
		super(ctx,collectionName, type);
	}
	
	
	protected DocumentController(NGLContext ctx, String collectionName, Class<T> type, List<String> defaultKeys) {
		super(ctx,collectionName, type, defaultKeys);
		
	}
	
	// TODO: remove as this is a duplicate of the TraceInformation method and
	// does not forces the update of the update fields as setTraceInformation
	// has some bogus behavior.
	protected TraceInformation getUpdateTraceInformation(TraceInformation ti) {
		ti.setTraceInformation(getCurrentUser());
		return ti;
	}
	
	
	
}
