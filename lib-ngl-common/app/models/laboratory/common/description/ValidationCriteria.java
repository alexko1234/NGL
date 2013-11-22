package models.laboratory.common.description;

import models.laboratory.common.description.dao.ValidationCriteriaDAO;
import models.utils.Model;


/**
 * @author dnoisett
 *
 */
public class ValidationCriteria extends Model<ValidationCriteria>{
	
	public String name;
	public String path;
	
	public static Finder<ValidationCriteria> find = new Finder<ValidationCriteria>(ValidationCriteriaDAO.class.getName()); 
	
	public ValidationCriteria() {
		super(ValidationCriteriaDAO.class.getName());
	}
	
	
}
