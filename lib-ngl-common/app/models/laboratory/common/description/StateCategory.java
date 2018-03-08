package models.laboratory.common.description;

//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.laboratory.common.description.dao.StateCategoryDAO;

public class StateCategory extends AbstractCategory<StateCategory> {
	
	public enum CODE {
		F,
		IP,
		IW,
		N
	}; 
	
//	public static Finder<StateCategory> find = new Finder<StateCategory>(StateCategoryDAO.class.getName()); 
	public static Finder<StateCategory,StateCategoryDAO> find = new Finder<>(StateCategoryDAO.class); 
	
	public StateCategory() {
		super(StateCategoryDAO.class.getName());
	}

}
