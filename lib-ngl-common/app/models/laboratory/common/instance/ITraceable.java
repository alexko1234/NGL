package models.laboratory.common.instance;

import validation.ContextValidation;

/**
 * Trace information holder. Implementors are required to define the
 * getTraceInformation that must return a non null TraceInformation instance.
 * 
 * @author vrd
 *
 */
public interface ITraceable {
		
	/**
	 * Trace information, must be created if needed.
	 * @return
	 */
	TraceInformation getTraceInformation();
	
	/**
	 * Set the creation stamp using the current user and the current time.
	 */
	default void setTraceCreationStamp(ContextValidation ctx, String userName) {
		getTraceInformation().creationStamp(ctx,userName);
	}
	
	/**
	 * Set the update stamp using the current user and the current time.
	 */
	default void setTraceUpdateStamp(ContextValidation ctx, String userName) {
		getTraceInformation().modificationStamp(ctx,userName);
	}
	
}
