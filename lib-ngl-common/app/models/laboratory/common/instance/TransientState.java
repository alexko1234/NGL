package models.laboratory.common.instance;

import java.util.Date;

public class TransientState {

	public Integer index;
	public String code;
	public String user;
	public Date date;

	public TransientState(State state, Integer index) {
		super();
		this.index = index;
		this.code = state.code;
		this.date = state.date;
		this.user = state.user;
	}

	public TransientState() {
		super();
	}

}
