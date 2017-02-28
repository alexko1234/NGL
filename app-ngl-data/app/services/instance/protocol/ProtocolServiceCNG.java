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
import services.instance.InstanceFactory;
import validation.ContextValidation;

public class ProtocolServiceCNG {	

	private final static String institute = "CNG";
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
		
		//----------Experiences de transformation-------------------------------------
		lp.add(newProtocol("PrepFC_CBot_ptr_sox139_1","PrepFC_CBot_ptr_sox139_1","","1","production", 
				InstanceFactory.setExperimentTypeCodes("prepa-flowcell")));
		
		// 27/06/2016 ajout "protocole_FC_ordonnée"
		lp.add(newProtocol("protocole-FC-ordered","protocole_FC_ordonnée","","1","production", 
				InstanceFactory.setExperimentTypeCodes("prepa-fc-ordered")));
		
		lp.add(newProtocol("1a-sop-ill-pcrfree","1A_SOP_ILL_PCRfree_270116", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-pcr-free",
													   "labchip-migration-profile" )));
		
		lp.add(newProtocol("1a-sop-ill-pcrfree-dap-plate","1A_SOP_ILL_PCRfree_DAPplate", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-pcr-free",
													   "labchip-migration-profile" )));


		// 10/08/2016 protocole  pour toutes les experiences du processus X5_WG NANO
		// 01/09/2016 aussi pour "labchip-migration-profile"
		// 27/09/2016 prepa-fc-ordered, normalisation, illumina-depot ont leur propre protocole
		lp.add(newProtocol("1a-sop-ill-nano-240214","1A_SOP_ILL_NANO_240214", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-wg-nano",
													   "pcr-and-purification",
													   "labchip-migration-profile")));
		
		lp.add(newProtocol("sop-1","SOP 1","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("denat-dil-lib")));
		
		lp.add(newProtocol("sop-en-attente","SOP en attente","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("normalization-and-pooling",														   
													   "aliquoting",
													   "tubes-to-plate",
													   "plate-to-tubes",
													   "plates-to-plate",
													   "x-to-plate")));

		// 12/12/2016 protocoles pour RNA
		lp.add(newProtocol("2a-ill-ssmrna-010616","2A_ILL_ssmRNA_010616","?","1","production", 
				InstanceFactory.setExperimentTypeCodes( "library-prep",
							                            "pcr-and-purification")));
			
		// 05/12/2016 library-prep	toujours en DEV
		lp.add(newProtocol("2a-ill-sstotalrna-170816","2A_ILL_ssTotalRNA_170816","?","1","production", 
				InstanceFactory.setExperimentTypeCodes( "library-prep",
													    "pcr-and-purification")));						

		// 26/09/2016 ajout protocole "normalisation" dédié a l'experience lib-normalization"
		lp.add(newProtocol("normalization","normalisation","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("lib-normalization")));
		
		// 27/09/2016 ajout protocole "protocole_pool" dédié a l'experience "pool plaque a plaque"
		lp.add(newProtocol("protocol-pool","protocole_pool","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("pool")));
		
		// 27/09/2016 ajout protocole "protocole_dépôt_illumina" dédié a l'experience "illumina-depot"
		lp.add(newProtocol("protocol-illumina-depot","protocole_dépôt_illumina","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("illumina-depot")));
		
		// 24/02/2017 ajout protocole pour Chromium
		lp.add(newProtocol("chromium-genome-protocol","chromium genome protocol","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("chromium-gem-generation",
													   "wg-chromium-lib-prep")));
		
		
		//------------Experiences de Control Qualité------------------------------
		lp.add(newProtocol("7-sop-miseq","7_SOP_Miseq","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("miseq-qc")));
		
		lp.add(newProtocol("3a-kapa-qPCR-240715","3A_KAPA_qPCR_240715", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("qpcr-quantification")));
		
		// 01/09/2016 ajout 
		lp.add(newProtocol("labchip-gx","LabChiP_GX", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("labchip-migration-profile")));
		
		// 27/02/2017 ajout protocole pour Bioanalyzer ???????
		lp.add(newProtocol("bioanalyzer","BioAnalyzer", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("bioanalyzer-migration-profile")));
		
		for(Protocol protocole:lp){
			InstanceHelpers.save(InstanceConstants.PROTOCOL_COLL_NAME, protocole,ctx);
			Logger.debug("protocol '"+protocole.name + "' saved..." );
		}
	}
	
}