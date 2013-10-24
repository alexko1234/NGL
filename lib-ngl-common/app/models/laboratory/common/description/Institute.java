package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.dao.InstituteDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


/**
 * Value of institute (only 2 possible values : CNG & CNS)
 * @author dnoisett
 *
 */
public class Institute extends Model<Institute>{

	public enum CODE{CNG, CNS};
	
	public String name;
	
	
	public Institute() {
		super(InstituteDAO.class.getName());
	}
	
}
