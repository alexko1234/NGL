package workflows;

import java.util.Date;
import java.util.HashSet;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.TransientState;
import validation.ContextValidation;

public abstract class Workflows<T> {
	
	private NGLContext ctx;
	
//	public Workflows(NGLContext ctx) {
//		this.ctx = ctx;
//	}
	
	@Autowired
	public NGLContext getNGLContext() {
		return this.ctx;
	}
	
	public abstract void applyPreStateRules(ContextValidation validation, T exp, State nextState);
	
	//public abstract void applyCurrentStateRules(ContextValidation validation, T object);
	
	public abstract void applyPreValidateCurrentStateRules(ContextValidation validation, T object);
	
	public abstract void applyPostValidateCurrentStateRules(ContextValidation validation, T object);
	
	public abstract void applySuccessPostStateRules(ContextValidation validation, T exp);
	
	public abstract void applyErrorPostStateRules(ContextValidation validation, T exp, State nextState);
	
	public abstract void setState(ContextValidation contextValidation, T object, State nextState);
	
	public abstract void nextState(ContextValidation contextValidation, T object);
	
	protected TraceInformation updateTraceInformation(
			TraceInformation traceInformation, State nextState) {		
		traceInformation.modifyDate = nextState.date;
		traceInformation.modifyUser = nextState.user;		
		return traceInformation;
	}

	protected boolean goBack(State previousState, State nextState) {
		models.laboratory.common.description.State nextStateDesc = models.laboratory.common.description.State.find.findByCode(nextState.code);
		models.laboratory.common.description.State previousStateDesc = models.laboratory.common.description.State.find.findByCode(previousState.code);
		boolean goBack = false;
		if(nextStateDesc.position < previousStateDesc.position){
			goBack=true;
			
		}
		return goBack;
	}

	
	/*
	 * Clone State without historical
	 * @param state
	 * @return
	 */
	protected State cloneState(State state, String user) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = new Date();
		nextState.user = user;
		return nextState;
	}
	
	protected static State getNewState(String stateCode, String user) {
		State nextState = new State();
		nextState.code = stateCode;
		nextState.user = user;
		nextState.date = new Date();
		return nextState;
	}
	
	protected State updateHistoricalNextState(State previousState, State nextState) {
		if (null == previousState.historical) {
			nextState.historical = new HashSet<TransientState>(0);
			nextState.historical.add(new TransientState(previousState, nextState.historical.size()));
		} else {
			nextState.historical = previousState.historical;
		}
		nextState.historical.add(new TransientState(nextState, nextState.historical.size()));		
		return nextState;
	}
}
