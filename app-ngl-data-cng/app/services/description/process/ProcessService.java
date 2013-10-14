package services.description.process;

import static services.description.DescriptionFactory.newProcessType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
public class ProcessService {
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
		DAOHelpers.removeAll(ProcessCategory.class, ProcessCategory.find);
		
		saveProcessCategories(errors);
		saveProcessTypes(errors);
	}

	public static void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
		
	}
	
	private static void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		l.add(newProcessType("Banque 300-600", "lib-300-600", ProcessCategory.find.findByCode("library"), null, getExperimentTypes("fragmentation","librairie","amplification"), 
				getExperimentTypes("fragmentation").get(0), getExperimentTypes("amplification").get(0), getExperimentTypes("void-lib-300-600").get(0)));
		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}
	
	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}
}
