package services.description.experiment;

import java.util.List;
import java.util.Map;

import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;

public abstract class AbstractExperimentService {

	
	public void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
		DAOHelpers.removeAll(ExperimentTypeNode.class, ExperimentTypeNode.find);

		DAOHelpers.removeAll(ExperimentType.class, ExperimentType.find);
		DAOHelpers.removeAll(ExperimentCategory.class, ExperimentCategory.find);

		saveProtocolCategories(errors);
		saveExperimentCategories(errors);
		saveExperimentTypes(errors);
		saveExperimentTypeNodes(errors);
	}

	abstract void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException ;

	abstract void saveExperimentTypes(Map<String, List<ValidationError>> errors) throws DAOException;

	abstract void saveExperimentCategories(Map<String, List<ValidationError>> errors) throws DAOException;

	abstract void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException;
}
