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
	
	public StateCategory stateCategory;
	
	public String level;
	
	public static Finder<State> find = new Finder<State>(StateDAO.class.getName()); 
	
	public State() {
		super(StateDAO.class.getName());
	}
	

}
