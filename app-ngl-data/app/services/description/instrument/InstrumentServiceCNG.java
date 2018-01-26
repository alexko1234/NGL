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
		l.add(newInstrumentCategory("Quantification par spectrophotométrie","spectrophotometer"));
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
	
	// NOTE FDS 12/07/2017: attention lors de la modification du booleen 'active' sur un instrument il y a un cache de 1Heure
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
						// 16/01/2017 cbot ancienne version plus sur site => desactiver ???
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
		
		// 07/12/2017 NGL-1730 "cBot-MarieCurix-A et "cBot-MarieCurix-B
		l.add(newInstrumentUsedType("cBot-onboard", "cBot-onboard", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(	
						createInstrument("cBot-Hi9-A",   "cBot-interne-Hi9-A",    null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi9-B",   "cBot-interne-Hi9-B",    null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi10-A",  "cBot-interne-Hi10-A",   null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi10-B",  "cBot-interne-Hi10-B",   null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi11-A",  "cBot-interne-Hi11-A",   null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Hi11-B",  "cBot-interne-Hi11-B",   null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-Miseq1",  "cBot-interne-Miseq1",   null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-NextSeq1","cBot-interne-Nextseq1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-MarieCurix-A","cBot-interne-MarieCurix-A",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("cBot-MarieCurix-B","cBot-interne-MarieCurix-B",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"flowcell-4","flowcell-2","flowcell-1" }), 
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
		
		// 07/12/2017 NGL-1730: ajout Novaseq6000
		l.add(newInstrumentUsedType("NOVASEQ6000", "NOVASEQ6000", InstrumentCategory.find.findByCode("illumina-sequencer"), getNovaseq6000Properties(), 
				getInstrumentNovaseq6000(),
				getContainerSupportCategories(new String[]{"flowcell-2","flowcell-4"}), 
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		
		/* NOTE GENERALE 30/08/2017 
		 * les noms (names) de machine affichés a l'utilisateur se terminent par un numéro décollé du nom mais sans "-" exemple "BioAnalyzer 1" et pas "BioAnalyzer1" ni "BioAnalyzer-1"
		 */
		/** chip-electrophoresis **/
		// FDS 24/02/2017 ajouter strip-8 en input
		l.add(newInstrumentUsedType("Agilent 2100 bioanalyzer", "agilent-2100-bioanalyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"),getBioanalyzerProperties(), 
				getInstruments(
						createInstrument("bioAnalyzer1", "BioAnalyzer 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("bioAnalyzer2", "BioAnalyzer 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // ajout 30/03/2017
				getContainerSupportCategories(new String[]{"tube","strip-8"}),
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// pas de properties ????
		// FDS 01/09/2016 labGX: nom et code incorrects -/- specs!!! Laisser le code (car sinon reprise de donnees) mais corriger le name; 
		l.add(newInstrumentUsedType("LabChip GX", "labChipGX", InstrumentCategory.find.findByCode("chip-electrophoresis"), null, 
				getInstruments(
						createInstrument("labGX",  "LABCHIP_GX 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)) ,
						createInstrument("labGX2", "LABCHIP_GX 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))) ,
				getContainerSupportCategories(new String[]{"96-well-plate"}),
				null, 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** thermocyclers **/
		//FDS ajout 03/04/2017 NGL-1225:  Mastercycler Nexus SX1 seul (input tubes ou plaques / output tubes ou  plaques)
		//FDS 13/07/2017 "Mastercycler Nexus-SX1" et "Mastercycler Nexus" sont le meme type => fusionner !!!
		//               essai: mettre le nom de labo dans le "path"; 15/01/2018 LHD et LMPD ont fusionné!!
		l.add(newInstrumentUsedType("Mastercycler Nexus-SX1", "mastercycler-nexus", InstrumentCategory.find.findByCode("thermocycler"), getMastercyclerNexusProperties(), 
				getInstruments(
						createInstrument("mastercycler-nexus5", "Mastercycler 5 (Nexus SX1)", null, true, "ex-LHD", DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-nexus6", "Mastercycler 6 (Nexus SX1)", null, true, "ex-LHD", DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						
						createInstrument("mastercycler-nexus7", "Mastercycler 7 (Nexus SX1)", null, true, "ex-LMPD", DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-nexus8", "Mastercycler 8 (Nexus SX1)", null, true, "ex-LMPD", DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-nexus9", "Mastercycler 9 (Nexus SX1)", null, true, "ex-LMPD", DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
        
		
		//FDS 13/07/2017 le nom "Mastercycler Nexus Gradient" est incorrect =>"Mastercycler EP-Gradient"
		l.add(newInstrumentUsedType("Mastercycler EP-Gradient", "mastercycler-ep-gradient", InstrumentCategory.find.findByCode("thermocycler"), getMastercyclerEPGradientProperties(), 
				getInstruments(
						createInstrument("mastercycler-ep-gradient1", "Mastercycler 1 (EP Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-ep-gradient2", "Mastercycler 2 (EP Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-ep-gradient3", "Mastercycler 3 (EP Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler-ep-gradient4", "Mastercycler 4 (EP Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		
		
		/** covaris **/
		// FDS 16/06/2017 Covaris E210 plus utilisé=> inactiver
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("covaris1", "Covaris 1", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		// FDS correction 29/08/2017 les covaris utilisent aussi des plaques et pas seulement des tubes !!
		l.add(newInstrumentUsedType("Covaris LE220", "covaris-le220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("covaris2", "Covaris 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG))); 
		
		l.add(newInstrumentUsedType("Covaris E220", "covaris-e220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("covaris3", "Covaris 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		
		/** quality **/
		l.add(newInstrumentUsedType("qPCR (Lightcycler 480 II)", "qpcr-lightcycler-480II", InstrumentCategory.find.findByCode("qPCR-system"), getLightCyclerProperties(), 
				getInstruments(
						createInstrument("lightCycler1", "LightCycler 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("lightCycler2", "LightCycler 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("lightCycler3", "LightCycler 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				null,                                                               // pas de sortie pour les instruments * quality *
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 04/09/2017 pas de propriétés pour le QuBit; 29/11/2017 ajout qubit2
		l.add(newInstrumentUsedType("QuBit", "qubit", InstrumentCategory.find.findByCode("fluorometer"),getQuBitProperties(), 
				getInstruments(
						createInstrument("quBit1", "QuBit 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("quBit2", "QuBit 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}),
				null,                                                                // pas de sortie pour les instruments * quality *
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
        // FDS 03/08/2017 -- NL-1201: Ajout fluorometer Spectramax; FDS 04/09/2017 pas de propriétés
		l.add(newInstrumentUsedType("SpectraMax", "spectramax", InstrumentCategory.find.findByCode("spectrophotometer"), null, 
				getInstruments(
						createInstrument("spectramax-bank1", "SpectraMax Banque 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("spectramax-bank2", "SpectraMax Banque 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("spectramax-prod1", "SpectraMax Prod",     null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // pas encore livré (active=false)
				getContainerSupportCategories(new String[]{"96-well-plate"}),
				null,                                                                // pas de sortie pour les instruments * quality *
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** liquid-handling-robot  **/
		// 16/09/2016 un seul Janus pour l'instant => Janus1 
		l.add(newInstrumentUsedType("Janus", "janus", InstrumentCategory.find.findByCode("liquid-handling-robot"), getJanusProperties(), 
				getInstruments(
						createInstrument("janus1", "Janus 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		//FDS ajout 04/08/2016 JIRA NGL-1026: Sciclone NGSX seul
		l.add(newInstrumentUsedType("Sciclone NGSX", "sciclone-ngsx", InstrumentCategory.find.findByCode("liquid-handling-robot"), getScicloneNGSXAloneProperties(), 
				getInstruments(
						createInstrument("ngs1", "NGS 1",null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),  // FDS 29/08/2017 NGS-1 plus utilisé=> désactiver
						createInstrument("ngs2", "NGS 2",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("ngs3", "NGS 3",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),  // FDS 29/08/2017 ajout
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		//FDS ajout 04/10/2016 Epimotion (input plate / output tubes); 18/10/2017 ajout tube en entree
		l.add(newInstrumentUsedType("EpMotion", "epmotion", InstrumentCategory.find.findByCode("liquid-handling-robot"), getEpMotionProperties(), 
				getInstruments(
						createInstrument("epmotion1", "EpMotion 1",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), 
				getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		//FDS ajout 26/06/2017 Bravo WorkStation (input plate / output plate)
		// FDS 10/07/2017 inversion code/name
		// FDS 09/11/2017 ajout de properties...
		l.add(newInstrumentUsedType("Bravo WorkStation","bravo-workstation", InstrumentCategory.find.findByCode("liquid-handling-robot"), getBravoWsProperties(), 
				getInstruments(
						createInstrument("bravo-workstation1", "Bravo Workstation 1",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravo-workstation2", "Bravo Workstation 2",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravo-workstation3", "Bravo Workstation 3",null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		
		// FDS ajout 29/01/2016 JIRA NGL-894 pseudo instruments covaris+Sciclone (plaque input/plaque output) 
		// FDS 12/07/2017 Covaris E210 plus utilisé=> inactiver les 3 instruments mixtes
		l.add(newInstrumentUsedType("Covaris E210 + Sciclone NGSX", "covaris-e210-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris1-and-ngs1", "Covaris 1 / NGS 1", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris1-and-ngs2", "Covaris 1 / NGS 2", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris1-and-ngs3", "Covaris 1 / NGS 3", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // FDS 29/08/2017 ajout
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// 05/12/2016 SUPSQCNG-429 erreur label : LE220 et pas LE210
		l.add(newInstrumentUsedType("Covaris LE220 + Sciclone NGSX", "covaris-le220-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris2-and-ngs1", "Covaris 2 / NGS 1", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), // FDS 29/08/2017 NGS-1 plus utilisé=> désactiver
						createInstrument("covaris2-and-ngs2", "Covaris 2 / NGS 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris2-and-ngs3", "Covaris 2 / NGS 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // FDS 29/08/2017 ajout
				getContainerSupportCategories(new String[]{"96-well-plate"}), getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
		l.add(newInstrumentUsedType("Covaris E220 + Sciclone NGSX", "covaris-e220-and-sciclone-ngsx", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndScicloneNGSXProperties(), 
				getInstruments(
						createInstrument("covaris3-and-ngs1", "Covaris 3 / NGS 1", null, false, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), // FDS 29/08/2017 NGS-1 plus utilisé=> désactiver
						createInstrument("covaris3-and-ngs2", "Covaris 3 / NGS 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris3-and-ngs3", "Covaris 3 / NGS 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), // FDS 29/08/2017 ajout
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
		
		// 16/11/2017 NGL-1691 ajout "Covaris LE220 + Bravo Workstation" 
		// Covaris 2 ; ET Bravo Workstation 1, 2 et 3
		l.add(newInstrumentUsedType("Covaris LE220 + Bravo WS", "covaris-le220-and-bravows", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndBravoWsProperties(), 
				getInstruments(
						createInstrument("covaris2-and-bravows1", "Covaris 2 / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("covaris2-and-bravows2", "Covaris 2 / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris2-and-bravows3", "Covaris 2 / Bravo Workstation 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
		
		
		// 16/11/2017 NGL-1691 ajout "Covaris E220 + Bravo Workstation"
		// Covaris 3 ; ET Bravo Workstation 1, 2 et 3
		l.add(newInstrumentUsedType("Covaris E220 +  Bravo WS", "covaris-e220-and-bravows", InstrumentCategory.find.findByCode("covaris-and-liquid-handling-robot"), getCovarisAndBravoWsProperties(), 
				getInstruments(
						createInstrument("covaris3-and-bravows1", "Covaris 3 / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)), 
						createInstrument("covaris3-and-bravows2", "Covaris 3 / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("covaris3-and-bravows3", "Covaris 3 / Bravo Workstation 3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate" }), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
		
		
		// FDS ajout 22/03/2016 JIRA NGL-982 pseudo instruments Janus+Cbot  
		//  23/01/2017 les cbots ancien modele n'existent plus => desactiver ??? on ne peut plus faire de recherche !!!
		l.add(newInstrumentUsedType("Janus + cBot", "janus-and-cBot", InstrumentCategory.find.findByCode("liquid-handling-robot-and-cBot"), getJanusAndCBotProperties(), 
				getInstruments(
						createInstrument("janus1-and-cBot1", "Janus 1 / cBot1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot2", "Janus 1 / cBot2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot3", "Janus 1 / cBot3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBot4", "Janus 1 / cBot4", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 23/01/2017 ajout Janus + cBot-v2 
		l.add(newInstrumentUsedType("Janus + cBot-v2", "janus-and-cBotV2", InstrumentCategory.find.findByCode("liquid-handling-robot-and-cBot"), getJanusAndCBotV2Properties(), 
				getInstruments(
						createInstrument("janus1-and-cBotA", "Janus 1 / cBotA", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotB", "Janus 1 / cBotB", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotC", "Janus 1 / cBotC", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotD", "Janus 1 / cBotD", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotE", "Janus 1 / cBotE", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("janus1-and-cBotF", "Janus 1 / cBotF", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 29/07/2016 JIRA NGL-1027 pseudo instrument Masterycler EP-Gradient + Zephyr
		// 16/09/2016 un seul Zephyr pour l'instant donc=> Zephyr1; ne laisser que Mastercycler1 et Mastercycler2
		l.add(newInstrumentUsedType("Mastercycler EP-Gradient + Zephyr", "mastercycler-epg-and-zephyr", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getMastercyclerEPGAndZephyrProperties(), 
				getInstruments(
						createInstrument("mastercycler1-and-zephyr1", "Mastercycler 1 (EP-Gradient) / Zephyr 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler2-and-zephyr1", "Mastercycler 2 (EP-Gradient) / Zephyr 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 17/07/2017 NGL-1201 : pseudo instrument Masterycler EP-Gradient + Bravo
		// ep-gradient 1 ou 2 ou 4 + bravo 1 ou 2
		l.add(newInstrumentUsedType("Mastercycler EP-Gradient + Bravo Workstation", "mastercycler-epg-and-bravows", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getMastercyclerEPGAndBravoWsProperties(), 
				getInstruments(
						createInstrument("mastercycler1-and-bravows1", "Mastercycler 1 (EP-Gradient) / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler1-and-bravows2", "Mastercycler 1 (EP-Gradient) / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler2-and-bravows1", "Mastercycler 2 (EP-Gradient) / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler2-and-bravows2", "Mastercycler 2 (EP-Gradient) / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler4-and-bravows1", "Mastercycler 4 (EP-Gradient) / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler4-and-bravows2", "Mastercycler 4 (EP-Gradient) / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 17/07/2017 NGL-1201 : pseudo instrument Masterycler Nexus SX-1 + Bravo
		// Mastercycler Nexus 5 + bravo 1 ou 2
		l.add(newInstrumentUsedType("Mastercycler Nexus SX1 + Bravo Workstation", "mastercycler-nexus-and-bravows", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getMastercyclerNexusAndBravoWsProperties(), 
				getInstruments(
						createInstrument("mastercycler5-and-bravows1", "Mastercycler 5 (Nexus SX-1) / Bravo Workstation 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("mastercycler5-and-bravows2", "Mastercycler 5 (Nexus SX-1) / Bravo Workstation 2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 15/11/2017 NGL-1691 en fait il faut aussi un instrument  "hybride Bravo Workstation + Mastercycler EP-Gradient" (dans l'autre sens) avec des propriétés differentes ( juste celles du bravo seul)...
		// pour l'instant creer toutes les combinaisons quitte a en supprimer plus tard...
		l.add(newInstrumentUsedType("Bravo Workstation + Mastercycler EP-Gradient", "bravows-and-mastercycler-epg", InstrumentCategory.find.findByCode("thermocycler-and-liquid-handling-robot"), getBravoWsProperties(), 
				getInstruments(
						createInstrument("bravows1-and-mastercycler1", "Bravo Workstation 1 / Mastercycler 1 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows1-and-mastercycler2", "Bravo Workstation 1 / Mastercycler 2 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows1-and-mastercycler4", "Bravo Workstation 1 / Mastercycler 4 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						
						createInstrument("bravows2-and-mastercycler1", "Bravo Workstation 2 / Mastercycler 1 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows2-and-mastercycler2", "Bravo Workstation 2 / Mastercycler 2 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows2-and-mastercycler4", "Bravo Workstation 2 / Mastercycler 4 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						
						createInstrument("bravows3-and-mastercycler1", "Bravo Workstation 3 / Mastercycler 1 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows3-and-mastercycler2", "Bravo Workstation 3 / Mastercycler 2 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG)),
						createInstrument("bravows3-and-mastercycler4", "Bravo Workstation 3 / Mastercycler 4 (EP-Gradient)", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),

				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				getContainerSupportCategories(new String[]{"96-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		// FDS ajout 20/02/2017 NGL-1167 : Chromium controller ( entree tubes / sortie strip-8 )
		l.add(newInstrumentUsedType("Chromium controller", "chromium-controller", InstrumentCategory.find.findByCode("10x-genomics-instrument"), getChromiumControllerProperties(), 
				getInstruments(
						createInstrument("chromium1", "Chromium 1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.CNG))),
				getContainerSupportCategories(new String[]{"tube"}), 
				getContainerSupportCategories(new String[]{"strip-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		/** nanopore sequencers **/
		// FDS ajout 30/03/2017 : NGL-1225 ( Nanopore )
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
		
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getHiseqXProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// idem Hiseq4000 !!
		
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	// NGL-1730: ajout Novaseq
	private static List<PropertyDefinition> getNovaseq6000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// idem Hiseq4000, HiseqX !!
		
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
		
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, false, null,
										null, "single",null,true, null,null));

		return l;
	}
	
	private static List<PropertyDefinition> getMastercyclerNexusProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
        // 18/07/2017 correction !!! nbCycles => pcrCycleNumber		
		
		l.add(newPropertiesDefinition("Nbre Cycles PCR", "pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
                						null, "single",null,true, null,null));
		
		// FDS 29/11/2017 .manquait "Ratio billes","AdnBeadVolumeRatio"
		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
				null, null, null , null, "single", null, true ,null, null));
		return l;
	}
	
	// 18/07/2017 strictement les meme propriétés que Nexus ?? utile de faire 2 méthodes ??
	private static List<PropertyDefinition> getMastercyclerEPGradientProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
				
		l.add(newPropertiesDefinition("Nbre Cycles PCR", "pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
                						null, "single",null,true, null,null));
		
		// FDS 29/11/2017 ajout NGL-1717 mais etait manquant de toutes facons !!!
		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
				null, null, null , null, "single", null, true ,null, null));

		return l;
	}
	
	
	private static List<PropertyDefinition> getQuBitProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		// suppression de "Kit" demandée 29/11/2017 	
		
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
		
		//09/11/2017 Capture (valeurs reelles)
		progList.add("SureSelect XT initial SPRI cleanup");
		progList.add("SureSelect XT library prep");

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
		
		// 18/07/2017 aussi utilise en Fragmentation/capture !!!
		l.add(newPropertiesDefinition("Programme Covaris", "programCovaris", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
                						newValues("PCR FREE PROD NGS FINAL",
                								  "SureSelect96 final"),  
                						"single", null, false ,null, null));

		l.addAll(getScicloneNGSXProperties());
		
		// optionnel
		l.add(newPropertiesDefinition("Nom du Run","robotRunCode", LevelService.getLevels(Level.CODE.Instrument),  String.class, false, null,
										null, null, null, null, "single", null, true ,null, null));
		return l;
	}
	// FDS 16/11/2017 NLG-1691: ajout
	//  Programme Covaris (obligatoire) : SureSelect96 final (menu déroulant avec juste cette valeur)
	//  Programme Bravo WS : saisie libre NON obligatoire
	private static List<PropertyDefinition> getCovarisAndBravoWsProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		l.add(newPropertiesDefinition("Programme Covaris", "programCovaris", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
                						newValues( "SureSelect96 final"),  
                						"single", null, false ,null, null));
		
		l.add(newPropertiesDefinition("Programme Bravo WS","programBravoWs", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null,
				null, null, null , null, "single", null, true ,null, null));
		
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
				 						newValues("Clusterstripprepworklist"), 
				 						"single", 40, false ,null, null));
		
		l.add(newPropertiesDefinition("Strip #", "stripDestination", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
										newValues("1","2","3","4"), 
										"single", 50, true ,null, null));
		
        l.add(newPropertiesDefinition("Source", "source", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, "N",
        								Arrays.asList(newValue("1", "Source 1"), newValue("2", "Source 2"), newValue("3", "Source 3"),newValue("4", "Source 4")), 
        								"single", 2, true , null, null));
				
		l.addAll(getCBotProperties());
		
		return l;
	}
	 
	//FDS 23/01/2017 ajout Janus + cbot-v2
	//FDS 08/08/2017 NGL-1550 passage a 8 sources/6 strips pour le Janus
    //FDS 11/08/2017 attendre feu vert de la prod pour ajout nouveau programme....

	private static List<PropertyDefinition> getJanusAndCBotV2Properties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
			
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
				 						newValues("Clusterstripprepworklist"), "single", 40, false ,null, null));
		/* 
		         						newValues("Clusterstripprepworklist",
		         						"2_HiseqCluster_ClusterStripPrep_worklist_US_plaque"), "single", 40, false ,null, null));
		*/
		//FDS 04/08/2017: evolution du janus=> passer a 6 strips.
		l.add(newPropertiesDefinition("Strip #", "stripDestination", LevelService.getLevels(Level.CODE.Instrument), String.class, true, null,
										newValues("1","2","3","4","5","6"), "single", 50, true ,null, null));

		//FDS 04/08/2017: evolution du janus=> passer a 8 sources
	    l.add(newPropertiesDefinition("Source", "source", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, "N",
										 Arrays.asList(newValue("1", "Source 1"), 
												       newValue("2", "Source 2"), 
												       newValue("3", "Source 3"),
												       newValue("4", "Source 4"),
												       newValue("5", "Source 5"), 
												       newValue("6", "Source 6"),
												       newValue("7", "Source 7"),
												       newValue("8", "Source 8")), 
										"single", 2, true , null, null));
					
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
										newValues("1-48","49-96"), null, null , null, 
										"single", null, false ,null, null));
		
		return l;
	}
	
	
	// FDS 29/07/2016 JIRA NGL-1027 ajout propriétés pseudo instrument Masterycler EP-Gradient + Zephyr 
    //  09/11/2017 NGL-1691  suppression valeurs par defaut ( pcrCycleNumber et AdnBeadVolumeRatio )
	private static List<PropertyDefinition> getMastercyclerEPGAndZephyrProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		//Mastercycler EPG
		l.add(newPropertiesDefinition("Nbre Cycles PCR","pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, null,
										null, null, null , null, "single", null, true ,null, null));

		
		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
										null, null, null , null, "single", null, true ,null, null));
		//Zephyr
		
		return l;
	}
	
	// FDS 17/07/2017 NGL-1201  ajout propriétés pseudo instrument Mastercycler EP-Gradient + Bravo Workstation
	//     09/11/2017 NGL-1691  suppression valeurs par defaut ( pcrCycleNumber et AdnBeadVolumeRatio )
	private static List<PropertyDefinition> getMastercyclerEPGAndBravoWsProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		//Mastercycler EPG
		l.add(newPropertiesDefinition("Nbre Cycles PCR","pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, null,
										null, null, null , null, "single", null, true ,null, null));

		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
										null, null, null , null, "single", null, true ,null, null));
		// Bravo 
		// FDS 09/11/2017 NGL-1691: ajout propriété "Programme Bravo WS" en saisie libre non obligatoire
		l.add(newPropertiesDefinition("Programme Bravo WS","programBravoWs", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null,
										null, null, null , null, "single", null, true ,null, null));
		
		return l;
	}
	
	// FDS 17/07/2017 NGL-1201 Mastercycler Nexus SX1 + Bravo Workstation
    //     09/11/2017 NGL-1691  suppression valeurs par defaut ( pcrCycleNumber et AdnBeadVolumeRatio )
	private static List<PropertyDefinition> getMastercyclerNexusAndBravoWsProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		//Mastercycler Nexus
		l.add(newPropertiesDefinition("Nbre Cycles PCR","pcrCycleNumber", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, null,
										null, null, null , null, "single", null, true ,null, null));

		l.add(newPropertiesDefinition("Ratio billes","AdnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Instrument),Double.class, true, null,
										null, null, null , null, "single", null, true ,null, null));
		
		// Bravo
		// FDS 09/11/2017 NGL-1691: ajout propriété "Programme Bravo WS" en saisie libre non obligatoire
		l.add(newPropertiesDefinition("Programme Bravo WS","programBravoWs", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null,
										null, null, null , null, "single", null, true ,null, null));
		return l;
	}
	
	// FDS 09/11/2017 ajout pour NGL-1691 dans le cas ou instrument utilisé seul
	private static List<PropertyDefinition>getBravoWsProperties()throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		// propriété "Programme Bravo WS" en saisie libre, non obligatoire
		l.add(newPropertiesDefinition("Programme Bravo WS","programBravoWs", LevelService.getLevels(Level.CODE.Instrument),String.class, false, null,
										null, null, null , null, "single", null, true ,null, null));
		
		return l;
	}
	
	//FDS 20/02/2017 NGL-1167: Chromium controller
	private static List<PropertyDefinition> getChromiumControllerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		
		//06/03/2017 chipPosition est une propriete d'instrument et pas d'experience...
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

	// 06/12/2017 FDS : ne sont plus actifs=> booleen a false, pas suffisant pour les désactiver...
	public static List<Instrument> getInstrumentHiseq2000() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("HISEQ1", "HISEQ1", null, false, "/env/ig/atelier/illumina/cng/HISEQ1/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ2", "HISEQ2", null, false, "/env/ig/atelier/illumina/cng/HISEQ2/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ3", "HISEQ3", null, false, "/env/ig/atelier/illumina/cng/HISEQ3/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ4", "HISEQ4", null, false, "/env/ig/atelier/illumina/cng/HISEQ4/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ5", "HISEQ5", null, false, "/env/ig/atelier/illumina/cng/HISEQ5/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ6", "HISEQ6", null, false, "/env/ig/atelier/illumina/cng/HISEQ6/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ7", "HISEQ7", null, false, "/env/ig/atelier/illumina/cng/HISEQ7/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ8", "HISEQ8", null, false, "/env/ig/atelier/illumina/cng/HISEQ8/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)) );
		return instruments;
	}
	
	public static List<Instrument> getInstrumentHiseq2500() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("HISEQ9",  "HISEQ9",  "H1", true, "/env/ig/atelier/illumina/cng/HISEQ9/",  DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ10", "HISEQ10", "H2", true, "/env/ig/atelier/illumina/cng/HISEQ10/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("HISEQ11", "HISEQ11", "H3", true, "/env/ig/atelier/illumina/cng/HISEQ11/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		return instruments;
	}
	
	// FDS ajout 30/03/2017 NGL-1225 (Nanopore)
	private static List<Instrument> getInstrumentMKIB() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add(createInstrument("MN18834", "MN18834", null, true, "/env/ig/atelier/nanopore/cng/MN18834", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		instruments.add(createInstrument("MN19213", "MN19213", null, true, "/env/ig/atelier/nanopore/cng/MN19213", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19240", "MN19240", null, true, "/env/ig/atelier/nanopore/cng/MN19240", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19270", "MN19270", null, true, "/env/ig/atelier/nanopore/cng/MN19270", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19813", "MN19813", null, true, "/env/ig/atelier/nanopore/cng/MN19813", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19802", "MN19802", null, true, "/env/ig/atelier/nanopore/cng/MN19802", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add(createInstrument("MN19190", "MN19190", null, true, "/env/ig/atelier/nanopore/cng/MN19190", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		return instruments;
	}
	
	// FDS ajout 06/12/2017 NGL-1730 (Novaseq6000) + SUPSQCNG-506 (EXTNOVASEQ)
	private static List<Instrument> getInstrumentNovaseq6000() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		
		instruments.add( createInstrument("MARIECURIX", "MARIECURIX", "V1", true, "/env/ig/atelier/illumina/cng/MARIECURIX/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		instruments.add( createInstrument("EXTNOVASEQ", "EXTNOVASEQ", null, true, "/env/ig/atelier/illumina/cng/EXTNOVASEQ/", DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		return instruments;
	}
}