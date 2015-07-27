package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.Logger;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

import com.typesafe.config.ConfigFactory;
public class ExperimentService {
	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
		DAOHelpers.removeAll(ExperimentTypeNode.class, ExperimentTypeNode.find);

		DAOHelpers.removeAll(ExperimentType.class, ExperimentType.find);
		DAOHelpers.removeAll(ExperimentCategory.class, ExperimentCategory.find);

		DAOHelpers.removeAll(Protocol.class, Protocol.find);		
		DAOHelpers.removeAll(ProtocolCategory.class, ProtocolCategory.find);

		saveProtocolCategories(errors);
		//saveProtocol(errors);
		saveExperimentCategories(errors);
		saveExperimentTypes(errors);
		saveExperimentTypeNodes(errors);
	}
	
	@SuppressWarnings("unchecked")
	public static void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProtocolCategory> l = new ArrayList<ProtocolCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Developpement", "development"));
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Production", "production"));
		DAOHelpers.saveModels(ProtocolCategory.class, l, errors);

	}
	/*
	public static void saveProtocol(Map<String, List<ValidationError>> errors) throws DAOException {
		List<Protocol> l = new ArrayList<Protocol>();
		l.add(newProtocol("Fragmentation_ptr_sox140_1","fragmentation_ptr_sox140_1","path1","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("BqSPRI_ptr_sox142_1","bqspri_ptr_sox142_1","path2","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Amplif_ptr_sox144_1","amplif_ptr_sox144_1","path3","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_1","depot_illumina_ptr_1","path4","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_2","depot_illumina_ptr_2","path5","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_3","depot_illumina_ptr_3","path6","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Opgen_prt_1","depot_opgen_ptr_1","path7","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("PrepFC_CBot_ptr_sox139_1","prepfc_cbot_ptr_sox139_1","path7","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Proto_QC_v1","proto_qc_v1","path7","1", ProtocolCategory.find.findByCode("production")));
		
		//for CNG
		l.add(newProtocol("Sop_depot_1","sop_depot_1","path4","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("SOP","SOP","path4","1", ProtocolCategory.find.findByCode("production")));

		DAOHelpers.saveModels(Protocol.class, l, errors);

	} */

	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveExperimentCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ExperimentCategory> l = new ArrayList<ExperimentCategory>();
		for (ExperimentCategory.CODE code : ExperimentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(ExperimentCategory.class, l, errors);
	}


	private static void saveExperimentTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to dépôt opgen","ext-to-opgen-depot",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), getPropertyDefinitionExtToOpgenDepot(), null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		/*
		l.add(newExperimentType("Depot Opgen", "opgen-depot",1600
				, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),null, getProtocols("depot_opgen_ptr_1"), 
				getInstrumentUsedTypes("ARGUS"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS))); */
		
		l.add(newExperimentType("Depot Opgen", "opgen-depot",null,1600
				, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionOpgenDepot(), 
				getInstrumentUsedTypes("ARGUS"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//Prepaflowcell : to finish
		l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		l.add(newExperimentType("Ext to qPCR","ext-to-qpcr",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		l.add(newExperimentType("Ext to Solution-stock","ext-to-solution-stock",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		
		l.add(newExperimentType("Preparation flowcell", "prepa-flowcell",null,1200, 
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellCNS(),
				getInstrumentUsedTypes("cBot-interne","cBot"), "ManyToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		l.add(newExperimentType("Solution stock","solution-stock",null,1000,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionSolutionStock(),
				getInstrumentUsedTypes("hand","tecan-evo-100"),"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//Depot solexa -- FDS 27/02/2015 devient commun
		l.add(newExperimentType("Depot Illumina", "illumina-depot",null, 1400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),getPropertyDefinitionsIlluminaDepot(),
				getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500","NEXTSEQ500"), "OneToVoid", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
		
		
		l.add(newExperimentType("Préparation flowcell","prepa-flowcell-cng",null,1100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellCNG(),
				getInstrumentUsedTypes("cBot", "cBot-onboard"),"ManyToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
		
			//Nanopore
			l.add(newExperimentType("Ext to Nanopore Fragmentation preCR","ext-to-nanopore-frg-preCR",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Ext to Librairie ONT","ext-to-lib-ONT",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Ext to Depot Nanopore","ext-to-nanopore-depot",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Fragmentation preCR","nanopore-fragmentation",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyFragmentationNanopore(), getInstrumentUsedTypes("eppendorf-mini-spin-plus"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Aliquotage","aliquoting",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()),
					getPropertyAliquoting(), getInstrumentUsedTypes("hand"),"OneToMany", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			l.add(newExperimentType("Librairie ONT","nanopore-library",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyLibrairieNanopore(), getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Depot Nanopore","nanopore-depot",null,200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDepotNanopore(),
					getInstrumentUsedTypes("minion"),"ManyToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));

			//library
			l.add(newExperimentType("Fragmentation","fragmentation",null,200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionFragmentation(),
					getInstrumentUsedTypes("hand","covaris-s2","covaris-e210"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
			
			l.add(newExperimentType("Librairie indexée","librairie-indexing",null,400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(),
					getInstrumentUsedTypes("hand","spri"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			/*l.add(newExperimentType("Librairie dual indexing","librairie-dualindexing",600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibDualIndexing(),  
					getInstrumentUsedTypes("hand","spri"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));*/
			
			l.add(newExperimentType("Amplification","amplification",null,800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","thermocycler"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			

			//quality control

			//purif
			l.add(newExperimentType("Ampure Non Ampli","ampure-na",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()),
					null, getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Ampure Ampli","ampure-a",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//void
			l.add(newExperimentType("Ext to Banque","ext-to-library",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			/**********************************************************************************/
			//transformation CNG & CNS
			
		
			l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
			
			/*
			l.add(newExperimentType("Migration sur puce","chip-migration",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getProtocols("proto_qc_v1"), getInstrumentUsedTypes("agilent-2100-bioanalyzer","labChipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			*/
			
			l.add(newExperimentType("Migration sur puce (ampli)","chip-migration-post-pcr",null,650,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			l.add(newExperimentType("Migration sur puce (non ampli)","chip-migration-pre-pcr",null,250,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			
			l.add(newExperimentType("Dosage fluorimétrique","fluo-quantification",null,450,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
					getInstrumentUsedTypes("qubit"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			l.add(newExperimentType("Quantification qPCR","qPCR-quantification",null,850,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
					getInstrumentUsedTypes("rocheLightCycler-qPCR","stratagene-qPCR"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG))); 	
			


			
			/**********************************************************************************/
			
			//17-12-2014 transformation CNG

			//library
			/*
			l.add(newExperimentType("Fragmentation","fragmentation",null,200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","covaris-le220","covaris-e210"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG) ));
			
			l.add(newExperimentType("Librairie indexée","librairie-indexing",null,400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(),
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			*/
			/*l.add(newExperimentType("Librairie dual indexing","librairie-dualindexing",600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibDualIndexing(),
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));*/
			
			/* FDS 11-03-2015=> NGL-356: PCR pas encore utilisable.. commenter pour l'instant
			l.add(newExperimentType("PCR","pcr",800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","thermocycler"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
            */
			
			//pre-sequencing
			//    FDS NGL-356: remommage solution-x-nM=> lib-normalization
			
			
			/* GA : 03/08/2015 old declaration
			l.add(newExperimentType("Librairie normalisée","lib-normalization",null,1000,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			*/
			
			l.add(newExperimentType("Ext to librairie dénaturée","ext-to-denat-dil-lib",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			
			//GA : 03/08/2015 new declaration. lib-normalization became void to avoid to display this step in IHM
			l.add(newExperimentType("Librairie normalisée","lib-normalization",null,1000,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, "OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			
			// FDS new 02-02-2015, intrument Used =>robot oui mais lequel???
			l.add(newExperimentType("Dénaturation-dilution","denat-dil-lib",null,1100,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsDenatDilLibCNG(),
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));


		
			l.add(newExperimentType("Pool Tube","pool-tube",null,1200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), getPropertyDefinitionPoolTube(),
					getInstrumentUsedTypes("hand","tecan-evo-100"),"ManyToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		}

		DAOHelpers.saveModels(ExperimentType.class, l, errors);

	}


	

	private static void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {

		newExperimentTypeNode("ext-to-opgen-depot", getExperimentTypes("ext-to-opgen-depot").get(0), false, false, null, null, null).save();
		newExperimentTypeNode("ext-to-prepa-flowcell", getExperimentTypes("ext-to-prepa-flowcell").get(0), false, false, null, null, null).save();
		newExperimentTypeNode("ext-to-qpcr", getExperimentTypes("ext-to-qpcr").get(0), false, false, null, null, null).save();	
		newExperimentTypeNode("ext-to-solution-stock", getExperimentTypes("ext-to-solution-stock").get(0), false, false, null, null, null).save();	

		if(ConfigFactory.load().getString("ngl.env").equals("PROD")){
			newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false,false,getExperimentTypeNodes("ext-to-qpcr"),
				null,null).save();
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","solution-stock"),
					null,null).save();
			newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false,false,getExperimentTypeNodes("prepa-flowcell"),
					null,null).save();
		}
		
		newExperimentTypeNode("opgen-depot",getExperimentTypes("opgen-depot").get(0),false,false,getExperimentTypeNodes("ext-to-opgen-depot"),null,null).save();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			
			//Nanopore
			newExperimentTypeNode("ext-to-nanopore-depot", getExperimentTypes("ext-to-nanopore-depot").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("ext-to-nanopore-frg-preCR", getExperimentTypes("ext-to-nanopore-frg-preCR").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("ext-to-lib-ONT", getExperimentTypes("ext-to-lib-ONT").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("nanopore-fragmentation",getExperimentTypes("nanopore-fragmentation").get(0),false,false,getExperimentTypeNodes("ext-to-nanopore-frg-preCR"),
					null,null).save();
			newExperimentTypeNode("nanopore-library",getExperimentTypes("nanopore-library").get(0),false,false,getExperimentTypeNodes("ext-to-lib-ONT","nanopore-fragmentation"),
					null,null).save();
			newExperimentTypeNode("nanopore-depot",getExperimentTypes("nanopore-depot").get(0),false,false,getExperimentTypeNodes("nanopore-library","ext-to-nanopore-depot"),
					null,null).save();
			
			newExperimentTypeNode("ext-to-library", getExperimentTypes("ext-to-library").get(0), false, false, null, null, null).save();
			
			//REM : experimentTypes list confirmées par Julie
			newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("ext-to-library"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
			
			newExperimentTypeNode("librairie-indexing", getExperimentTypes("librairie-indexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
			
			/*newExperimentTypeNode("librairie-dualindexing", getExperimentTypes("librairie-dualindexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();*/
			
			newExperimentTypeNode("amplification", getExperimentTypes("amplification").get(0), false, false, getExperimentTypeNodes("librairie-indexing"), 
					getExperimentTypes("ampure-a"), getExperimentTypes("fluo-quantification","chip-migration-post-pcr","qPCR-quantification")).save();
						
			newExperimentTypeNode("qPCR-quantification",getExperimentTypes("qPCR-quantification").get(0),false,false,getExperimentTypeNodes("amplification"),null,null).save();

			newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false,false,getExperimentTypeNodes("ext-to-qpcr","amplification"),
					null,null).save();
			
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","solution-stock"),
					null,null).save();

			/* GA : 03/08/2015 old declaration
			newExperimentTypeNode("lib-normalization",getExperimentTypes("lib-normalization").get(0),false,false,getExperimentTypeNodes("ext-to-qpcr"),
					null,null).save();
			*/
			newExperimentTypeNode("ext-to-denat-dil-lib", getExperimentTypes("ext-to-denat-dil-lib").get(0), false, false, null, null, null).save();
			
			
			// GA : 03/08/2015 new temporary declaration
			newExperimentTypeNode("lib-normalization",getExperimentTypes("lib-normalization").get(0),false,false,null,null,null).save();
			
			newExperimentTypeNode("denat-dil-lib",getExperimentTypes("denat-dil-lib").get(0),false,false,getExperimentTypeNodes("ext-to-denat-dil-lib", "lib-normalization"),
					null,null).save();
			
			
			newExperimentTypeNode("prepa-flowcell-cng",getExperimentTypes("prepa-flowcell-cng").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","denat-dil-lib"),
					null,null).save();
			
	        // FDS 27/02/2015 supression "illumina-depot-cng"
			newExperimentTypeNode("pool-tube",getExperimentTypes("pool-tube").get(0),false,false,getExperimentTypeNodes("solution-stock","lib-normalization","nanopore-library"),
					null,null).save();
			
			newExperimentTypeNode("aliquoting",getExperimentTypes("aliquoting").get(0),false,false,getExperimentTypeNodes("nanopore-fragmentation"),
					null,null).save();
			
			newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false,false,getExperimentTypeNodes("prepa-flowcell","prepa-flowcell-cng"),
					null,null).save();
		}
		


	}

/*	private static List<Protocol> getProtocols(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(Protocol.class,Protocol.find, codes);
	}*/

	private static List<InstrumentUsedType> getInstrumentUsedTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(InstrumentUsedType.class,InstrumentUsedType.find, codes);
	}

	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}

	private static List<ExperimentTypeNode> getExperimentTypeNodes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentTypeNode.class,ExperimentTypeNode.find, codes);
	}

	
	private static List<PropertyDefinition> getPropertyAliquoting() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","pickedUpVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µl"),MeasureUnit.find.findByCode( "µl"), "single",10));
		propertyDefinitions.add(newPropertiesDefinition("Quantité mesurée","mesuredQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",20));
		propertyDefinitions.add(newPropertiesDefinition("Nombre de out","outNumber", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, null, null, null, "single",20));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyFragmentationNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb fragmentations","fragmentionNumber",LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, true, null
				, null ,null,null, "single",11));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Qté totale dans frg","inputFrgQuantity",LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content), Double.class, true,  null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",12));
		
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Conc. finale FRG","postFrgConcentration",LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",3));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Qté finale FRG","postFrgQuantity",LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",4));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Profil","fragmentionProfile",LevelService.getLevels(Level.CODE.ContainerOut), Image.class, false, null
				,null,null,null, "img",5));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille réelle","measuredLibrarySize",LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), Integer.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "kb"),MeasureUnit.find.findByCode( "kb"), "single",6));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb preCR","preCRNumber",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false, null
				, null ,null,null, "single",7));
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
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single",300));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("PDF Report","report",LevelService.getLevels(Level.CODE.Experiment), File.class, false, "file"));

        // Unite a verifier
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Date creation","loadingReport.creationDate",LevelService.getLevels(Level.CODE.ContainerIn), Date.class, false, null
				, null,null,null, "object_list",50));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Heure dépot","loadingReport.hour",LevelService.getLevels(Level.CODE.ContainerIn), String.class, false,null
				, null,null,null,"object_list",100));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Temps","loadingReport.time",LevelService.getLevels(Level.CODE.ContainerIn), Long.class, false,null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_TIME),MeasureUnit.find.findByCode( "h"),MeasureUnit.find.findByCode( "h"), "object_list",200));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Volume","loadingReport.volume",LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "object_list",300));

		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Groupe","qcFlowcell.group",LevelService.getLevels(Level.CODE.ContainerOut), String.class, false,null
				, null,null,null, "object_list",100));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb pores actifs avant dépot","qcFlowcell.preLoadingNbActivePores",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false,null
				, null, null, null, "object_list",200));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb pores actifs après dépot","qcFlowcell.postLoadingNbActivePores",LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, false, null
				, null,null,null, "object_list",300));
		
		propertyDefinitions.add(newPropertiesDefinition("Channels with Reads", "minknowChannelsWithReads", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Events in Reads", "minknowEvents", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Complete reads", "minknowCompleteReads", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Read count", "metrichorReadCount", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, "single",700));
        propertyDefinitions.add(newPropertiesDefinition("Total 2D yield", "metrichor2DReadsYield", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "pb"),MeasureUnit.find.findByCode( "pb"), "single",800));
        propertyDefinitions.add(newPropertiesDefinition("Longest 2D read", "metrichorMax2DRead", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "pb"),MeasureUnit.find.findByCode( "pb"),"single",900));
        propertyDefinitions.add(newPropertiesDefinition("Peak 2D quality score", "metrichorMax2DQualityScore", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, false, "single",1000));

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyLibrairieNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µl"),MeasureUnit.find.findByCode( "µl"), "single",10));
		propertyDefinitions.add(newPropertiesDefinition("Qté engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",11));
//		propertyDefinitions.add(newPropertiesDefinition("Taille", "librarySize", LevelService.getLevels(Level.CODE.ContainerOut), Integer.class, true, null
	//			, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "kb"),MeasureUnit.find.findByCode( "kb"), "single",8));
		propertyDefinitions.add(newPropertiesDefinition("Conc. finale End Repair","postEndRepairConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",10));
		propertyDefinitions.add(newPropertiesDefinition("Qté finale End Repair","postEndRepairQuality", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",20));

		propertyDefinitions.add(newPropertiesDefinition("Conc. finale dA tailing","postTailingConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",30));
		propertyDefinitions.add(newPropertiesDefinition("Qté finale dA tailing","postTailingQuality", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",40));

		
/*		propertyDefinitions.add(newPropertiesDefinition("Conc. finale Ligation","measuredConcentration", LevelService.getLevels(Level.CODE.ContainerOut),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µl"),MeasureUnit.find.findByCode( "ng/µl"), "single",50));
		// Problème code car doit etre measuredquantity
		propertyDefinitions.add(newPropertiesDefinition("Qté finale Ligation","ligationQuantity", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"), "single",60)); */

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}
	
	// GA separation getPropertyDefinitionsPrepaflowcellCNS / getPropertyDefinitionsPrepaflowcellCNG pour JIRA 676
	//  ==> feuille de calcul differentes pour la prepaflowcell entre CNS et CNG
	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellCNS() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Outputcontainer
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",1,true,"1"));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",2));
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume sol. stock à engager dénat.", "requiredVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",1, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",2,true,"1"));
		
		propertyDefinitions.add(newPropertiesDefinition("Conc. solution NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true,DescriptionFactory.newValues("1N","2N"), "2N", "single",3));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume EB", "EBVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",4, false));

		propertyDefinitions.add(newPropertiesDefinition("Concentration dénat. ", "finalConcentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",5,true,"2"));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat.", "finalVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "20"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",6));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat. à engager dans dilution", "requiredVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",7, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume HT1", "HT1Volume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",8, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume Phix", "phixVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",9, false));

		propertyDefinitions.add(newPropertiesDefinition("Conc. sol. mère Phix", "phixConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"),"single",10, true,"0.02"));

		propertyDefinitions.add(newPropertiesDefinition("Conc. dilution", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",11));

		propertyDefinitions.add(newPropertiesDefinition("Volume dilution", "finalVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "1000"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",12));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dilution à engager sur la piste", "requiredVolume3", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",13, false));		
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Outputcontainer
		//Outputcontainer
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",1,true,"1"));		
		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
						, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",2));
				
				//InputContainer
				
				
		propertyDefinitions.add(newPropertiesDefinition("Conc. dilution", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
						, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",11));

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsDenatDilLibCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Stockage", "storage", LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, null, null, null, null, "single",20,true,null));		
		
		return propertyDefinitions;
	}

	

	private static List<PropertyDefinition> getPropertyDefinitionsLibIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Tag","tag", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie tag","tagCategory", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}

	
	
	
	private static List<PropertyDefinition> getPropertyDefinitionsLibDualIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Index1","tag1", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Index2","tag2", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		return propertyDefinitions;
	}


	//TODO
	// Propriete taille en output et non en input ?
	// Valider les keys
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigration() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.ContainerIn),Integer.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "size", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		// Voir avec Guillaume comment gérer les fichiers
		propertyDefinitions.add(newPropertiesDefinition("Profil DNA HS", "fileResult", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Utiliser par import ngl-data CNG de creation des depot-illumina
		//propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Experiment), Integer.class, false, "single"));	
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getPropertyDefinitionExtToOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, false, "single"));
		return propertyDefinitions;
	}	
	
	private static List<PropertyDefinition> getPropertyDefinitionOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionSolutionStock() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume à engager", "requiredVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",6, false));
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",7, false));
		//Outputcontainer
/*		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode("nM"),"single",7,true,"10.0"));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",8,true)); */
		return propertyDefinitions; 
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionPoolTube() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",8, false));
	/*	propertyDefinitions.add(newPropertiesDefinition("Volume tampon à rajouter", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",9, false)); */
		//Outputcontainer
		/*	propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",13,true));
		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",14,true));	*/	
		
		return propertyDefinitions;
	}

}
