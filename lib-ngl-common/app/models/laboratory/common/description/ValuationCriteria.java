package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.dao.ValuationCriteriaDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


/**
 * @author dnoisett
 *
 */
public class ValuationCriteria extends Model<ValuationCriteria>{
	
	public String name;
	public String path;
	public List<Institute> institutes;
	
	
	public ValuationCriteria() {
		super(ValuationCriteriaDAO.class.getName());
	}
	
	public static ValuationCriteriaFinder find = new ValuationCriteriaFinder(); 
	
	
	public static class ValuationCriteriaFinder extends Finder<ValuationCriteria>{

		public ValuationCriteriaFinder() {
			super(ValuationCriteriaDAO.class.getName());			
		}
		
		public List<ValuationCriteria> findByTypeCode(String code) throws DAOException {
			return ((ValuationCriteriaDAO) getInstance()).findByTypeCode(code);
		}
		
		public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {
			return ((ValuationCriteriaDAO) getInstance()).isCodeExistForTypeCode(code, typeCode);
		}
		
		public List<ValuationCriteria> findByCommonInfoType(long idCommonInfoType) throws DAOException {
			return ((ValuationCriteriaDAO) getInstance()).findByCommonInfoType(idCommonInfoType);
		}
		
	}
		
}
