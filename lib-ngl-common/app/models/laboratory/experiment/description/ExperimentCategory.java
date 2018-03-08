package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.utils.dao.DAOException;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

public class ExperimentCategory extends AbstractCategory<ExperimentCategory>{

	public enum CODE {
		purification, 
		qualitycontrol, 
		transfert, 
		transformation, 
		voidprocess
	};

	public ExperimentCategory() {
		super(ExperimentCategoryDAO.class.getName());
	}

	public static ExperimentCategoryFinder find = new ExperimentCategoryFinder(); 
	
	
	public static class ExperimentCategoryFinder extends Finder<ExperimentCategory> {

//		public ExperimentCategoryFinder() {
//			super(ExperimentCategoryDAO.class.getName());			
//		}
		public ExperimentCategoryFinder() { super(ExperimentCategoryDAO.class);	}
		
		public List<ExperimentCategory> findByProcessTypeCode(String processTypeCode) throws DAOException{
			return ((ExperimentCategoryDAO)getInstance()).findByProcessTypeCode(processTypeCode);
		}
		
	}
	
}
