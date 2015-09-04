package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.run.description.AnalysisType;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;

public abstract class AbstractRunService {
	
	public void main(Map<String, List<ValidationError>> errors)  throws DAOException {
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(AnalysisType.class, AnalysisType.find);
		
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		saveReadSetType(errors);
		saveAnalysisType(errors);
		saveRunCategories(errors);
		saveRunType(errors);
	}

	public abstract void saveRunType(Map<String, List<ValidationError>> errors)throws DAOException;

	public abstract void saveRunCategories(Map<String, List<ValidationError>> errors)throws DAOException;

	public abstract void saveAnalysisType(Map<String, List<ValidationError>> errors)throws DAOException;

	public abstract void saveReadSetType(Map<String, List<ValidationError>> errors)throws DAOException;

}
