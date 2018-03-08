package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentCategory.ExperimentCategoryFinder;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.laboratory.run.description.dao.RunCategoryDAO;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;


public class RunCategory extends AbstractCategory<RunCategory> {
	
	public static final RunCategoryFinder find = new RunCategoryFinder(); 
		
	public RunCategory() {
		super(RunCategoryDAO.class.getName());
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
