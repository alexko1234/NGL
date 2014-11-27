package builder.data;

import models.laboratory.common.instance.State;

public class StateBuilder {

	State state = new State();
	
	public StateBuilder withCode(String code)
	{
		state.code=code;
		return this;
	}
	
	public State build(){
		return state;
	}
	
}
