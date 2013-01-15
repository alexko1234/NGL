package factory;


import models.description.common.ObjectType;
import controllers.admin.types.ExperimentTypes;
import controllers.admin.types.IGenericCreateTypes;
import controllers.admin.types.InstrumentUsedTypes;

public class ControllerTypeFactory {

	public static final String typeExperiment="Experiment";
	public static final String typeInstrumentUsed="InstrumentUsed";
	public static final String typePurificationMethod="PurificationMethod";
	public static final String typeQualityControl="QualityControl";
	public static final String typeReagent="Reagent";
	public static final String typeTransferMethod="TransferMethod";
	
	public static IGenericCreateTypes getInstance(Long idObjectType)
	{
		ObjectType ot = ObjectType.findById(idObjectType);
		if(ot.type.equals(ControllerTypeFactory.typeExperiment)){
			return new ExperimentTypes();
		}else if(ot.type.equals(ControllerTypeFactory.typeInstrumentUsed)){
			return new InstrumentUsedTypes();
		}
		return null;
	}
}
