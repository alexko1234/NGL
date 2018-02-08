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

//interface ITransition3 <T> {
//
//	public void execute(SubmissionWorkflows self,
//			ContextValidation contextValidation, Submission object, State nextState);
//
//}
//
//class Transition3 <T> implements SubmissionTransition{//Transition <T> {
//	ITransition3 <T> execute;
//	ITransition3 <T> success;
//	ITransition3 <T> error;
//	Transition3(ITransition3 <T> execute,
//	ITransition3 <T> success,
//	ITransition3 <T> error) {
//		this.execute = execute;
//		this.success = success;
//		this.error = error;
//	}
//
//	@Override
//	public void execute(SubmissionWorkflows self,
//			ContextValidation contextValidation, Submission object, State nextState) {
//		execute.execute(self, contextValidation, object, nextState);
//	}
//
//	@Override
//	public void success(SubmissionWorkflows self,
//			ContextValidation contextValidation, Submission object, State nextState) {
//		success.execute(self, contextValidation, object, nextState);
//	}
//
//	@Override
//	public void error(SubmissionWorkflows self,
//			ContextValidation contextValidation, Submission object, State nextState) {
//		error.execute(self, contextValidation, object, nextState);
//	}
//	
//}
//
//abstract class Transition2 <T> extends Workflows <T> {
//	
//	public abstract void applyPreStateRules(ContextValidation validation, T exp, State nextState);
//	
//	//public abstract void applyCurrentStateRules(ContextValidation validation, T object);
//	
//	public abstract void applyPreValidateCurrentStateRules(ContextValidation validation, T object);
//	
//	public abstract void applyPostValidateCurrentStateRules(ContextValidation validation, T object);
//	
//	public abstract void applySuccessPostStateRules(ContextValidation validation, T exp);
//	
//	public abstract void applyErrorPostStateRules(ContextValidation validation, T exp, State nextState);
//	
//	public abstract void setState(ContextValidation contextValidation, T object, State nextState);
//	
//	public abstract void nextState(ContextValidation contextValidation, T object);
//
//	
//	public void execute(SubmissionWorkflows self,
//			ContextValidation contextValidation, 
//			T object,
//			State nextState) {
//		//applyPreStateRules  
//		//applyPreValidateCurrentStateRules
//	}
//
//	public void success(SubmissionWorkflows self,
//			ContextValidation contextValidation, 
//			T object,
//			State nextState){
//		//applyPostValidateCurrentStateRules
//		//applySuccessPostStateRules
//		//nextState
//	}
//
//	public void error(SubmissionWorkflows self,
//			ContextValidation contextValidation, 
//			T object,
//			State nextState){
//			//applyErrorPostStateRules
//	}
//
//}

