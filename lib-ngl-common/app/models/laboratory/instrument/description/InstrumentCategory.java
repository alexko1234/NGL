package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

public class InstrumentCategory extends AbstractCategory<InstrumentCategory> {
	
	public static final InstrumentCategoryFinder find = new InstrumentCategoryFinder(); 
	
	public InstrumentCategory() {
		super(InstrumentCategoryDAO.class.getName());
	}
	
	@Override
	protected Class<? extends AbstractDAO<InstrumentCategory>> daoClass() {
		return InstrumentCategoryDAO.class;
	}
	
	public static class InstrumentCategoryFinder extends Finder<InstrumentCategory,InstrumentCategoryDAO> {

//		public InstrumentCategoryFinder() {
//			super(InstrumentCategoryDAO.class.getName());			
//		}
		public InstrumentCategoryFinder() { super(InstrumentCategoryDAO.class); }
		
		public List<InstrumentCategory> findByInstrumentUsedTypeCode(String instrumentTypeCode) throws DAOException {
//			return ((InstrumentCategoryDAO)getInstance()).findByInstrumentUsedTypeCode(instrumentTypeCode);
			return getInstance().findByInstrumentUsedTypeCode(instrumentTypeCode);
		}
		
	}

}
