package models.laboratory.run.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.run.description.dao.RunCategoryDAO;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

public class RunCategory extends AbstractCategory<RunCategory> {
	
	public static final RunCategoryFinder find = new RunCategoryFinder(); 
		
	public RunCategory() {
		super(RunCategoryDAO.class.getName());
	}
	
	@Override
	protected Class<? extends AbstractDAO<RunCategory>> daoClass() {
		return RunCategoryDAO.class;
	}
	
	public static class RunCategoryFinder extends Finder<RunCategory,RunCategoryDAO> {

//		public RunCategoryFinder() {
//			super(RunCategoryDAO.class.getName());			
//		}
		public RunCategoryFinder() { super(RunCategoryDAO.class); }
		
		public RunCategory findByTypeCode(String typeCode) throws DAOException{
//			return ((RunCategoryDAO)getInstance()).findByTypeCode(typeCode);
			return getInstance().findByTypeCode(typeCode);
		}
		
	}

}
