package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.dao.DAOException;


public class InstrumentCategory extends AbstractCategory<InstrumentCategory>{
	
	public static InstrumentCategoryFinder find = new InstrumentCategoryFinder(); 
	
	public InstrumentCategory() {
		super(InstrumentCategoryDAO.class.getName());
	}
	
	public static class InstrumentCategoryFinder extends Finder<InstrumentCategory>{

		public InstrumentCategoryFinder() {
			super(InstrumentCategoryDAO.class.getName());			
		}
		
		public List<InstrumentCategory> findByInstrumentUsedTypeCode(String instrumentTypeCode) throws DAOException{
			return ((InstrumentCategoryDAO)getInstance()).findByInstrumentUsedTypeCode(instrumentTypeCode);
		}
	}
	
	
}
