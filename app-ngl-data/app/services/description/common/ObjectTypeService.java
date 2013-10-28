package services.description.common;

import services.description.DescriptionFactory;
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
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Container.name(), ObjectType.CODE.Container.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Project.name(), ObjectType.CODE.Project.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Experiment.name(), ObjectType.CODE.Experiment.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Process.name(), ObjectType.CODE.Process.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Run.name(), ObjectType.CODE.Run.name(), DescriptionFactory.getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.Lane.name(), ObjectType.CODE.Lane.name(), DescriptionFactory.getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.ReadSet.name(), ObjectType.CODE.ReadSet.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.newObjectType(ObjectType.CODE.File.name(), ObjectType.CODE.File.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		
		DAOHelpers.saveModels(ObjectType.class, l, errors);
		
	}
	
	
	public static void updateStatesObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Container.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Project.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Experiment.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Process.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Run.name(), DescriptionFactory.getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Lane.name(), DescriptionFactory.getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.ReadSet.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.File.name(), DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		
		//default values
		//TODO : define values
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Sample.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Instrument.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Reagent.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Import.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Treatment.name(), DescriptionFactory.getStates("F", "N", "IP") ));
		
		
		DAOHelpers.updateModels(ObjectType.class, l, errors);	
	}
	
	


	
}
