package models.laboratory.common.description;

import models.laboratory.common.description.dao.InstituteDAO;
import models.utils.Model;


/**
 * Value of institute (only 2 possible values : CNG & CNS)
 * @author dnoisett
 *
 */
public class Institute extends Model<Institute>{

	public String name;
	
	public static Finder<Institute> find = new Finder<Institute>(InstituteDAO.class.getName()); 
	
	public Institute() {
		super(InstituteDAO.class.getName());
	}
	
}
