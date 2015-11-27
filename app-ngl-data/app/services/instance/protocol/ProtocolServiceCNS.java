package services.instance.protocol;

import static services.instance.InstanceFactory.newProtocol;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;

import com.typesafe.config.ConfigFactory;

import fr.cea.ig.MongoDBDAO;
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
		
		
		lp.add(newProtocol("map005","MAP005","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("map005-on-beads"," MAP005 sur billes","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library")));
		lp.add(newProtocol("map006-low-input","MAP006 low input","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library","nanopore-fragmentation")));
		
		lp.add(newProtocol("map005-depot","MAP005_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		lp.add(newProtocol("map005-on-bead-depot","MAP005 sur billes_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		lp.add(newProtocol("map006-depot","MAP006_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		lp.add(newProtocol("map006-low-input-depot","MAP006 low input_dépôt","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-depot")));
		
		lp.add(newProtocol("map005-preCR","MAP005 preCR","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		lp.add(newProtocol("map006-preCR","MAP006 preCR","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));
		lp.add(newProtocol("map006-FFPE","MAP006 FFPE","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-fragmentation")));

		lp.add(newProtocol("map006","MAP006","path7","1","production", InstanceFactory.setExperimentTypeCodes("nanopore-library","nanopore-fragmentation")));
		
		lp.add(newProtocol("prt_wait","Proto_en_attente","path1","1","production", InstanceFactory.setExperimentTypeCodes("aliquoting")));
		lp.add(newProtocol("ptr_pool_tube_v1","PTR_POOL_TUBE_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("pool-tube")));
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			lp.add(newProtocol("irys-prep-nlrs-300-900","Irys Prep Labelling NLRS (300/900)","path7","1","production", InstanceFactory.setExperimentTypeCodes("irys-nlrs-prep","irys-chip-preparation")));
			lp.add(newProtocol("bionano_standard_ptr","ptr_standard","path7","1","production", InstanceFactory.setExperimentTypeCodes("bionano-depot")));
			lp.add(newProtocol("bionano_customized_ptr","ptr_modifié","path7","1","production", InstanceFactory.setExperimentTypeCodes("bionano-depot")));
			
			lp.add(newProtocol("fragmentation_ptr_sox140_1","Fragmentation_ptr_sox140_1","path1","1","production", InstanceFactory.setExperimentTypeCodes("fragmentation")));
			//lp.add(newProtocol("bqspri_ptr_sox142_1","BqSPRI_ptr_sox142_1","path2","1","production", InstanceFactory.setExperimentTypeCodes("librairie-indexing", "librairie-dualindexing")));
			lp.add(newProtocol("amplif_ptr_sox144_1","Amplif_ptr_sox144_1","path3","1","production", InstanceFactory.setExperimentTypeCodes("amplification", "solution-stock")));
			lp.add(newProtocol("proto_qc_v1","Proto_QC_v1","path7","1","production", InstanceFactory.setExperimentTypeCodes("chip-migration-post-pcr", "chip-migration-pre-pcr", "fluo-quantification"/*, "qpcr-quantification"*/)));

			
		}
		
		for(Protocol protocole:lp){
			InstanceHelpers.save(InstanceConstants.PROTOCOL_COLL_NAME, protocole,ctx);
			Logger.debug(" Protocole "+protocole.code);
		}
	}
	
	





}
