package services.description;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import models.laboratory.common.description.AbstractCategory;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.ResolutionCategory;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.laboratory.common.description.Value;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;

public class DescriptionFactory {

	
	public static <T extends AbstractCategory<T>> T newSimpleCategory(Class<T> classCat, String name, String code){
		try {
			T cat = classCat.newInstance();
			cat.code = code;
			cat.name = name;
			return cat; 
		} catch (InstantiationException e) {
			Logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}
	}

	public static PropertyDefinition newPropertiesDefinition(String name, String code, Level.CODE level, Class<?> type, Boolean required) throws DAOException{
		PropertyDefinition pd = new PropertyDefinition();		
		pd.name = name;
		pd.code = code;
		pd.active = true;
		pd.level = Level.find.findByCode(level.name());
		pd.type = type.getName();
		pd.required = required;
		pd.choiceInList = false;		
		return pd;
	}
	
	public static PropertyDefinition newPropertiesDefinition(String name, String code, Level.CODE level, Class<?> type, Boolean required,
			MeasureCategory measureCategory, MeasureUnit displayMeasureUnit, MeasureUnit saveMeasureUnit) throws DAOException{
		PropertyDefinition pd = new PropertyDefinition();		
		pd.name = name;
		pd.code = code;
		pd.active = true;
		pd.level = Level.find.findByCode(level.name());
		pd.type = type.getName();
		pd.required = required;
		pd.choiceInList = false;	
		pd.measureCategory = measureCategory;
		pd.displayMeasureValue = displayMeasureUnit;
		pd.saveMeasureValue = saveMeasureUnit;
		return pd;
	}

	public static PropertyDefinition newPropertiesDefinition(String name, String code, Level.CODE level, Class<?> type, Boolean required, 
			List<Value> values, MeasureCategory measureCategory, MeasureUnit displayMeasureUnit, MeasureUnit saveMeasureUnit) throws DAOException{
		PropertyDefinition pd = new PropertyDefinition();		
		pd.name = name;
		pd.code = code;
		pd.active = true;
		pd.level = Level.find.findByCode(level.name());
		pd.type = type.getName();
		pd.required = required;
		pd.choiceInList = true;		
		pd.possibleValues = values;
		pd.measureCategory = measureCategory;
		pd.displayMeasureValue = displayMeasureUnit;
		pd.saveMeasureValue = saveMeasureUnit;		
		return pd;
	}
	
	public static PropertyDefinition newPropertiesDefinition(String name, String code, Level.CODE level, Class<?> type, Boolean required, List<Value> values) throws DAOException{
		PropertyDefinition pd = new PropertyDefinition();		
		pd.name = name;
		pd.code = code;
		pd.active = true;
		pd.level = Level.find.findByCode(level.name());
		pd.type = type.getName();
		pd.required = required;
		pd.choiceInList = true;		
		pd.possibleValues = values;
		return pd;
	}

	public static List<Value> newValues(String...values) {
		List<Value> l = new ArrayList<Value>(values.hashCode());
		for(String v : values){
			Value value = new Value();
			value.value = v;
			value.defaultValue = false;
			l.add(value);
		}
		return l;
	}

	public static Level newLevel(String name, String code) {
		Level l = new Level();
		l.code = code;
		l.name = name;
		return l;
	}
	
	public static ObjectType newObjectType(String name, String code) {
		ObjectType l = new ObjectType();
		l.code = code;
		l.generic = false;
		return l;
	}
	
	public static MeasureUnit newMeasureUnit(String code, String value,
			boolean defaultUnit, MeasureCategory category) {
		MeasureUnit measureUnit = new MeasureUnit();
		measureUnit.code = code;
		measureUnit.value = value;
		measureUnit.defaultUnit = defaultUnit;
		measureUnit.category = category;
		
		return measureUnit;
	}

	public static Resolution newResolution(String name, String code,
			ResolutionCategory category) {
		Resolution r = new Resolution();
		r.code = code;
		r.name = name;
		r.category = category;
		return r;
	}

	/**
	 * Crée un nouvel etat
	 * @param name
	 * @param code
	 * @param active
	 * @param order
	 * @param category
	 * @return
	 */
	public static State newState(String name, String code, boolean active,	int position, List<StateCategory> categories) {
		State s = new State();
		s.code = code;
		s.name = name;
		s.active =active;
		s.position = position;
		s.categories = categories;
		return s;
	}

	public static ContainerSupportCategory newContainerSupportCategory(String name, String code, int nbLine, int nbColumn, int nbUsableContainer, ContainerCategory containerCategory) {
		ContainerSupportCategory csc = DescriptionFactory.newSimpleCategory(ContainerSupportCategory.class, name, code);
		csc.nbLine = nbLine;
		csc.nbColumn = nbColumn;
		csc.nbUsableContainer = nbUsableContainer;	
		csc.containerCategory = containerCategory;
		return csc;
	}

