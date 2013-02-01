package models.laboratory.common.description;


import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.StateDAO;
import play.modules.spring.Spring;

/**
 * Value of the possible state of type
 * @author ejacoby
 *
 */
public class State{

	public Long id;
	
	public String name;
	
	public String code;
	
	public boolean active;
	
	public Integer priority;
	
	public State() {
		super();
	}

	public State(String name, String code, boolean active, Integer priority) {
		super();
		this.name = name;
		this.code = code;
		this.active = active;
		this.priority = priority;
	}

	public static Map<String, String> getMapPossibleStates()
	{
		Map<String, String> mapPossibleStates = new HashMap<String, String>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		for(State possibleState : stateDAO.findAll()){
			mapPossibleStates.put(possibleState.id.toString(), possibleState.name);
		}
		return mapPossibleStates;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
}
