package services.description.instrument;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.typesafe.config.ConfigFactory;

import akka.util.Collections;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

public class InstrumentServiceCNG extends AbstractInstrumentService{
	
	
	public void saveInstrumentCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<InstrumentCategory> l = new ArrayList<InstrumentCategory>();
		
		l.add(newInstrumentCategory("Covaris","covaris"));
		l.add(newInstrumentCategory("Spri","spri"));
		l.add(newInstrumentCategory("Thermocycleur","thermocycler"));
		l.add(newInstrumentCategory("Centrifugeuse","centrifuge"));
		
		// FDS 29/01/2016 JIRA NGL-894 (couple d'instruments...)
		l.add(newInstrumentCategory("Covaris + Robot pipetage","covaris-and-liquid-handling-robot"));
		
		// FDS 22/03/2016 JIRA NGL-982 (couple d'instruments...)
		l.add(newInstrumentCategory("Robot pipetage + cBot","liquid-handling-robot-and-cBot"));
		
		// FDS 29/07/2016 JIRA NGL-1027 (couple d'instruments...)
		l.add(newInstrumentCategory("Thermocycleur + Robot pipetage","thermocycler-and-liquid-handling-robot"));
		
		l.add(newInstrumentCategory("Quantification par fluorométrie","fluorometer"));
		l.add(newInstrumentCategory("Appareil de qPCR","qPCR-system"));
		l.add(newInstrumentCategory("Electrophorèse sur puce","chip-electrophoresis"));
		
		l.add(newInstrumentCategory("Main","hand"));
		l.add(newInstrumentCategory("CBot","cbot"));
		
		l.add(newInstrumentCategory("Séquenceur Illumina","illumina-sequencer"));
		l.add(newInstrumentCategory("QC Séquenceur Illumina","qc-illumina-sequencer"));
		l.add(newInstrumentCategory("Cartographie Optique Opgen","opt-map-opgen"));
		l.add(newInstrumentCategory("Séquenceur Nanopore","nanopore-sequencer")); // FDS modifié 30/03/2017 NGL-1225
		l.add(newInstrumentCategory("Extérieur","extseq"));
		
		l.add(newInstrumentCategory("Robot pipetage","liquid-handling-robot"));
		l.add(newInstrumentCategory("Appareil de sizing","sizing-system"));
		
		// FDS 20/02/2017 NGL-1167 (Chromium)
		l.add(newInstrumentCategory("10x Genomics Instrument","10x-genomics-instrument"));
		
		
				
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();		
		
