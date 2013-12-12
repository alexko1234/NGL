package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;

public class StateService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(State.class, State.find);
		DAOHelpers.removeAll(StateCategory.class, StateCategory.find);

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

	/**
	 * 
	 * @param errors
	 * @throws DAOException
	 */
	public static void saveStates(Map<String, List<ValidationError>> errors) throws DAOException {
		List<State> l = new ArrayList<State>();
		//COMMON
		l.add(newState("Disponible", "A", true, 1000, getStateCategories(StateCategory.CODE.N)));
		l.add(newState("Indisponible", "UA", true, 1000, getStateCategories(StateCategory.CODE.N)));
		l.add(newState("Terminé", "F", true, 1000, getStateCategories(StateCategory.CODE.F)));
		l.add(newState("Terminé en erreur", "FE", true, 1000, getStateCategories(StateCategory.CODE.F)));
		l.add(newState("Terminé en ", "FS", true, 1000, getStateCategories(StateCategory.CODE.F)));
		//
		l.add(newState("Contrôle qualité en attente", "IW-QC", true, 401, getStateCategories(StateCategory.CODE.IW)));	
		l.add(newState("Contrôle qualité en cours", "IP-QC", true, 450, getStateCategories(StateCategory.CODE.IP)));	
		l.add(newState("Contrôle qualité terminé", "F-QC", true, 500, getStateCategories(StateCategory.CODE.F)));	
		
		l.add(newState("Evaluation en attente", "IW-V", true, 601, getStateCategories(StateCategory.CODE.IW)));
		l.add(newState("Evaluation en cours", "IP-V", true, 651, getStateCategories(StateCategory.CODE.IP)));
		l.add(newState("Evaluation terminée", "F-V", true, 701, getStateCategories(StateCategory.CODE.F)));
		
		//NGL-SQ
		l.add(newState("Nouveau", "N", true, 0, getStateCategories(StateCategory.CODE.N)));
		l.add(newState("En cours", "IP", true, 500, getStateCategories(StateCategory.CODE.IP)));
		l.add(newState("Processus en attente", "IW-P", true, 101, getStateCategories(StateCategory.CODE.IW)));
		l.add(newState("Expérience en attente", "IW-E", true, 201, getStateCategories(StateCategory.CODE.IW)));
		l.add(newState("En cours d'utilisation", "IU", true, 250, getStateCategories(StateCategory.CODE.IP)));
		l.add(newState("En stock", "IS", true, 900, getStateCategories(StateCategory.CODE.N)));
		//NGL-BI
		l.add(newState("Séquençage en cours", "IP-S", true, 150, getStateCategories(StateCategory.CODE.IP)));
		l.add(newState("Séquençage en echec", "FE-S", true, 199, getStateCategories(StateCategory.CODE.F)));
		l.add(newState("Séquençage terminé", "F-S", true, 200, getStateCategories(StateCategory.CODE.F)));
		
		l.add(newState("Read generation en attente", "IW-RG", true, 201, getStateCategories(StateCategory.CODE.IW)));
		l.add(newState("Read generation en cours", "IP-RG", true, 250, getStateCategories(StateCategory.CODE.IP)));
		l.add(newState("Read generation terminée", "F-RG", true, 300, getStateCategories(StateCategory.CODE.F)));

		DAOHelpers.saveModels(State.class, l, errors);
	}
	
	/**
	 * 
	 * @param codes
	 * @return
	 * @throws DAOException
	 */
	private static List<StateCategory> getStateCategories(StateCategory.CODE...codes) throws DAOException {
		List<StateCategory> categories = new ArrayList<StateCategory>();
		for(StateCategory.CODE code: codes){
			categories.add(StateCategory.find.findByCode(code.name()));
		}
		return categories;
	}
	

}
