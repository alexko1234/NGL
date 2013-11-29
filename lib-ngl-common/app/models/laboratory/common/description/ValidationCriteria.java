package models.laboratory.common.description;

import java.util.List;
import models.laboratory.common.description.dao.ValidationCriteriaDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


/**
 * @author dnoisett
 *
 */
public class ValidationCriteria extends Model<ValidationCriteria>{
	
	public String name;
	public String path;
	
	public ValidationCriteria() {
		super(ValidationCriteriaDAO.class.getName());
	}
	
	public static ValidationCriteriaFinder find = new ValidationCriteriaFinder(); 
	
	
	public static class ValidationCriteriaFinder extends Finder<ValidationCriteria>{

		public ValidationCriteriaFinder() {
			super(ValidationCriteriaDAO.class.getName());			
		}
		
		public List<ValidationCriteria> findByTypeCode(String code) throws DAOException {
			return ((ValidationCriteriaDAO) getInstance()).findByTypeCode(code);
		}
		
		public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {
			return ((ValidationCriteriaDAO) getInstance()).isCodeExistForTypeCode(code, typeCode);
		}
		
	}
		
}
