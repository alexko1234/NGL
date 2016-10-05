package services.instance.protocol;

import static services.instance.InstanceFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import com.google.common.collect.Maps;
import com.typesafe.config.ConfigFactory;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.Logger;
import scala.collection.script.Remove;
import services.instance.InstanceFactory;
import validation.ContextValidation;

public class ProtocolServiceCNS {	

	private final static String institute = "CNS";

	public static void main(ContextValidation ctx) {	

		Logger.info("Start to create protocols collection for "+institute+"...");
		Logger.info("Remove protocol");
		removeProtocols(ctx);
		Logger.info("Save protocols ...");
		saveProtocols(ctx);
		Logger.info(institute+" Protocols collection creation is done!");
	}
	
	
	private static void removeProtocols(ContextValidation ctx) {
		MongoDBDAO.delete(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.empty());
	}


	public static void saveProtocols(ContextValidation ctx){		
		List<Protocol> lp = new ArrayList<Protocol>();
		
		lp.add(newProtocol("depot_opgen_ptr_1","Depot_Opgen_prt_1","path7","1","production", InstanceFactory.setExperimentTypeCodes("opgen-depot")));
		
		lp.add(newProtocol("hiseq2000_Illumina","HiSeq2000_Illumina","path4","1","production", InstanceFactory.setExperimentTypeCodes("illumina-depot")));
		lp.add(newProtocol("hiseq2500_fast_Illumina","HiSeq2500_fast_Illumina","path5","1","production", InstanceFactory.setExperimentTypeCodes("illumina-depot")));
		lp.add(newProtocol("hiseq2500_Illumina","HiSeq2500_Illumina","path6","1","production", InstanceFactory.setExperimentTypeCodes("illumina-depot")));
		lp.add(newProtocol("ptr_sox147_v1_depot","PTR_SOX147_v1","path6","1","production", InstanceFactory.setExperimentTypeCodes("illumina-depot")));
		
		lp.add(newProtocol("prepfc_cbot_ptr_sox139_1","PrepFC_CBot_ptr_sox139_1","path7","1","production", InstanceFactory.setExperimentTypeCodes("prepa-flowcell")));
		lp.add(newProtocol("cbot_rapid_run_2500_Illumina","CBot_rapid_run_2500_Illumina","path7","1","production", InstanceFactory.setExperimentTypeCodes("prepa-flowcell")));
		lp.add(newProtocol("ptr_sox147_v1","PTR_SOX147_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("prepa-flowcell")));
		
		lp.add(newProtocol("hiseq_4000_system_guide","Illumina Hiseq 4000 system guide","path8","1","production", InstanceFactory.setExperimentTypeCodes("prepa-fc-ordered", "illumina-depot")));
		
		
		//To disable
			
		//Nanopore
		lp.add(newProtocol("R9-Long-Read","R9-Long Read","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-1D-ligation","R9-1D ligation","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-1D-transposition","R9-1D transposition","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-Long-Read 1D","R9-Long Read 1D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-Long-Read 2D","R9-Long Read 2D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-Low-input","R9-Low input","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-1D","R9-1D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-2D","R9-2D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("R9-depot","R9-dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		lp.add(newProtocol("R9-depot-SpotON","R9-dépôt-SpotON","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		
		
		//To disable
		lp.add(newProtocol("map005","MAP005","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library"),false));
		lp.add(newProtocol("map005-on-beads","MAP005 sur billes","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library"),false));
		lp.add(newProtocol("map006-low-input","MAP006 low input","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library"),false));
		lp.add(newProtocol("map006","MAP006","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library"),false));
	
		
		lp.add(newProtocol("map005-depot","MAP005_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot"),false));
		lp.add(newProtocol("map005-on-bead-depot","MAP005 sur billes_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot"),false));
		lp.add(newProtocol("map006-depot","MAP006_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot"),false));
		lp.add(newProtocol("map006-low-input-depot","MAP006 low input_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot"),false));
		lp.add(newProtocol("R9-1D-depot","R9-1D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library","nanopore-depot"),false));
		lp.add(newProtocol("R9-2D-depot","R9-2D","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library","nanopore-depot"),false));
		
		lp.add(newProtocol("mechanical-fragmentation","fragmentation mécanique","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		lp.add(newProtocol("enzymatic-fragmentation","fragmentation enzymatique","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		//lp.add(newProtocol("map005-preCR","MAP005 preCR","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		//lp.add(newProtocol("map006-preCR","MAP006 preCR","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		//lp.add(newProtocol("map006-FFPE","MAP006 FFPE","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		
		lp.add(newProtocol("prt_wait","Proto_en_attente","path1","1","production", InstanceFactory.setExperimentTypeCodes("aliquoting")));
		
		
		lp.add(newProtocol("irys-prep-nlrs-300-900","Irys Prep Labelling NLRS (300/900)","path7","1","production", InstanceFactory.setExperimentTypeCodes("irys-nlrs-prep","irys-chip-preparation")));
		lp.add(newProtocol("bionano_standard_ptr","ptr_standard","path7","1","production", InstanceFactory.setExperimentTypeCodes("bionano-depot")));
		lp.add(newProtocol("optimization","optimisation","path7","1","production", InstanceFactory.setExperimentTypeCodes("bionano-depot")));
		
		//lp.add(newProtocol("fragmentation_ptr_sox140_1","Fragmentation_ptr_sox140_1","path1","1","production", InstanceFactory.setExperimentTypeCodes("fragmentation")));
		//lp.add(newProtocol("bqspri_ptr_sox142_1","BqSPRI_ptr_sox142_1","path2","1","production", InstanceFactory.setExperimentTypeCodes("librairie-indexing", "librairie-dualindexing")));
		//lp.add(newProtocol("amplif_ptr_sox144_1","Amplif_ptr_sox144_1","path3","1","production", InstanceFactory.setExperimentTypeCodes("amplification", "solution-stock")));
		//lp.add(newProtocol("proto_qc_v1","Proto_QC_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("chip-migration-post-pcr", "chip-migration-pre-pcr", "fluo-quantification", "qpcr-quantification")));

		lp.add(newProtocol("zr-duet-extraction","Extraction ZR Duet","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-rna-extraction")));
		lp.add(newProtocol("cryogenic-grinding","Cryobroyage","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-rna-extraction")));
		lp.add(newProtocol("fast_prep_grinding","Broyage Fast Prep","path2","1","production",InstanceFactory.setExperimentTypeCodes("grinding")));
		
		lp.add(newProtocol("dnase-treatment","Traitement à la Dnase","path2","1","production",InstanceFactory.setExperimentTypeCodes("dnase-treatment")));

		lp.add(newProtocol("fluo-dosage","dosage_fluo","path2","1","production",InstanceFactory.setExperimentTypeCodes("fluo-quantification")));
		lp.add(newProtocol("ptr-ctl-123-4","PTR_CTL123_4","path2","1","production",InstanceFactory.setExperimentTypeCodes("gel-migration")));		
		lp.add(newProtocol("proto_qc_v1","Proto_QC_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("qpcr-quantification")));
		
		
		
		lp.add(newProtocol("ptr_pool_tube_v1","PTR_POOL_TUBE_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("pool-tube","pool")));

		
		lp.add(newProtocol("amplif_ptr_sox144_1","Amplif_ptr_sox144_1","path3","1","production", InstanceFactory.setExperimentTypeCodes("solution-stock")));

		lp.add(newProtocol("Tag18S_V9","Tag18S V9","path2","1","production",InstanceFactory.setExperimentTypeCodes("tag-pcr")));
		lp.add(newProtocol("Tag16S_V4V5_Fuhrmann","Tag16S V4V5 Fuhrmann","path2","1","production",InstanceFactory.setExperimentTypeCodes("tag-pcr")));
		lp.add(newProtocol("Tag16S_Full_Length_16S_V4V5_Fuhrman","Tag 16S_Full Length + 16S_V4V5_Fuhrman","path2","1","production",InstanceFactory.setExperimentTypeCodes("tag-pcr")));
		
		
		
		lp.add(newProtocol("amplif_ptr_sox_144-4","Amplif ptr Sox 144-4","path2","1","production",InstanceFactory.setExperimentTypeCodes("pcr-amplification-and-purification")));
		lp.add(newProtocol("amplif_nebnext_ultraii_ptr_151_1","Amplif_NebNext_UltraII ptr 151_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("pcr-amplification-and-purification")));
		
		
		
		lp.add(newProtocol("Amplif_ptr_Sox_144-4newProtocol","Amplif ptr Sox 144-4newProtocol","path2","1","production",InstanceFactory.setExperimentTypeCodes("sizing")));
		lp.add(newProtocol("Decoupe_sur_gel","Découpe sur gel","path2","1","production",InstanceFactory.setExperimentTypeCodes("sizing")));
		lp.add(newProtocol("Spri_select","Spri select","path2","1","production",InstanceFactory.setExperimentTypeCodes("sizing")));

		
		lp.add(newProtocol("Bq_Super_low_cost_ptr_150_1","Bq_Super_low cost_ptr 150_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library","fragmentation")));
		lp.add(newProtocol("Bq_Low cost_ptr_148_3","Bq_Low cost_ptr_148_3","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library","fragmentation")));
		lp.add(newProtocol("Bq_NEB_Next_Ultra_II_ptr_151_1","Bq_NEB Next Ultra II ptr_151_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library","fragmentation")));
		
		lp.add(newProtocol("smarter_v4","Smarter V4_ptr_sox156_1","path1","1","production", InstanceFactory.setExperimentTypeCodes("cdna-synthesis"), 
				concatMap(newPSV("rnaLibProtocol","Smarter V4"),newPSV("strandOrientation","unstranded"),newPSV("cDNAsynthesisType","oligodT"))));
    	
		lp.add(newProtocol("ovation_rnaseq_system_v2","Ovation RNAseq system v2","path1","1","production", InstanceFactory.setExperimentTypeCodes("cdna-synthesis"), 
				concatMap(newPSV("rnaLibProtocol","Ovation RNAseq system v2"),newPSV("strandOrientation","unstranded"),newPSV("cDNAsynthesisType","random + oligodT"))));
    	
		
		lp.add(newProtocol("truseq_stranded_poly_a","TruSeq Stranded poly A_ptr_sox153_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("rna-illumina-indexed-library"), 
				concatMap(newPSV("rnaLibProtocol","TruSeq Stranded poly A"),newPSV("strandOrientation","reverse"),newPSV("cDNAsynthesisType","random"))));
		
		lp.add(newProtocol("truseq_stranded_proc","TruSeq Stranded_proc_ptr_sox154_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("rna-illumina-indexed-library"), 
				concatMap(newPSV("rnaLibProtocol","TruSeq Stranded Proc"),newPSV("strandOrientation","reverse"),newPSV("cDNAsynthesisType","random"))));
		
		lp.add(newProtocol("smarter_stranded","Smarter Stranded_ptr_sox155_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("rna-illumina-indexed-library","pcr-amplification-and-purification"), 
				concatMap(newPSV("rnaLibProtocol","Smarter Stranded"),newPSV("strandOrientation","forward"),newPSV("cDNAsynthesisType","random"))));
		/*
		lp.add(newProtocol("indac","Indac","path2","1","production",InstanceFactory.setExperimentTypeCodes("rna-illumina-indexed-library"), 
				concatMap(newPSV("rnaLibProtocol","indac"),newPSV("strandOrientation","reverse"),newPSV("cDNAsynthesisType","?"))));
		*/
		
		lp.add(newProtocol("bacteria-rrna-depletion","Déplétion bactérienne","path2","1","production",InstanceFactory.setExperimentTypeCodes("rrna-depletion"), 
				newPSV("depletionMethod","bactérienne")));
		
		lp.add(newProtocol("plant-rrna-depletion","Déplétion plante","path2","1","production",InstanceFactory.setExperimentTypeCodes("rrna-depletion"), 
				newPSV("depletionMethod","plante")));
		
		
		lp.add(newProtocol("prt_wait_2","Proto_en_attente","path1","1","production", 
				InstanceFactory.setExperimentTypeCodes("chip-migration","chip-migration-rna-evaluation","control-pcr-and-gel","normalisation","tubes-to-plate","plate-to-tubes","plates-to-plate","x-to-plate")));

		lp.add(newProtocol("Ampure_post_pcr","ampure_post_pcr","path2","1","production",InstanceFactory.setExperimentTypeCodes("post-pcr-ampure")));

		if(ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			//lp.add(newProtocol("Bq_Super_low_cost_ptr_150_1","Bq_Super_low cost_ptr 150_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library")));
			//lp.add(newProtocol("Bq_Low cost_ptr_148_3","Bq_Low cost_ptr_148_3","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library")));
			//lp.add(newProtocol("Bq_NEB_Next_Ultra_II_ptr_151_1","Bq_NEB Next Ultra II ptr_151_1","path2","1","production",InstanceFactory.setExperimentTypeCodes("dna-illumina-indexed-library")));
					
		}else if(ConfigFactory.load().getString("ngl.env").equals("DEV") ){			
			// a mettre en PROD		
			//fin mise en prod
			
        	//lp.add(newProtocol("prt_wait_dev","Proto_en_attente","path1","1","production", InstanceFactory.setExperimentTypeCodes()));
			
		}else if(ConfigFactory.load().getString("ngl.env").equals("UAT") ){	
			//lp.add(newProtocol("prt_wait_uat","Proto_en_attente","path1","1","production", null));
		}
		
		for(Protocol protocole:lp){
			InstanceHelpers.save(InstanceConstants.PROTOCOL_COLL_NAME, protocole,ctx);
			Logger.debug(" Protocole "+protocole.code);
		}
	}

/*
protocole	Smarter V4	Ovation RNAseq system v2	TruSeq Stranded poly A	TruSeq Stranded Proc	Smarter Stranded	Indac
rnaLibProtocol	smarterV4	ovationRNAseqSystemV2	truseqStrandedPolyA	truseqStrandedProk	smarterStranded	indac
strandOrientation	?	?	reverse	reverse	forward	reverse
cDNAsynthesisType	?	?	?	?	?	?
 */
	@SafeVarargs
	private static Map<String, PropertyValue> concatMap(
			Map<String, PropertyValue>...map) {
		Map<String, PropertyValue> mapFinal = new HashMap<String, PropertyValue>(map.length);
		for(int i = 0 ; i < map.length; i++){
			mapFinal.putAll(map[i]);
		}
		return mapFinal;
	}
	
	





}
