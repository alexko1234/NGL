package services.description.run;

import java.util.List;
import java.util.Map;

import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;

public abstract class AbstractTreatmentService {
	
	public void main(Map<String, List<ValidationError>> errors)  throws DAOException{		
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);		
		saveTreatmentCategory(errors);
		saveTreatmentContext(errors);
		saveTreatmentType(errors);	
	}

	public abstract void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException;
	public abstract void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException;
	public abstract void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException;
		

}
