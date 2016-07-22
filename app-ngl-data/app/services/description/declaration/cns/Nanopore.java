package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.parameter.index.NanoporeIndex;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;

public class Nanopore extends AbstractDeclaration{

	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to Frg, Lib ONT, Dépôt","ext-to-nanopore-process-library",null, -1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to Lib ONT, Dépôt","ext-to-nanopore-process-library-no-frg",null, -1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to Run Nanopore","ext-to-nanopore-run",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Fragmentation-réparation","nanopore-fragmentation","FRG",2100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
				getPropertyFragmentationNanopore(), getInstrumentUsedTypes("eppendorf-mini-spin-plus","hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));


		l.add(newExperimentType("Librairie ONT","nanopore-library","LIB",2200,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
				getPropertyLibrairieNanopore(), getInstrumentUsedTypes("hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Depot Nanopore","nanopore-depot",null,2300,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDepotNanopore(),
				getInstrumentUsedTypes("minion","mk1", "mk1b"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS) ));

		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeDEV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypePROD() {
		List<ProcessType> l=new ArrayList<ProcessType>();
		l.add(DescriptionFactory.newProcessType("Frg, Lib ONT, Dépôt", "nanopore-process-library", ProcessCategory.find.findByCode("library"),getPropertyDefinitionsNanoporeFragmentation() , 
				Arrays.asList(getPET("ext-to-nanopore-process-library",-1),getPET("nanopore-fragmentation",0),getPET("nanopore-library",1),getPET("nanopore-depot",2)), 
				getExperimentTypes("nanopore-fragmentation").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore-process-library").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		
		l.add(DescriptionFactory.newProcessType("Lib ONT, Dépôt", "nanopore-process-library-no-frg", ProcessCategory.find.findByCode("library"),getPropertyDefinitionsNanoporeLibrary() , 
				Arrays.asList(getPET("ext-to-nanopore-process-library-no-frg",-1),getPET("nanopore-fragmentation",-1), getPET("nanopore-library",0),getPET("nanopore-depot",1)),
				getExperimentTypes("nanopore-library").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore-process-library-no-frg").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
				
		l.add(DescriptionFactory.newProcessType("Run Nanopore", "nanopore-run", ProcessCategory.find.findByCode("sequencing"),null , 
				Arrays.asList(getPET("ext-to-nanopore-run",-1), getPET("nanopore-library",-1), getPET("nanopore-depot",0)),
				getExperimentTypes("nanopore-depot").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore-run").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		return l;
	}

	@Override
	protected List<ProcessType> getProcessTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void getExperimentTypeNodeDEV() {
		//newExperimentTypeNode("nanopore-fragmentation",getExperimentTypes("nanopore-fragmentation").get(0),false, false,false,getExperimentTypeNodes("ext-to-nanopore-process-library"),null,getExperimentTypes("qpcr-quantification"),getExperimentTypes("aliquoting")).save();
	}

	@Override
	protected void getExperimentTypeNodePROD() {
		//Nanopore
		newExperimentTypeNode("ext-to-nanopore-run", getExperimentTypes("ext-to-nanopore-run").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("ext-to-nanopore-process-library", getExperimentTypes("ext-to-nanopore-process-library").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("ext-to-nanopore-process-library-no-frg", getExperimentTypes("ext-to-nanopore-process-library-no-frg").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("nanopore-fragmentation",getExperimentTypes("nanopore-fragmentation").get(0),false, false,false,getExperimentTypeNodes("ext-to-nanopore-process-library"),null,null,getExperimentTypes("aliquoting")).save();

		newExperimentTypeNode("nanopore-library",getExperimentTypes("nanopore-library").get(0),false, false,false,getExperimentTypeNodes("ext-to-nanopore-process-library-no-frg","nanopore-fragmentation"),null,null,getExperimentTypes("pool-tube")).save();
		newExperimentTypeNode("nanopore-depot",getExperimentTypes("nanopore-depot").get(0),false, false,false,getExperimentTypeNodes("nanopore-library","ext-to-nanopore-run"),null,null,null).save();

	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub

	}

	private static List<PropertyDefinition> getPropertyFragmentationNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb fragmentations","fragmentionNumber",LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, true, null
				, null ,null,null, "single",11));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Qté totale dans frg","inputFrgQuantity",LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content), Double.class, true,  null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode("ng"),MeasureUnit.find.findByCode( "ng"), "single",12));


		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Conc. finale FRG","postFrgConcentration",LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",13));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Qté finale FRG","postFrgQuantity",LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",14));
		/*
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Profil","fragmentionProfile",LevelService.getLevels(Level.CODE.ContainerOut), Image.class, false, null
				,null,null,null, "img",15));
		 */
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille réelle","measuredLibrarySize",LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), Integer.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "pb"),MeasureUnit.find.findByCode( "pb"), "single",16));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb réparations","preCRNumber",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false, null
				, null ,null,null, "single",17));
		/*propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Conc. finale preCR","postPreCRConcentration",LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",8));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Qté finale preCR","postPreCRQuantity",LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false,null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",9));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Volume final","measuredVolume",LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µl"),MeasureUnit.find.findByCode( "µl"), "single",10));
		 */
		return propertyDefinitions;

	}

	private static List<PropertyDefinition> getPropertyDepotNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single",300));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("PDF Report","report",LevelService.getLevels(Level.CODE.Experiment), File.class, false, "file", 400));

		// Unite a verifier
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Date creation","loadingReport.creationDate",LevelService.getLevels(Level.CODE.ContainerIn), Date.class, false, "object_list",600));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Heure dépot","loadingReport.hour",LevelService.getLevels(Level.CODE.ContainerIn), String.class, false,"object_list",601));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Temps","loadingReport.time",LevelService.getLevels(Level.CODE.ContainerIn), Long.class, false,null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_TIME),MeasureUnit.find.findByCode( "h"),MeasureUnit.find.findByCode( "h"), "object_list",602));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Volume","loadingReport.volume",LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "object_list",603));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Groupe","qcFlowcell.group",LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, false, "object_list",700));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb pores actifs à réception","qcFlowcell.preLoadingNbActivePores",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false, "object_list",701));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb pores actifs lors du dépôt","qcFlowcell.postLoadingNbActivePores",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false, "object_list",702));

		//propertyDefinitions.add(newPropertiesDefinition("Channels with Reads", "minknowChannelsWithReads", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",301));
		//propertyDefinitions.add(newPropertiesDefinition("Events in Reads", "minknowEvents", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, "single",302));
		//propertyDefinitions.add(newPropertiesDefinition("Complete reads", "minknowCompleteReads", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",303));
		//propertyDefinitions.add(newPropertiesDefinition("Read count", "metrichorReadCount", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",304));
		//propertyDefinitions.add(newPropertiesDefinition("Total 2D yield", "metrichor2DReadsYield", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, null
		//		, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "pb"),MeasureUnit.find.findByCode( "pb"), "single",305));
		//propertyDefinitions.add(newPropertiesDefinition("Longest 2D read", "metrichorMax2DRead", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, null
		//		, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "pb"),MeasureUnit.find.findByCode( "pb"),"single",306));
		//propertyDefinitions.add(newPropertiesDefinition("Peak 2D quality score", "metrichorMax2DQualityScore", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, "single",307));

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyLibrairieNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µl"),MeasureUnit.find.findByCode( "µl"), "single",10));
		propertyDefinitions.add(newPropertiesDefinition("Qté engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",11));
		//		propertyDefinitions.add(newPropertiesDefinition("Taille", "librarySize", LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, true, null
		//			, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "kb"),MeasureUnit.find.findByCode( "kb"), "single",8));

		propertyDefinitions.add(newPropertiesDefinition("Tag","tag", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content),String.class, false, getTagNanopore(), "single",13));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie tag","tagCategory", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content),String.class, false, getTagCategoriesNanopore(),"SINGLE-INDEX", "single",14));


		propertyDefinitions.add(newPropertiesDefinition("Conc. finale End Repair","postEndRepairConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",15));
		propertyDefinitions.add(newPropertiesDefinition("Qté finale End Repair","postEndRepairQuality", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",20));

		propertyDefinitions.add(newPropertiesDefinition("Conc. finale dA tailing","postTailingConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",30));
		propertyDefinitions.add(newPropertiesDefinition("Qté finale dA tailing","postTailingQuality", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",40));


		/*		propertyDefinitions.add(newPropertiesDefinition("Conc. finale Ligation","measuredConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",50));*/
		// Problème code car doit etre measuredquantity

		propertyDefinitions.add(newPropertiesDefinition("Qté finale Ligation","ligationQuantity", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content),Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",60)); 


		return propertyDefinitions;
	}

	private static List<Value> getTagNanopore() {
		List<NanoporeIndex> indexes = MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, NanoporeIndex.class, DBQuery.is("typeCode", "index-nanopore-sequencing")).sort("name").toList();
		List<Value> values = new ArrayList<Value>();
		indexes.forEach(index -> {
			values.add(DescriptionFactory.newValue(index.code, index.name));	
		});

		return values;
	}

	private static List<Value> getTagCategoriesNanopore(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("SINGLE-INDEX", "SINGLE-INDEX"));		
		return values;	
	}


	public static List<PropertyDefinition> getPropertyDefinitionsNanoporeFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Process,Level.CODE.Content),String.class, true, getLibProcessTypeCodeValues(), "ONT","single" ,1));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille banque souhaitée","librarySize",LevelService.getLevels(Level.CODE.Process),Integer.class,true, DescriptionFactory.newValues("8","20")
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "kb"),MeasureUnit.find.findByCode( "kb"), "single",2));
		
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsNanoporeLibrary() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Process,Level.CODE.Content),String.class, true, getLibProcessTypeCodeValues(), "ONT","single" ,1));
		
		return propertyDefinitions;
	}

	private static List<Value> getLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
         values.add(DescriptionFactory.newValue("ONT","ONT - Nanopore"));
         return values;
	}

}
