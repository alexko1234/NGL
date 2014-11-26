package utils;

import models.laboratory.common.instance.State;

public class StateBuilder {

	State state = new State();
	
	public StateBuilder withCode(String code)
	{
		this.state.code=code;
		return this;
	}
	
	public State build(){
		return state;
	}
	
}
