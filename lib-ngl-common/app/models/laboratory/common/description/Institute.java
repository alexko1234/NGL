package models.laboratory.common.description;

import models.laboratory.common.description.dao.InstituteDAO;
import models.utils.Model;


/**
 * Value of institute (only 2 possible values : CNG {@literal &} CNS)
 * 
 * @author dnoisett
 *
 */
public class Institute extends Model<Institute> {

	public String name;
	
	// Doc generation produces an error with the unqualified name.
	// public static Finder<Institute> find = new Finder<Institute>(InstituteDAO.class.getName()); 
//	public static Model.Finder<Institute> find = new Model.Finder<Institute>(InstituteDAO.class.getName()); 
	public static Finder<Institute> find = new Finder<>(InstituteDAO.class); 
	
	public Institute() {
		super(InstituteDAO.class.getName());
	}
	
}
