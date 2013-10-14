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
		//COMMON
		l.add(newState("Disponible", "A", true, 1000, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		l.add(newState("Indisponible", "UA", true, 1000, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		l.add(newState("Finie", "F", true, 1000, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment, StateCategory.CODE.Process, StateCategory.CODE.Run, StateCategory.CODE.Lane)));			
		l.add(newState("Evalué", "E", true, 1000, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.Lane)));			
		
		l.add(newState("En Attente Contrôle Qualité", "IW-QC", true, 401, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));	
		l.add(newState("En Cours Contrôle Qualité", "IP-QC", true, 450, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));	
		l.add(newState("Fin Contrôle Qualité", "F-QC", true, 500, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));	
		l.add(newState("En Attente Validation", "IW-V", true, 601, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.Run, StateCategory.CODE.Lane, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		l.add(newState("En Cours Validation", "IP-V", true, 651, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.Run, StateCategory.CODE.Lane, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		l.add(newState("Fin Validation", "F-V", true, 701, getStateCategories(StateCategory.CODE.Container, StateCategory.CODE.Run, StateCategory.CODE.Lane, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
				
		
		//NGL-SQ
		l.add(newState("Nouveau", "N", true, 0, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Container,StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Cours", "IP", true, 500, getStateCategories(StateCategory.CODE.Project, StateCategory.CODE.Experiment,StateCategory.CODE.Process)));
		l.add(newState("En Attente Processus", "IW-P", true, 101, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Attente Expérience", "IW-E", true, 201, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Cours d'Utilisation", "IU", true, 250, getStateCategories(StateCategory.CODE.Container)));
		l.add(newState("En Stock", "IS", true, 900, getStateCategories(StateCategory.CODE.Container)));
				
		//NGL-BI
		l.add(newState("En Cours Séquençage", "IP-S", true, 150, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.Lane)));	
		l.add(newState("En Cours Read Génération", "IP-RG", true, 250, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.Lane, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		l.add(newState("Fin Read Génération", "F-RG", true, 300, getStateCategories(StateCategory.CODE.Run, StateCategory.CODE.Lane, StateCategory.CODE.ReadSet, StateCategory.CODE.File)));
		// TODO : see IWQC
		
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
