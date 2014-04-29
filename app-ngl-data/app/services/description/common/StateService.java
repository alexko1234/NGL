package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.laboratory.run.description.RunCategory;
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
	 * 
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
		l.add(newState("Disponible", "A", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("Indisponible", "UA", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("Terminé", "F", true, 1000, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Process.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name()) ));
		l.add(newState("Terminé en erreur", "FE", true, 1000, StateCategory.find.findByCode("F"), null));
		l.add(newState("Terminé en ", "FS", true, 1000, StateCategory.find.findByCode("F"), null));
		l.add(newState("Contrôle qualité en attente", "IW-QC", true, 400, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
		l.add(newState("Contrôle qualité en cours", "IP-QC", true, 450, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
		l.add(newState("Contrôle qualité terminé", "F-QC", true, 500, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
		l.add(newState("Evaluation en attente", "IW-V", true, 600, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("Evaluation en cours", "IP-V", true, 625, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("Evaluation terminée", "F-V", true, 649, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));		
		//NGL-SQ
		l.add(newState("Nouveau", "N", true, 0, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Process.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name(),ObjectType.CODE.Container.name(),ObjectType.CODE.Analysis.name()) ));
		l.add(newState("En cours", "IP", true, 500, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Process.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name()) ));
		l.add(newState("Processus en attente", "IW-P", true, 100, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name())));
		l.add(newState("Expérience en attente", "IW-E", true, 200, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name())));
		l.add(newState("En cours d'utilisation", "IU", true, 250, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name())));
		l.add(newState("En stock", "IS", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name())));
		l.add(newState("Disponible controle qualité", "A-QC", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name())));
		l.add(newState("Disponible purif", "A-PF", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name())));
		//NGL-BI
		l.add(newState("Séquençage en cours", "IP-S", true, 150, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newState("Séquençage en echec", "FE-S", true, 190, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newState("Séquençage terminé", "F-S", true, 199, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name())));	
		
		l.add(newState("Read generation en attente", "IW-RG", true, 200, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newState("Read generation en cours", "IP-RG", true, 250, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name())));
		l.add(newState("Read generation terminée", "F-RG", true, 299, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));
		
		l.add(newState("EVAL. QC en attente", "IW-VQC", true, 650, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("EVAL. QC terminée", "F-VQC", true, 699, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.ReadSet.name()) ));		
		
		l.add(newState("Analyse BI en attente", "IW-BA", true, 700, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.ReadSet.name()) ));
		l.add(newState("Analyse BI en cours", "IP-BA", true, 750, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.ReadSet.name(),ObjectType.CODE.Analysis.name()) ));
		l.add(newState("Analyse BI terminée", "F-BA", true, 799, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.ReadSet.name(),ObjectType.CODE.Analysis.name()) ));		
		
		l.add(newState("EVAL. Analyse BI en attente", "IW-VBA", true, 800, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.ReadSet.name(),ObjectType.CODE.Analysis.name()) ));
		l.add(newState("EVAL. Analyse BI terminée", "F-VBA", true, 899, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.ReadSet.name(),ObjectType.CODE.Analysis.name()) ));		
		
		DAOHelpers.saveModels(State.class, l, errors);
	}

	

}
