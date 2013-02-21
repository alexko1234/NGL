package models.laboratory.common.description;

import models.laboratory.common.description.dao.ResolutionDAO;
import models.utils.Model;


/**
 * Value of the resolution of final possible state
 * @author ejacoby
 *
 */
public class Resolution extends Model<Resolution>{

	public String name;
	
	public static Finder<Resolution> find = new Finder<Resolution>(ResolutionDAO.class.getName());
	
	public Resolution() {
		super(ResolutionDAO.class.getName());
	}

	
}
