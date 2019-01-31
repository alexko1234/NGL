package models.laboratory.common.description;

import models.laboratory.common.description.dao.InstituteDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;

/**
 * Value of institute (only 2 possible values : CNG {@literal &} CNS)
 * 
 * @author dnoisett
 *
 */
public class Institute extends Model<Institute> {

	// public static Finder<Institute> find = new Finder<Institute>(InstituteDAO.class.getName()); 
	public static final Finder<Institute,InstituteDAO> find = new Finder<>(InstituteDAO.class); 
	
	public String name;
	
	public Institute() {
		super(InstituteDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<Institute>> daoClass() {
		return InstituteDAO.class;
	}
	
}