		// 27/07/2016 la main peut traiter des plaques en entree ET en sortie; 02/03/2017 ajout strip-8
		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, 
				getInstruments(
						createInstrument("hand", "Main", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)) ),
				getContainerSupportCategories(new String[]{"tube","96-well-plate","strip-8"}),
				getContainerSupportCategories(new String[]{"tube","96-well-plate","strip-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		
	    /** cBots and sequencers **/	
		l.add(newInstrumentUsedType("cBot", "cBot", InstrumentCategory.find.findByCode("cbot"), getCBotProperties(), 
				getInstruments(
						// 16/01/2017 cbot ancienne version plus sur site => desactiver ??? on ne plus faire de recherche !!!
						createInstrument("cBot1", "cBot1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot2", "cBot2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot3", "cBot3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot4", "cBot4", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// 23/01/2017 separation des cbot-v2 pour mieux gerer leur proprietes	
		l.add(newInstrumentUsedType("cBot-v2", "cBotV2", InstrumentCategory.find.findByCode("cbot"), getCBotV2Properties(), 
				getInstruments(
						// 19/09/20167 ajout 6 cbots V2: possibilité de lire le code barre du strip et d'importer un fichier XML...
						createInstrument("cBotA", "cBotA", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBotB", "cBotB", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBotC", "cBotC", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBotD", "cBotD", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBotE", "cBotE", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBotF", "cBotF", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("cBot-onboard", "cBot-onboard", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(
						createInstrument("cBot-Hi9",     "cBot-interne-Hi9",     null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi10",    "cBot-interne-Hi10",    null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi11",    "cBot-interne-Hi11",    null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Miseq1",  "cBot-interne-Miseq1",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-NextSeq1","cBot-interne-Nextseq1",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1","flowcell-4" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
	
		l.add(newInstrumentUsedType("MISEQ", "MISEQ", InstrumentCategory.find.findByCode("illumina-sequencer"), getMiseqProperties(), 
				getInstrumentMiSeq(),
				getContainerSupportCategories(new String[]{"flowcell-1"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("MISEQ QC", "MISEQ-QC-MODE", InstrumentCategory.find.findByCode("qc-illumina-sequencer"), getMiseqQCProperties(), 
				getInstrumentMiSeqQC(),
				getContainerSupportCategories(new String[]{"96-well-plate","tube"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		l.add(newInstrumentUsedType("HISEQ2000", "HISEQ2000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2000Properties(), 
				getInstrumentHiseq2000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQ2500", "HISEQ2500", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2500Properties(), 
				getInstrumentHiseq2500(),
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("NEXTSEQ500", "NEXTSEQ500", InstrumentCategory.find.findByCode("illumina-sequencer"), getNextseq500Properties(), 
				getInstrumentNextseq500(),
				getContainerSupportCategories(new String[]{"flowcell-4"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQ4000", "HISEQ4000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq4000Properties(), 
				getInstrumentHiseq4000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQX", "HISEQX", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseqXProperties(), 
				getInstrumentHiseqX(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** chip-electrophoresis **/
		// FDS 24/02/2017 ajouter strip-8 en input
		l.add(newInstrumentUsedType("Agilent 2100 bioanalyzer", "agilent-2100-bioanalyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"),getBioanalyzerProperties(), 
				getInstruments(
						createInstrument("bioAnalyzer1", "BioAnalyzer1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("bioAnalyzer2", "BioAnalyzer2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // ajout 30/03/2017
				getContainerSupportCategories(new String[]{"tube","strip-8"}),
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// pas de properties ????
		// FDS 01/09/2016 nom et code incorrects -/- specs!!! Laisser le code mais corriger le name; 
		// FDS 12/01/2017 ajout CHIP_GX2;  24/02/217 BUG:le code etait en doublon 
		l.add(newInstrumentUsedType("LabChip GX", "labChipGX", InstrumentCategory.find.findByCode("chip-electrophoresis"), null, 
				getInstruments(
						createInstrument("labGX",  "LABCHIP_GX1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)) ,
						createInstrument("labGX2", "LABCHIP_GX2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))) ,
				getContainerSupportCategories(new String[]{"384-well-plate","96-well-plate"}),
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** thermocyclers **/
		// FDS a renommer pour etre coherent avec thermocyclers-and-liquid-handling-robot ????/
		l.add(newInstrumentUsedType("Thermocycleur", "thermocycler", InstrumentCategory.find.findByCode("thermocycler"), getThermocyclerProperties(), 
				getInstruments(
						createInstrument("thermo1", "Thermo1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("thermo2", "Thermo2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("thermo3", "Thermo3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("thermo4", "Thermo4", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		//FDS ajout 03/04/2017 NGL-1225:  Mastercycler Nexus SX1 seul ( input tubes ou plaques / output tubes ou  plaques)
		l.add(newInstrumentUsedType("Mastercycler Nexus-SX1", "mastercycler-nsx1", InstrumentCategory.find.findByCode("thermocycler"), getThermocyclerProperties(), 
				getInstruments(
						createInstrument("mastercycler1", "Mastercycler 1 (Nexus SX1)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		/** covaris **/
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov1", "Cov1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		l.add(newInstrumentUsedType("Covaris LE220", "covaris-le220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov2", "Cov2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG))); 
		
		l.add(newInstrumentUsedType("Covaris E220", "covaris-e220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov3", "Cov3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		
		/** quality **/
		l.add(newInstrumentUsedType("qPCR (Lightcycler 480 II)", "qpcr-lightcycler-480II", InstrumentCategory.find.findByCode("qPCR-system"), getLightCyclerProperties(), 
				getInstruments(
						createInstrument("lightCycler1", "LightCycler1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("lightCycler2", "LightCycler2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("lightCycler3", "LightCycler3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
			
		l.add(newInstrumentUsedType("QuBit", "qubit", InstrumentCategory.find.findByCode("fluorometer"), getQuBitProperties(), 
				getInstruments(
						createInstrument("quBit1", "QuBit1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** liquid-handling-robot  **/
		// 16/09/2016 un seul Janus pour l'instant => Janus1 
		l.add(newInstrumentUsedType("Janus", "janus", InstrumentCategory.find.findByCode("liquid-handling-robot"), getJanusProperties(), 
				getInstruments(
						createInstrument("janus1", "Janus1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		//FDS ajout 04/08/2016 JIRA NGL-1026: Sciclone NGSX seul
		l.add(newInstrumentUsedType("Sciclone NGSX", "sciclone-ngsx", InstrumentCategory.find.findByCode("liquid-handling-robot"), getScicloneNGSXAloneProperties(), 
				getInstruments(
						createInstrument("ngs1", "NGS-1",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("ngs2", "NGS-2",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		//FDS ajout 04/10/2016 Epimotion (input plate / output tubes)
		l.add(newInstrumentUsedType("EpMotion", "epmotion", InstrumentCategory.find.findByCode("liquid-handling-robot"), getEpMotionProperties(), 
				getInstruments(
						createInstrument("epmotion1", "EpMotion1",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		
		// FDS ajout 29/01/2016 JIRA NGL-894 pseudo instruments covaris+Sciclone (plaque input/plaque output) 
		l.add(newInstrumentUsedType("Covaris E210 + Sciclone NGSX", "covaris-e210-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris1-and-ngs1", "Covaris1 / NGS-1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris1-and-ngs2", "Covaris1 / NGS-2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// 05/12/2016 SUPSQCNG-429 erreur label : LE220 et pas LE210
		l.add(newInstrumentUsedType("Covaris LE220 + Sciclone NGSX", "covaris-le220-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris2-and-ngs1", "Covaris2 / NGS-1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris2-and-ngs2", "Covaris2 / NGS-2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
		l.add(newInstrumentUsedType("Covaris E220 + Sciclone NGSX", "covaris-e220-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris3-and-ngs1", "Covaris3 / NGS-1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris3-and-ngs2", "Covaris3 / NGS-2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
		
		// FDS ajout 22/03/2016 JIRA NGL-982 pseudo instruments Janus+Cbot  
		//  23/01/2017 les cbots ancien modele n'existent plus => desactiver ??? on ne peut plus faire de recherche !!!
		l.add(newInstrumentUsedType("Janus + cBot", "janus-and-cBot", InstrumentCategory.find.findByCode("liquid-handling-robot-and-cBot"), getJanusAndCBotProperties(), 
				getInstruments(
						createInstrument("janus1-and-cBot1", "Janus1 / cBot1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot2", "Janus1 / cBot2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot3", "Janus1 / cBot3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot4", "Janus1 / cBot4", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 23/01/2017 ajout Janus + cBot-v2 
		l.add(newInstrumentUsedType("Janus + cBot-v2", "janus-and-cBotV2", InstrumentCategory.find.findByCode("liquid-handling-robot-and-cBot"), getJanusAndCBotV2Properties(), 
				getInstruments(
						createInstrument("janus1-and-cBotA", "Janus1 / cBotA", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotB", "Janus1 / cBotB", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotC", "Janus1 / cBotC", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotD", "Janus1 / cBotD", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotE", "Janus1 / cBotE", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotF", "Janus1 / cBotF", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 29/07/2016 JIRA NGL-1027 pseudo instrument Masterycler EP-Gradient + Zephyr
		// 16/09/2016 un seul Zephyr pour l'instant donc=> Zephyr1; ne laisser que Mastercycler1 et Mastercycler2
		l.add(newInstrumentUsedType("Mastercycler EP-Gradient + Zephyr", "mastercycler-epg-and-zephyr", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getMastercyclerEPGAndZephyrProperties(), 
				getInstruments(
						createInstrument("mastercycler1-and-zephyr1", "Mastercycler1 (EP-Gradient) / Zephyr1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler2-and-zephyr1", "Mastercycler2 (EP-Gradient) / Zephyr1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS prevision.... pseudo instrument Masterycler Nexus SX1 + Zephyr 
		l.add(newInstrumentUsedType("Mastercycler Nexus-SX1 + Zephyr", "mastercycler-nsx1-epg-and-zephyr", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getMastercyclerNSX1AndZephyrProperties(), 
				getInstruments(
						createInstrument("mastercycler5-and-zephyr1", "Mastercycler5 (Nexus SX1) / Zephyr1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler6-and-zephyr1", "Mastercycler6 (Nexus SX1) / Zephyr1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 20/02/2017 NGL-1167 : Chromium controller ( entree tubes / sortie strip-8
		l.add(newInstrumentUsedType("Chromium controller", "chromium-controller", InstrumentCategory.find.findByCode("10x-genomics-instrument"), getChromiumControllerProperties(), 
				getInstruments(
						createInstrument("chromium1", "Chromium 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"strip-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** nanopore sequencers **/
		// FDS ajout 30/03/2017 : NGL-1225 ( Nanopore )
		l.add(newInstrumentUsedType("MinION", "minION", InstrumentCategory.find.findByCode("nanopore-sequencer"), getNanoporeSequencerProperties(),
				getInstrumentMinIon(), 
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-1"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Mk1", "mk1", InstrumentCategory.find.findByCode("nanopore-sequencer"), getNanoporeSequencerProperties(), 
				getInstrumentMKI(), 
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-1"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Mk1B", "mk1b", InstrumentCategory.find.findByCode("nanopore-sequencer"), getNanoporeSequencerProperties(),
				getInstrumentMKIB(), 
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-1"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** centrifugeuses **/
		l.add(newInstrumentUsedType("Eppendorf Centrifuge 5424", "eppendorf-5424", InstrumentCategory.find.findByCode("centrifuge"), getEppendorf5424Properties(), 
				getInstruments(
						createInstrument("eppendorf-5424-1","Eppendorf 5424", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		
		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}
	

	/*** get properties methods ***/

	private static List<PropertyDefinition> getCBotProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		// 17/01/2017 numérotation des propriétés
        l.add(newPropertiesDefinition("Type lectures","sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true, null, DescriptionFactory.newValues("SR","PE"),"single", 70,true,null,null));
        l.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true,null, null,"single", 80,true,null,null));         
        l.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true, null, DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2","3","4","5","6","7","8"),"single", 90,true,"Pas de piste contrôle (auto-calibrage)", null));     

        return l;
	}

	
	private static List<PropertyDefinition> getCBotInterneProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		/* 23/01/2017 strictement la meme liste que cBot standard!! simplification...*/    
		l.addAll(getCBotProperties());
		
        return l;
	}
	
	// 23/01/2017 creation methode distincte...
	private static List<PropertyDefinition> getCBotV2Properties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		// propriete des V1
		l.addAll(getCBotProperties());
		
		// proprietes specifiques V2: NGL-1141: ne pas mettre ces proprietes en obligatoires=> pose probleme. Utiliser une regle drool pour l'experience prepa-flowcell
		
        l.add(newPropertiesDefinition("Code Strip", "stripCode", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null, null, "single", 60, true, null,null));
    	// fichier generé cbotRunFile" (NON editable)
        l.add(newPropertiesDefinition("Fichier cBot", "cbotFile", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null, null, "single", 150, false ,null,null));
       
        return l;
	}
	

	private static List<PropertyDefinition> getHiseq2000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
        propertyDefinitions.add(newPropertiesDefinition("Position","position"	, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",700));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single",100));
       
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getMiseqProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Nom cassette Miseq", "miseqReagentCassette",LevelService.getLevels(Level.CODE.Instrument),String.class,true,"single",100));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
       
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getMiseqQCProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Nom cassette Miseq", "miseqReagentCassette",LevelService.getLevels(Level.CODE.Instrument),String.class,true,"single",100));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
        //propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        //propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
       
        propertyDefinitions.add(newPropertiesDefinition("Genome folder", "genomeFolder", LevelService.getLevels(Level.CODE.Instrument),String.class,true, null, null, "single", 700, true, "Homo_sapiens\\UCSC\\hg19\\Sequence\\WholeGenomeFasta", null));
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getNextseq500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
       
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getHiseq2500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = getHiseq2000Properties();		
		
	   propertyDefinitions.add(0, newPropertiesDefinition("Mode run","runMode"
	        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("high-throughput","rapid run"), "single",50));
	   
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getHiseq4000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Position","position"
		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getHiseqXProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	//FDS 02/02/2016 modifier 'program' en 'programCovaris' pour pourvoir creer les proprietes de l'instrument mixte
	//    covaris+Sciclone car sinon doublon de proprietes...
	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Programme Covaris", "programCovaris", LevelService.getLevels(Level.CODE.Instrument), String.class, true,
				                       newValues("PCR FREE PROD NGS FINAL"), "PCR FREE PROD NGS FINAL", "single"));
		return l;
	}
	
	// FDS a renommer "Nbre de Cycles / pcrCycleNumber => necessite une reprise d'historique !!! NON CAR aucune experience en base
	// pourquoi ces 2 valeurs 15 et 18 ???????????
	private static List<PropertyDefinition> getThermocyclerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Nbre de Cycles", "pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument), String.class, true,
				                       newValues("15","18"), "single"));		
		return l;
	}
	
	
	private static List<PropertyDefinition> getQuBitProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Kit", "kit", LevelService.getLevels(Level.CODE.Instrument), String.class, true, 
				                       newValues("HS", "BR"), "single"));		
		return l;
	}

	
	//FDS 29/01/2016 ajout SicloneNGSX -- JIRA NGL-894
	private static List<PropertyDefinition> getScicloneNGSXProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		//FDS 25/10/2016 -- NGL-1025 : nouvelle liste (!! pas de contextualisation, tous les programmes seront listés dans toutes les experiences)
		// => les séparer au moins a la declaration..	
		ArrayList<String> progList = new ArrayList<String>();
		
		// RNA 12/12/2016
		progList.add("Stranded_TotalRNA_Avril2016");
		progList.add("Stranded_TotalRNA_Avril2016_RAP_Plate");
		progList.add("Stranded_mRNA_Avril2016");
		progList.add("Stranded_mRNA_Avril2016_RAP_Plate");
		
        //Nano
		progList.add("TruSEQ_DNA_Nano");
		
		//PCR free
		progList.add("TruSEQ_DNA_PCR_Free_Library_Prep");
		progList.add("TruSEQ_DNA_PCR_Free_Library_Prep_DAP_Plate");

		//transformer ArrayList progList en Array progList2 car newValue() prend un Array en argument !!
		String progList2[] = new String[progList.size()];
		progList2 = progList.toArray(progList2);
       
		//prop obligatoire
		l.add(newPropertiesDefinition("Programme Sciclone NGSX", "programScicloneNGSX", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				                       newValues(progList2), "single",null,false, null,null));
		
		return l;
	}

	
	// 05/08/2016 Il faut une methode distincte pour ajouter la propriété "robotRunCode", et ne pas la mettre directement dans getScicloneNGSXProperties
	// sinon il y a un doublon pour l'instrument fictif CovarisAndScicloneNGSX
	private static List<PropertyDefinition> getScicloneNGSXAloneProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.addAll(getScicloneNGSXProperties());
		
		l.add(newPropertiesDefinition("Nom du Run","robotRunCode", LevelService.getLevels(Level.CODE.Instrument),  String.class, false, null,
				null, null, null, null, "single", null, true ,null, null));
		return l;
	}
	
	//FDS 29/01/2016 (instrument fictif composé de 2 instruments) -- JIRA NGL-894
	//    ses propriétés sont la somme des propriétés de chacun (Attention au noms de propriété communs...)
	private static List<PropertyDefinition> getCovarisAndScicloneNGSXProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.addAll(getCovarisProperties());
		l.addAll(getScicloneNGSXProperties());
		
		l.add(newPropertiesDefinition("Nom du Run","robotRunCode", LevelService.getLevels(Level.CODE.Instrument),  String.class, false, null,
				null, null, null, null, "single", null, true ,null, null));
		return l;
	}
	
	//FDS 29/01/2016 ajout Janus -- JIRA NGL-894 
	private static List<PropertyDefinition> getJanusProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		//FDS 05/08/2016 le Janus est utilisé dans certaines experiences ou on ne veut pas tracer le programme => rendre cette propriété non obligatoire !
		//FDS 25/10/2016 -- NGL-1025 : nouvelle liste (!! pas de contextualisation, tous les programmes seront listés dans toutes les experiences)
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, false, null,
				newValues("programme 1_normalisation",    // normalization
						  "1_HiseqCluster_Normalisation_V0",
						  "1_HiseqCluster_Normalisation_gros_vol_tris"),
						  "single", null, false ,null, null));
		return l;
	}
	
	
	//FDS 22/03/2016 ajout Janus+cbot --JIRA NGL-982
	//    17/01/2017 numérotation des propriétés;
	 private static List<PropertyDefinition> getJanusAndCBotProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				 newValues("Clusterstripprepworklist"), "single", 40, false ,null, null));
		
		l.add(newPropertiesDefinition("Strip #", "stripDestination", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				newValues("1","2","3","4"), "single", 50, true ,null, null));
		
        // propriété de container in !!!
        l.add(newPropertiesDefinition("Source", "source", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, "N",
				 Arrays.asList(newValue("1", "Source 1"), newValue("2", "Source 2"), newValue("3", "Source 3"),newValue("4", "Source 4")), "single", 2, true , null, null));
				
		l.addAll(getCBotProperties());
		
		return l;
	}
	 
	//FDS 23/01/2017 ajout Janus + cbot-v2
	private static List<PropertyDefinition> getJanusAndCBotV2Properties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
			
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				 newValues("Clusterstripprepworklist"), "single", 40, false ,null, null));
			
		l.add(newPropertiesDefinition("Strip #", "stripDestination", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				newValues("1","2","3","4"), "single", 50, true ,null, null));
			
	    // propriété de container in !!!
	    l.add(newPropertiesDefinition("Source", "source", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, "N",
				 Arrays.asList(newValue("1", "Source 1"), newValue("2", "Source 2"), newValue("3", "Source 3"),newValue("4", "Source 4")), "single", 2, true , null, null));
					
	    l.addAll(getCBotV2Properties());
			
		return l;
	} 
	 
	//FDS 04/10/2016 ajout EpMotion
	private static List<PropertyDefinition> getEpMotionProperties() throws DAOException {
			List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
			// propriete obligatoire ou pas ??????
			// liste des programmes pas encore definie
			l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, false, null,
					newValues("programme 1",  
							  "---"),                         // ajouté pour éviter selection par defaut
							  "single", null, false ,null, null));
			return l;
	} 
	 
	//FDS 31/03/2016 ajout proprietes LightCyclers
	private static List<PropertyDefinition> getLightCyclerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Secteur Plaque 96","sector96", LevelService.getLevels(Level.CODE.Instrument),String.class, true, null,
				newValues("1-48","49-96"), null, null , null, "single", null, false ,null, null));
		
		return l;
	}
	
	//-------------------voir s'il faut ne garder qu'une seule méthode et non pas 2 pour les groupes de Masteryclers.....
	
	// FDS 29/07/2016 JIRA NGL-1027 ajout propriétés pseudo instrument Masterycler EP-Gradient + Zephyr 
	private static List<PropertyDefinition> getMastercyclerEPGAndZephyrProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		//Mastercycler
		l.add(newPropertiesDefinition("Nbre Cycles PCR","pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, null,
				null, null, null , null, "single", null, true ,"14", null));

		//Zephyr
		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
				null, null, null , null, "single", null, true ,"0.8", null));
		return l;
	}
	
	// FDS 29/07/2016 JIRA NGL-1027 ajout propriétés pseudo instrument Masterycler NSX1 + Zephyr 
	private static List<PropertyDefinition> getMastercyclerNSX1AndZephyrProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		//Mastercycler
		l.add(newPropertiesDefinition("Nbre Cycles PCR","pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, null,
				null, null, null , null, "single", null, true ,"14", null));
		//Zephyr
		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
				null, null, null , null, "single", null, true ,"0.8", null));
		return l;
	}
	
	//FDS 20/02/2017 NGL-1167: Chromium controller
	private static List<PropertyDefinition> getChromiumControllerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		//06/03/2017 chipPosition est une propriete d'instrument et pas d'experience...
		//14 03 essayer ne ne mettre obligatoire qu'a 'F' ??????? =>pire
		l.add(newPropertiesDefinition("Position sur puce", "chipPosition", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, null, 
					newValues("1","2","3","4","5","6","7","8"), 
					"single",23, true, null,null));

		return l;
	}
	
	//FDS 01/03/2017 NGL-1167: QC bioanalyser ajouté pour process Chromium
	private static List<PropertyDefinition> getBioanalyzerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		// Production CNG demande de ne pas tracer le type de puce...
		// l.add(newPropertiesDefinition("Type puce", "chipType", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null, newValues("HS", "1K, 12K"), 
		//		"single", 10, true, null,null));
		
		// reunion avec Marc23/03/2017: la puce  HS n' a que 11 positions utilisable mais les puces 1K, 12K en ont 12=> ajouter position 12
		l.add(newPropertiesDefinition("Position sur puce", "chipPosition", LevelService.getLevels(Level.CODE.ContainerIn), String.class, false, null, 
					newValues("1","2","3","4","5","6","7","8","9","10","11","12"), 
					"single", 11, true, null,null));
		
		return l;
	}

	
	// FD meme proprietes que minispin ???
	private static List<PropertyDefinition> getEppendorf5424Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
        propertyDefinitions.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument),String.class, true,
        		DescriptionFactory.newValues("G-TUBE"), "G-TUBE", null, null, null, "single", 1));
        
        propertyDefinitions.add(newPropertiesDefinition("Vitesse", "speed", LevelService.getLevels(Level.CODE.Instrument),String.class, false,
        		null, "8000", MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SPEED),
        					  MeasureUnit.find.findByCode("rpm"),
        					  MeasureUnit.find.findByCode("rpm"), "single", 2));
        
        // unite s
        propertyDefinitions.add(newPropertiesDefinition("Durée", "duration", LevelService.getLevels(Level.CODE.Instrument),String.class, false, 
        		null, "60", MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_TIME),
        				    MeasureUnit.find.findByCode("s"),
        				    MeasureUnit.find.findByCode("s"), "single", 3));
		return propertyDefinitions;
	}
	
	
	// FDS ajout 30/03/2017 NGL-1225 (Nanopore)
	private static List<PropertyDefinition> getNanoporeSequencerProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		// propriété CNS.. a adapter !!!!
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Version Flowcell", "flowcellChemistry", LevelService.getLevels(Level.CODE.Instrument,Level.CODE.Content),String.class, true, "single",200,"R9.4-spot-on"));
       
        //Liste a definir
        propertyDefinitions.add(newPropertiesDefinition("Identifiant PC", "pcId", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",300));

		return propertyDefinitions;
	}
	
	/*** get lists methods ***/
	// FDS 20/07/2016 JIRA SUPSQCNG-392 : ajout short names
	private static List<Instrument> getInstrumentMiSeq() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("MISEQ1", "MISEQ1", "M1", true,  "/env/ig/atelier/illumina/cng/MISEQ1/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		instruments.add(createInstrument("MISEQ2", "MISEQ2", "M2", false, "/env/ig/atelier/illumina/cng/MISEQ2/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}
	
	private static List<Instrument> getInstrumentMiSeqQC() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("MISEQ1-QC", "MISEQ1 QC", null, false, "/env/ig/atelier/illumina/cng/MISEQ1/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		instruments.add(createInstrument("MISEQ2-QC", "MISEQ2 QC", null, true,  "/env/ig/atelier/illumina/cng/MISEQ2/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}
	
	private static List<Instrument> getInstrumentNextseq500() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("NEXTSEQ1", "NEXTSEQ1", "N1", true, "/env/ig/atelier/illumina/cng/NEXTSEQ1/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}

	private static List<Instrument> getInstrumentHiseq4000() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("FALBALA", "FALBALA", "H4", true, "/env/ig/atelier/illumina/cng/FALBALA/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );		
		return instruments;
	}
	
	private static List<Instrument> getInstrumentHiseqX() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();

		instruments.add(createInstrument("ASTERIX",   "ASTERIX",    "X1", true, "/env/ig/atelier/illumina/cng/ASTERIX/",    DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );	
		instruments.add(createInstrument("OBELIX",    "OBELIX",     "X2", true, "/env/ig/atelier/illumina/cng/OBELIX/",     DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );	
		instruments.add(createInstrument("IDEFIX",    "IDEFIX",     "X3", true, "/env/ig/atelier/illumina/cng/IDEFIX/",     DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		instruments.add(createInstrument("PANORAMIX", "PANORAMIX",  "X4", true, "/env/ig/atelier/illumina/cng/PANORAMIX/",  DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );			
		instruments.add(createInstrument("DIAGNOSTIX","DIAGNOSTIX", "X5", true, "/env/ig/atelier/illumina/cng/DIAGNOSTIX/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );	
		instruments.add(createInstrument("EXTHISEQX", "EXTHISEQX",  null, true, "/env/ig/atelier/illumina/cng/EXTHISEQX/",  DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}

	public static List<Instrument> getInstrumentHiseq2000() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("HISEQ1", "HISEQ1", null, true, "/env/ig/atelier/illumina/cng/HISEQ1/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ2", "HISEQ2", null, true, "/env/ig/atelier/illumina/cng/HISEQ2/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ3", "HISEQ3", null, true, "/env/ig/atelier/illumina/cng/HISEQ3/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ4", "HISEQ4", null, true, "/env/ig/atelier/illumina/cng/HISEQ4/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ5", "HISEQ5", null, true, "/env/ig/atelier/illumina/cng/HISEQ5/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ6", "HISEQ6", null, true, "/env/ig/atelier/illumina/cng/HISEQ6/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ7", "HISEQ7", null, true, "/env/ig/atelier/illumina/cng/HISEQ7/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ8", "HISEQ8", null, true, "/env/ig/atelier/illumina/cng/HISEQ8/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}

	
	public static List<Instrument> getInstrumentHiseq2500() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add( createInstrument("HISEQ9",  "HISEQ9",  "H1", true, "/env/ig/atelier/illumina/cng/HISEQ9/",  DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add( createInstrument("HISEQ10", "HISEQ10", "H2", true, "/env/ig/atelier/illumina/cng/HISEQ10/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add( createInstrument("HISEQ11", "HISEQ11", "H3", true, "/env/ig/atelier/illumina/cng/HISEQ11/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		return instruments;
	}
	
	// FDS ajout 30/03/2017 NGL-1225 (Nanopore)
	private List<Instrument> getInstrumentMKI() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		// LISTE ????

		return instruments;
	}
	
	// FDS ajout 30/03/2017 NGL-1225 (Nanopore)
	private List<Instrument> getInstrumentMKIB() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		// LISTE ????
		
		return instruments;
	}
	
	// FDS ajout 30/03/2017 NGL-1225 (Nanopore)
	private static List<Instrument> getInstrumentMinIon() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("MN18834", "MN18834", null, true, "/env/ig/atelier/nanopore/cng/MN18834xxxxxx", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		instruments.add(createInstrument("MN19213", "MN19213", null, true, "/env/ig/atelier/nanopore/cng/MN19213xxxxxx", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19240", "MN19240", null, true, "/env/ig/atelier/nanopore/cng/MN19240xxxxxx", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19270", "MN19270", null, true, "/env/ig/atelier/nanopore/cng/MN19270xxxxxx", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19813", "MN19813", null, true, "/env/ig/atelier/nanopore/cng/MN19813xxxxxx", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
		return instruments;
	}
	
}