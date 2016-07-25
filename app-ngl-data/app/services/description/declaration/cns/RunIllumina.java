package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;

public class RunIllumina extends AbstractDeclaration {

	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to Norm, FC, Depot","ext-to-norm-fc-depot-illumina",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to qPCR-norm, FC, Depot","ext-to-qpcr-norm-fc-depot-illumina",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to Run Illumina","ext-to-illumina-run",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		/*
		l.add(newExperimentType("Ext to qPCR","ext-to-qpcr",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to Solution-stock","ext-to-solution-stock",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		 */

		l.add(newExperimentType("Solution stock","solution-stock","STK",1000,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionSolutionStock(),
				getInstrumentUsedTypes("hand","tecan-evo-100"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));


		l.add(newExperimentType("Preparation flowcell", "prepa-flowcell",null,1200, 
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellCNS(),
				getInstrumentUsedTypes("cBot-interne","cBot"), "ManyToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Prep. flowcell ordonnée", "prepa-fc-ordered",null,1300, 
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellOrdered(),
				getInstrumentUsedTypes("cBot"), "ManyToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));



		l.add(newExperimentType("Depot Illumina", "illumina-depot",null, 1400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),getPropertyDefinitionsIlluminaDepot(),
				getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500","HISEQ4000"), "OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeDEV() {
		List<ProcessType> l=new ArrayList<ProcessType>();
		l.add(DescriptionFactory.newProcessType("qPCR-norm, FC, dépôt Illumina", "qpcr-norm-fc-depot-illumina", ProcessCategory.find.findByCode("sequencing"), getPropertyDefinitionsQPCRQuantification(),
				Arrays.asList(getPET("ext-to-qpcr-norm-fc-depot-illumina",-1),getPET("sizing",-1),getPET("pcr-amplification-and-purification",-1),getPET("qpcr-quantification",0),getPET("solution-stock",0),getPET("prepa-flowcell",1),getPET("prepa-fc-ordered",1),getPET("illumina-depot",2)), 
				getExperimentTypes("qpcr-quantification").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-qpcr-norm-fc-depot-illumina").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(DescriptionFactory.newProcessType("Norm, FC, dépôt Illumina", "norm-fc-depot-illumina", ProcessCategory.find.findByCode("sequencing"), getPropertyDefinitionsQPCRQuantification(),
				Arrays.asList(getPET("ext-to-norm-fc-depot-illumina",-1),getPET("sizing",-1),getPET("pcr-amplification-and-purification",-1),getPET("solution-stock",0),getPET("prepa-flowcell",1),getPET("prepa-fc-ordered",1),getPET("illumina-depot",2)), 
				getExperimentTypes("solution-stock").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-norm-fc-depot-illumina").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));		
		return l;
	}

	@Override
	protected List<ProcessType> getProcessTypePROD() {
		List<ProcessType> l=new ArrayList<ProcessType>();

		l.add(DescriptionFactory.newProcessType("Run Illumina", "illumina-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNS() , 
				Arrays.asList(getPET("ext-to-illumina-run",-1),getPET("solution-stock",-1), getPET("prepa-flowcell",0),getPET("prepa-fc-ordered",0),getPET("illumina-depot",1)), 
				getExperimentTypes("prepa-flowcell").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-illumina-run").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	@Override
	protected List<ProcessType> getProcessTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void getExperimentTypeNodeDEV() {
		newExperimentTypeNode("ext-to-norm-fc-depot-illumina", getExperimentTypes("ext-to-norm-fc-depot-illumina").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("ext-to-qpcr-norm-fc-depot-illumina", getExperimentTypes("ext-to-qpcr-norm-fc-depot-illumina").get(0), false, false, false, null, null, null, null).save();

		newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false, false,false,getExperimentTypeNodes("ext-to-qpcr-norm-fc-depot-illumina","ext-to-norm-fc-depot-illumina","sizing","pcr-amplification-and-purification"),null,null,getExperimentTypes("pool", "pool-tube")).save();

	}

	@Override
	protected void getExperimentTypeNodePROD() {
		newExperimentTypeNode("ext-to-illumina-run", getExperimentTypes("ext-to-illumina-run").get(0), false, false, false, null, null, null, null).save();
		//		newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false, false,false,null,null,null,getExperimentTypes("pool")).save();
		newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false, false,false,getExperimentTypeNodes("ext-to-illumina-run","solution-stock"),null,null,null).save();
		newExperimentTypeNode("prepa-fc-ordered",getExperimentTypes("prepa-fc-ordered").get(0),false, false,false,getExperimentTypeNodes("ext-to-illumina-run","solution-stock"),null,null,null).save();
		newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false, false,false,getExperimentTypeNodes("prepa-flowcell","prepa-fc-ordered"),	null,null,null).save();

	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub

	}


	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Utiliser par import ngl-data CNG de creation des depot-illumina
		//propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Experiment), Integer.class, false, "single"));	
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}




	private static List<PropertyDefinition> getPropertyDefinitionSolutionStock() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume à engager", "requiredVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, "IP",
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",21, false,null, "1"));
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, "IP"
				,null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",22, false,null,"1"));

		//Outputcontainer
		/*		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode("nM"),"single",7,true,"10.0"));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",8,true)); */
		return propertyDefinitions; 
	}

	// GA separation getPropertyDefinitionsPrepaflowcellCNS / getPropertyDefinitionsPrepaflowcellCNG pour JIRA 676
	//  ==> feuille de calcul differentes pour la prepaflowcell entre CNS et CNG
	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellCNS() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume sol. stock dans dénat.", "requiredVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",11, false));

		propertyDefinitions.add(newPropertiesDefinition("Volume NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",12,true,"1", null));

		propertyDefinitions.add(newPropertiesDefinition("Conc. solution NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true,DescriptionFactory.newValues("1N","2N"), "2N", "single",13));

		propertyDefinitions.add(newPropertiesDefinition("Volume EB", "EBVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",14, false));

		propertyDefinitions.add(newPropertiesDefinition("Conc. dénat. ", "finalConcentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",15,true,"2", null));

		propertyDefinitions.add(newPropertiesDefinition("Volume dénat.", "finalVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "20"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",16));

		propertyDefinitions.add(newPropertiesDefinition("Volume dénat. dans dilution", "requiredVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",21, false));

		propertyDefinitions.add(newPropertiesDefinition("Volume HT1", "HT1Volume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",22, false));

		propertyDefinitions.add(newPropertiesDefinition("Volume Phix", "phixVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",23, false));

		propertyDefinitions.add(newPropertiesDefinition("Conc. sol. mère Phix", "phixConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"),"single",24, true,"0.02", null));

		propertyDefinitions.add(newPropertiesDefinition("Conc. dilution", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",25));

		propertyDefinitions.add(newPropertiesDefinition("Volume dilution", "finalVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "1000"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",26));

		propertyDefinitions.add(newPropertiesDefinition("Volume dilution sur la piste", "requiredVolume3", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",31, false));		


		//Outputcontainer
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",51,false,"1", null));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",52,false));


		return propertyDefinitions;
	}

	private List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellOrdered() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",11, false));
		propertyDefinitions.add(newPropertiesDefinition("Vol. PhiX", "phixVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",12, false));
		propertyDefinitions.add(newPropertiesDefinition("Vol. RSB", "rsbVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",13, false));
		//Add list value
		propertyDefinitions.add(newPropertiesDefinition("Conc. Phix", "phixConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, getPhixConcentrationCodeValues(), null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",14));
		propertyDefinitions.add(newPropertiesDefinition("Concentration dilution", "finalConcentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",15,true));
		propertyDefinitions.add(newPropertiesDefinition("Volume dilution", "finalVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",16));
		propertyDefinitions.add(newPropertiesDefinition("Vol. dil. ds dénat", "inputVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",21));

		propertyDefinitions.add(newPropertiesDefinition("Vol. NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",22));

		propertyDefinitions.add(newPropertiesDefinition("Conc. NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true,null, null, null, null, "single",23,true,"0.1N", null));
		propertyDefinitions.add(newPropertiesDefinition("Vol. TrisHCL", "trisHCLVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",24));
		propertyDefinitions.add(newPropertiesDefinition("Conc. TrisHCL", "trisHCLConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "200000000" 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "mM"),MeasureUnit.find.findByCode( "nM"), "single",25));
		propertyDefinitions.add(newPropertiesDefinition("Vol. master EPX", "masterEPXVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "35"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",26));
		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true,  null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"),"single",27,false));


		//OuputContainer
		//keep order declaration between phixPercent and finalVolume
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",51,false,"1", null));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",28,false));

		return propertyDefinitions;

	}

	public static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNS() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//TO do multi value
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getSequencingType(), "single",100));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de lectures", "readType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getReadType(), "single",200));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getReadLenght(), "single",300));

		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("% à déposer prévisionnel", "estimatedPercentPerLane"
						, LevelService.getLevels(Level.CODE.Process),Double.class, true,"single",400));	
		return propertyDefinitions;
	}

	private static List<Value> getSequencingType(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("Hiseq 2000/2500N", "Hiseq 2000 / 2500 N"));
		values.add(DescriptionFactory.newValue("Hiseq 2500 Rapide", "Hiseq 2500 Rapide"));
		values.add(DescriptionFactory.newValue("Miseq", "Miseq"));
		values.add(DescriptionFactory.newValue("Hiseq 4000", "Hiseq 4000"));
		values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
		return values;	
	}


	private static List<Value> getReadType(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("SR", "SR"));
		values.add(DescriptionFactory.newValue("PE", "PE"));
		values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
		return values;
	}

	private static List<Value> getReadLenght(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("50","50"));
		values.add(DescriptionFactory.newValue("100","100"));
		values.add(DescriptionFactory.newValue("150","150"));
		values.add(DescriptionFactory.newValue("250","250"));
		values.add(DescriptionFactory.newValue("300","300"));
		values.add(DescriptionFactory.newValue("500","500"));
		values.add(DescriptionFactory.newValue("600","600"));
		values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
		return values;
	}

	private static List<Value> getPhixConcentrationCodeValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("0.1","100"));
		values.add(DescriptionFactory.newValue("0.2","200"));
		values.add(DescriptionFactory.newValue("0.3","300"));
		return values;
	}


	public static List<PropertyDefinition> getPropertyDefinitionsQPCRQuantification() throws DAOException {
		List<PropertyDefinition> propertyDefinitions =getPropertyDefinitionsIlluminaDepotCNS();			
		return propertyDefinitions;
	}
}
