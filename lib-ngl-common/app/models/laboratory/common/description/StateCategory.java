package models.laboratory.common.description;

import models.laboratory.common.description.dao.StateCategoryDAO;
import models.utils.dao.AbstractDAO;

public class StateCategory extends AbstractCategory<StateCategory> {
	
//	public static Finder<StateCategory> find = new Finder<StateCategory>(StateCategoryDAO.class.getName()); 
	public static final Finder<StateCategory,StateCategoryDAO> find = new Finder<>(StateCategoryDAO.class); 
	
	public enum CODE {
		F,
		IP,
		IW,
		N
	} 
	
	public StateCategory() {
		super(StateCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<StateCategory>> daoClass() {
		return StateCategoryDAO.class;
	}

}
