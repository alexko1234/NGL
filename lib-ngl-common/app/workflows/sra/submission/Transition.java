package workflows.sra.submission;

import fr.cea.ig.DBObject;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Submission;
import validation.ContextValidation;
import workflows.Workflows;

public interface Transition <T extends DBObject & TransitionObject> {
	
	public void execute(ContextValidation contextValidation, 
						 T object,
						 State nextState);
	
	public void success(ContextValidation contextValidation, 
			 T object,
			 State nextState);

	public void error(ContextValidation contextValidation, 
			 T object,
			 State nextState);
/*	
	default void success(ContextValidation contextValidation, 
			 T object,
			 State nextState){}

	default void error(ContextValidation contextValidation, 
			 T object,
			 State nextState){}
*/

}


