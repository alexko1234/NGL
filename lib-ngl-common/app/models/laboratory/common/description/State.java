package models.laboratory.common.description;


import java.util.List;

import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.utils.ListObject;
import models.utils.Model;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

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
	
	public static List<ListObject> findAllForContainerList() throws DAOException{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		return stateDAO.findAllForContainerList();
	}
	
	public State() {
		super(StateDAO.class.getName());
	}
	

}
