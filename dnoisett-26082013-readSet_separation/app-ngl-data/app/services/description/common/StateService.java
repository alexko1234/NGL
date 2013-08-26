package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.*;
import models.laboratory.common.description.StateCategory.CODE;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;
public class StateService {
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		saveStateCategories(errors);	
		saveStates(errors);	
	}
	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveStateCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<StateCategory> l = new ArrayList<StateCategory>();
				
		for (StateCategory.CODE code : StateCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(StateCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(StateCategory.class, l, errors);
		
	}

	public static void saveStates(Map<String, List<ValidationError>> errors) throws DAOException {
		List<State> l = new ArrayList<State>();
		
		l.add(newState("Nouveau", "N", true, 1, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Container,StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Cours", "IP", true, 2, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Attente Processus", "IWP", true, 3,getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("Disponible", "A", true, 4, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Attente Expérience", "IWE", true, 5, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Cours d'Utilisation", "IU", true, 6,getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Stock", "IS", true, 7, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("Indisponible", "UA", true, 8, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("Finie", "F", true, 9, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment,StateCategory.CODE.Process)));			
		
		DAOHelpers.saveModels(State.class, l, errors);
		
	}
	private static List<StateCategory> getStateCategories(CODE...codes) throws DAOException {
		List<StateCategory> categories = new ArrayList<StateCategory>();
		for(CODE code: codes){
			categories.add(StateCategory.find.findByCode(code.name()));
		}
		return categories;
	}
	

}
