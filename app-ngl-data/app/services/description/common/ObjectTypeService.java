package services.description.common;

import static services.description.DescriptionFactory.newObjectType;
import static services.description.DescriptionFactory.setStatesToObjectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;


public class ObjectTypeService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		
		//DAOHelpers.removeAll(ObjectType.class, ObjectType.find);
		
		//saveObjectTypes(errors);
		
		updateStatesObjectTypes(errors);
	}
	

	
	public static void saveObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(newObjectType(ObjectType.CODE.Container.name(), ObjectType.CODE.Container.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		
		l.add(newObjectType(ObjectType.CODE.Project.name(), ObjectType.CODE.Project.name(), getStates("F", "N", "IP") ));
		
		l.add(newObjectType(ObjectType.CODE.Experiment.name(), ObjectType.CODE.Experiment.name(), getStates("F", "N", "IP") ));
		
		l.add(newObjectType(ObjectType.CODE.Process.name(), ObjectType.CODE.Process.name(), getStates("F", "N", "IP") ));
		
		l.add(newObjectType(ObjectType.CODE.Run.name(), ObjectType.CODE.Run.name(), getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(newObjectType(ObjectType.CODE.Lane.name(), ObjectType.CODE.Lane.name(), getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(newObjectType(ObjectType.CODE.ReadSet.name(), ObjectType.CODE.ReadSet.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		l.add(newObjectType(ObjectType.CODE.File.name(), ObjectType.CODE.File.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		
		DAOHelpers.saveModels(ObjectType.class, l, errors);
		
	}
	
	
	public static void updateStatesObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(setStatesToObjectType(ObjectType.CODE.Container.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Project.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Experiment.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Process.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Run.name(), getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Lane.name(), getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.ReadSet.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.File.name(), getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		//default values
		l.add(setStatesToObjectType(ObjectType.CODE.Sample.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Instrument.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Reagent.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Import.name(), getStates("F", "N", "IP") ));
		
		l.add(setStatesToObjectType(ObjectType.CODE.Treatment.name(), getStates("F", "N", "IP") ));
		
		
		DAOHelpers.updateModels(ObjectType.class, l, errors);	
	}
	
	
	private static List<State> getStates(String...codes) throws DAOException {
		List<State> states = new ArrayList<State>();
		for(String code: codes){
			states.add(State.find.findByCode(code));
		}
		return states;
	}
	


	
}
