package models.laboratory.common.description;

import models.laboratory.common.description.dao.StateCategoryDAO;

public class StateCategory extends AbstractCategory<StateCategory>{
	
	public enum CODE{F,IP,IW,N}; 
	
	public static Finder<StateCategory> find = new Finder<StateCategory>(StateCategoryDAO.class.getName()); 
	
	public StateCategory() {
		super(StateCategoryDAO.class.getName());
	}

}
