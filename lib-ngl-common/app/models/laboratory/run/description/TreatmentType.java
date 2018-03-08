package models.laboratory.run.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.run.description.dao.TreatmentTypeDAO;
import models.utils.ListObject;
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

public class TreatmentType extends CommonInfoType {

	public TreatmentCategory category;
	public String names;	
	public List<TreatmentTypeContext> contexts = new ArrayList<TreatmentTypeContext>();
	public String displayOrders;

	
	public static TreatmentTypeFinder find = new TreatmentTypeFinder(); 

	public TreatmentType() {
		super(TreatmentTypeDAO.class.getName());
	}
	
	public static class TreatmentTypeFinder extends CommonInfoType.AbstractCommonInfoTypeFinder<TreatmentType,TreatmentTypeDAO> {

		protected TreatmentTypeFinder() {
			super(TreatmentTypeDAO.class);			
		} 
		
		public List<TreatmentType> findByLevels(Level.CODE...levels) throws DAOException{
//			 return ((TreatmentTypeDAO)getInstance()).findByLevels(levels);			 
			 return getInstance().findByLevels(levels);			 
		}
		
		public List<TreatmentType> findByTreatmentCategoryNames(String...categoryNames) throws DAOException{
//			 return ((TreatmentTypeDAO)getInstance()).findByTreatmentCategoryNames(categoryNames);			 
			return getInstance().findByTreatmentCategoryNames(categoryNames);			 
		}	

	}
	
}
