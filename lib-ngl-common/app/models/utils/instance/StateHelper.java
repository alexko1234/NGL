package models.utils.instance;

import java.util.ArrayList;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TransientState;

public class StateHelper {

	
	public static State updateHistoricalNextState(State previousState, State nextState) {
		if (null == previousState.historical) {
			nextState.historical = new ArrayList<TransientState>(0);
			nextState.historical.add(new TransientState(previousState, nextState.historical.size()));
		} else {
			nextState.historical = previousState.historical;
		}
		nextState.historical.add(new TransientState(nextState, nextState.historical.size()));		
		return nextState;
	}
	
	public static State cloneState(State state) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = state.date;
		nextState.user = state.user;
		return nextState;
	}
	
}
