package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

public class ExperimentCategory extends AbstractCategory<ExperimentCategory>{

	public static final ExperimentCategoryFinder find = new ExperimentCategoryFinder(); 
		
	public enum CODE {
		purification, 
		qualitycontrol, 
		transfert, 
		transformation, 
		voidprocess
	}

	public ExperimentCategory() {
		super(ExperimentCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ExperimentCategory>> daoClass() {
		return ExperimentCategoryDAO.class;
	}
	
	public static class ExperimentCategoryFinder extends Finder<ExperimentCategory,ExperimentCategoryDAO> {

//		public ExperimentCategoryFinder() {
//			super(ExperimentCategoryDAO.class.getName());			
//		}
		public ExperimentCategoryFinder() { super(ExperimentCategoryDAO.class);	}
		
		public List<ExperimentCategory> findByProcessTypeCode(String processTypeCode) throws DAOException{
//			return ((ExperimentCategoryDAO)getInstance()).findByProcessTypeCode(processTypeCode);
			return getInstance().findByProcessTypeCode(processTypeCode);
		}
		
	}

}
