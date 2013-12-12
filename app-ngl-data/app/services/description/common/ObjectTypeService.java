package services.description.common;

import services.description.DescriptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.ObjectType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;


public class ObjectTypeService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		
		//Can not be removed cause integrity constraints ...
		//DAOHelpers.removeAll(ObjectType.class, ObjectType.find);
		
		saveObjectTypes(errors);
		
		updateStatesObjectTypes(errors);
	}
	

	
	public static void saveObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
			
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Container.name()), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Container.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Project.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Experiment.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Process.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Run.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.ReadSet.name() ), errors);
			//DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.File.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Sample.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Instrument.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Reagent.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Import.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class,DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Treatment.name() ), errors);
			
	}
	
	
	public static void updateStatesObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Container.name(), false, DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Project.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Experiment.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Process.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Run.name(), false, DescriptionFactory.getStates("F", "IW-V", "IP-V", "F-V", "IP-S", "F-S", "FE-S", "IW-RG", "IP-RG", "F-RG") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.ReadSet.name(), false, DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		//l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.File.name(), false, DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		//default values
		//TODO : define values
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Sample.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Instrument.name(), false, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Reagent.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Import.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Treatment.name(), false, DescriptionFactory.getStates("F", "N", "IP") ));
		
		DAOHelpers.updateModels(ObjectType.class, l, errors);	
	}
	
	


	
}
