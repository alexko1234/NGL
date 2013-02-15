package models.laboratory.common.description;


import models.laboratory.common.description.dao.StateDAO;
import models.utils.Model;

/**
 * Value of the possible state of type
 * @author ejacoby
 *
 */
public class State extends Model<State>{

	public String name;
	
	public boolean active;
	
	public Integer priority;
	
	public static Finder<State> find = new Finder<State>(StateDAO.class.getName()); 
	
	public State() {
		super(StateDAO.class.getName());
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
