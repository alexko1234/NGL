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
		DAOHelpers.removeAll(State.class, State.find);
		
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
		
		l.add(newState("Nouveau", "N", true, 10, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Container,StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Cours", "IP", true, 20, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Attente Processus", "IW-P", true, 30, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("Disponible", "A", true, 75, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet)));
		l.add(newState("En Attente Expérience", "IW-E", true, 50, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Cours d'Utilisation", "IU", true, 60, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Stock", "IS", true, 70, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("Indisponible", "UA", true, 80, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet)));
		l.add(newState("Finie", "F", true, 90, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment, StateCategory.CODE.Process, StateCategory.CODE.Run)));			
		//l.add(newState("En attente Purification", "IWP", true, 100, getStateCategories(StateCategory.CODE.Container)));			
		l.add(newState("En attente Contrôle Qualité", "IWQC", true, 110, getStateCategories(StateCategory.CODE.Container)));			
		l.add(newState("En attente Validation", "IW-V", true, 120, getStateCategories(StateCategory.CODE.Container)));
		
		l.add(newState("IP-S", "IP-S", true, 60, getStateCategories(StateCategory.CODE.Run)));	
		l.add(newState("IP-RG", "IP-RG", true, 20, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.ReadSet)));
		l.add(newState("F-RG", "F-RG", true, 30, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.ReadSet)));
		l.add(newState("F-QC", "F-QC", true, 60, getStateCategories(StateCategory.CODE.ReadSet)));	
		// TODO : see IWQC
		l.add(newState("IW-QC", "IW-QC", true, 70, getStateCategories(StateCategory.CODE.ReadSet)));	
		
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
