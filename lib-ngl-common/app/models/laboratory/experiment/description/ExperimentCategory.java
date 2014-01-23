package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;

public class ExperimentCategory extends AbstractCategory<ExperimentCategory>{

	public static enum CODE {purification, qualitycontrol, transfert, transformation, voidprocess};

	public ExperimentCategory() {
		super(ExperimentCategoryDAO.class.getName());
	}

	public static ExperimentCategoryFinder find = new ExperimentCategoryFinder(); 
	
	
	public static class ExperimentCategoryFinder extends Finder<ExperimentCategory>{

		public ExperimentCategoryFinder() {
			super(ExperimentCategoryDAO.class.getName());			
		}
		
		public List<ExperimentCategory> findByProcessTypeCode(String processTypeCode) throws DAOException{
			return ((ExperimentCategoryDAO)getInstance()).findByProcessTypeCode(processTypeCode);
		}
	}
}
