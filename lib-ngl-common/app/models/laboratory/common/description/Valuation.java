package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.dao.ValuationDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


/**
 * @author dnoisett
 *
 */
public class Valuation extends Model<Valuation>{
	
	public String name;
	public String path;
	
	public Valuation() {
		super(ValuationDAO.class.getName());
	}
	
	public static ValuationFinder find = new ValuationFinder(); 
	
	
	public static class ValuationFinder extends Finder<Valuation>{

		public ValuationFinder() {
			super(ValuationDAO.class.getName());			
		}
		
		public List<Valuation> findByTypeCode(String code) throws DAOException {
			return ((ValuationDAO) getInstance()).findByTypeCode(code);
		}
		
		public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {
			return ((ValuationDAO) getInstance()).isCodeExistForTypeCode(code, typeCode);
		}
		
	}
		
}