	public static ExperimentType newExperimentType(String name, String code, ExperimentCategory category, List<PropertyDefinition> propertiesDefinitions, List<Protocol> protocols, List<InstrumentUsedType> instrumentUsedTypes,String atomicTransfertMethod) throws DAOException {
		ExperimentType et = new ExperimentType();
		et.code =code.toLowerCase();
		et.name =name;
		et.category = category;
		et.objectType = ObjectType.find.findByCode(ObjectType.CODE.Experiment.name());
		et.propertiesDefinitions = propertiesDefinitions;
		et.protocols = protocols;
		et.instrumentUsedTypes = instrumentUsedTypes;
		et.states = State.find.findByCategoryCode(StateCategory.CODE.Experiment.name());
		et.resolutions = Resolution.find.findByCategoryCode(ResolutionCategory.CODE.Experiment.name());
		et.atomicTransfertMethod=atomicTransfertMethod;
		return et;
	}

	public static ExperimentTypeNode newExperimentTypeNode(String code, ExperimentType experimentType, boolean mandatoryPurif, boolean mandatoryQC, List<ExperimentTypeNode> previousExp, List<ExperimentType> purifTypes, List<ExperimentType> qcTypes) {
		ExperimentTypeNode etn = new ExperimentTypeNode();
		
		etn.code = code;
		etn.experimentType = experimentType;
		etn.doPurification = (purifTypes != null && purifTypes.size() > 0)?true:false;
		etn.mandatoryPurification = mandatoryPurif;
		etn.doQualityControl = (qcTypes != null && qcTypes.size() > 0)?true:false;
		etn.mandatoryQualityControl = mandatoryQC;
		etn.possiblePurificationTypes = purifTypes;
		etn.possibleQualityControlTypes = qcTypes;
		etn.previousExperimentType = previousExp;
		
		return etn;
	}

	
	public static Protocol newProtocol(String name, String code,
			String path, String version, ProtocolCategory cat) {
		Protocol p = new Protocol();
		p.code = code.toLowerCase().replace("\\s+", "-");
		p.name = name;
		p.filePath = path;
		p.version = version;
		p.category = cat;
		return p;
	}

	public static InstrumentCategory newInstrumentCategory(String name, String code) {
		InstrumentCategory ic = DescriptionFactory.newSimpleCategory(InstrumentCategory.class,name, code);
		return ic;
	}

	public static InstrumentUsedType newInstrumentUsedType(String name, String code, InstrumentCategory category, List<PropertyDefinition> propertiesDefinitions, List<Instrument> instruments) throws DAOException{
		InstrumentUsedType iut = new InstrumentUsedType();
		iut.code =code;
		iut.name =name;
		iut.category = category;
		iut.objectType = ObjectType.find.findByCode(ObjectType.CODE.Instrument.name());
		iut.propertiesDefinitions = propertiesDefinitions;
		iut.instruments = instruments;
		
		return iut; 
	}

	public static ProcessType newProcessType(String name, String code, ProcessCategory category, List<PropertyDefinition> propertiesDefinitions, 
			List<ExperimentType> experimentTypes, ExperimentType firstExperimentType, ExperimentType lastExperimentType, ExperimentType voidExperimentType) throws DAOException {
		ProcessType et = new ProcessType();
		et.code =code.toLowerCase();
		et.name =name;
		et.category = category;
		et.objectType = ObjectType.find.findByCode(ObjectType.CODE.Process.name());
		et.propertiesDefinitions = propertiesDefinitions;
		et.states = State.find.findByCategoryCode(StateCategory.CODE.Process.name());
		et.resolutions = Resolution.find.findByCategoryCode(ResolutionCategory.CODE.Process.name());
		et.firstExperimentType = firstExperimentType;
		et.lastExperimentType = lastExperimentType;
		et.voidExperimentType = voidExperimentType;
		et.experimentTypes = experimentTypes;
		return et;
	}

	
	public static SampleType newSampleType(String name, String code, SampleCategory category, List<PropertyDefinition> propertiesDefinitions) throws DAOException{
		SampleType st = new SampleType();
		st.code = code;
		st.name = name;
		st.category = category;
		st.objectType = ObjectType.find.findByCode(ObjectType.CODE.Sample.name());
		st.propertiesDefinitions = propertiesDefinitions;
		return st;
	}
	
	public static ImportType newImportType(String name, String code, ImportCategory category, List<PropertyDefinition> propertiesDefinitions) throws DAOException{
		ImportType st = new ImportType();
		st.code = code;
		st.name = name;
		st.category = category;
		st.objectType = ObjectType.find.findByCode(ObjectType.CODE.Import.name());
		st.propertiesDefinitions = propertiesDefinitions;
		return st;
	}
	
	public static ProjectType newProjectType(String name, String code, ProjectCategory category, List<PropertyDefinition> propertiesDefinitions) throws DAOException{
		ProjectType st = new ProjectType();
		st.code = code;
		st.name = name;
		st.category = category;
		st.objectType = ObjectType.find.findByCode(ObjectType.CODE.Project.name());
		st.propertiesDefinitions = propertiesDefinitions;
		return st;
	}
	

}
