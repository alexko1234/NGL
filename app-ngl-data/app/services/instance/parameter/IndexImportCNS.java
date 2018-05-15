package services.instance.parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import models.Constants;
import models.LimsCNSDAO;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.parameter.Parameter;
import models.laboratory.parameter.index.IlluminaIndex;
import models.laboratory.parameter.index.Index;
import models.laboratory.parameter.index.NanoporeIndex;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;

public class IndexImportCNS extends AbstractImportDataCNS{

	@Inject
	public IndexImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("IndexImportCNS",durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndexIllumina(limsServices,contextError);
		createIndexNanopore(contextError);
		createIndexChromium(contextError);
		createIndexNEBNext(contextError);
		createIndexCustom(contextError);
	}

	
	

	public static void createIndexIllumina(LimsCNSDAO limsServices,ContextValidation contextValidation) throws SQLException, DAOException{
		
	List<Index> indexs = limsServices.findIndexIlluminaToCreate(contextValidation) ;
		
		for(Index index:indexs){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code);
			}
		}
	
		InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,indexs,contextValidation);
		
	}
	
	public static void createIndexNanopore(ContextValidation contextValidation) {

		for (int i = 1; i <= 12; i++) {
			Index index = getNanoporeIndex(i);
			if (!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class,
					index.code)) {
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME, index, contextValidation);
			}
		}

	}

	private static Index getNanoporeIndex(int i) {
		Index index = new NanoporeIndex();
		String code = (i < 10)?"NB0"+i:"NB"+i;
		index.code = code;
		index.name = code;
		index.shortName = code;
		index.sequence = code;
		index.categoryCode = "SINGLE-INDEX";
		index.supplierName = new HashMap<>();
		index.supplierName.put("oxfordNanopore", code);
		index.traceInformation=new TraceInformation(Constants.NGL_DATA_USER);
		return index;
	}
	
	public static void createIndexChromium(ContextValidation contextValidation) throws DAOException{
		
		IndexImportUtils.getChromiumIndex().forEach((k,v)-> {
			Index index = getChromiumIndex(k,  v);
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				//Logger.info("creation index : "+ index.code +" / "+ index.categoryCode);
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
			} else {
				//Logger.info("index : "+ index.code + " already exists !!");
			}
		});			
		
	}

	private static Index getChromiumIndex(String code, String seq) {
		Index index = new IlluminaIndex();
		
		index.code = code;
		index.name = code;
		index.shortName = code;
		index.sequence = seq ;  //Voir plus tard: il y a 4 sequences pour les POOL-INDEX...Chromium
		index.categoryCode = "POOL-INDEX";
		index.supplierName = new HashMap<>();
		index.supplierName.put("10x Genomics", code);
		index.traceInformation=new TraceInformation(Constants.NGL_DATA_USER);
		
		return index;
	}
	
	
	private void createIndexNEBNext(ContextValidation contextValidation) {
		List<Index> indexes = new ArrayList<>();
		
		indexes.add(getNEBNextIndex("NEBNext1", "ATCACG", "IND1"));
		indexes.add(getNEBNextIndex("NEBNext2", "CGATGT", "IND2"));
		indexes.add(getNEBNextIndex("NEBNext3", "TTAGGC", "IND3"));
		indexes.add(getNEBNextIndex("NEBNext4", "TGACCA", "IND4"));
		indexes.add(getNEBNextIndex("NEBNext5", "ACAGTG", "IND5"));
		indexes.add(getNEBNextIndex("NEBNext6", "GCCAAT", "IND6"));
		indexes.add(getNEBNextIndex("NEBNext7", "CAGATC", "IND7"));
		indexes.add(getNEBNextIndex("NEBNext8", "ACTTGA", "IND8"));
		indexes.add(getNEBNextIndex("NEBNext9", "GATCAG", "IND9"));
		indexes.add(getNEBNextIndex("NEBNext10", "TAGCTT", "IND10"));
		indexes.add(getNEBNextIndex("NEBNext11", "GGCTAC", "IND11"));
		indexes.add(getNEBNextIndex("NEBNext12", "CTTGTA", "IND12"));
		indexes.add(getNEBNextIndex("NEBNext13", "AGTCAA", "IND13"));
		indexes.add(getNEBNextIndex("NEBNext14", "AGTTCC", "IND14"));
		indexes.add(getNEBNextIndex("NEBNext15", "ATGTCA", "IND15"));
		indexes.add(getNEBNextIndex("NEBNext16", "CCGTCC", "IND16"));
		indexes.add(getNEBNextIndex("NEBNext17", "GTAGAG", "IND17"));
		indexes.add(getNEBNextIndex("NEBNext18", "GTCCGC", "IND18"));
		indexes.add(getNEBNextIndex("NEBNext19", "GTGAAA", "IND19"));
		indexes.add(getNEBNextIndex("NEBNext20", "GTGGCC", "IND20"));
		indexes.add(getNEBNextIndex("NEBNext21", "GTTTCG", "IND21"));
		indexes.add(getNEBNextIndex("NEBNext22", "CGTACG", "IND22"));
		indexes.add(getNEBNextIndex("NEBNext23", "GAGTGG", "IND23"));
		indexes.add(getNEBNextIndex("NEBNext24", "GGTAGC", "IND24"));
		indexes.add(getNEBNextIndex("NEBNext25", "ACTGAT", "IND25"));
		indexes.add(getNEBNextIndex("NEBNext26", "ATGAGC", "IND26"));
		indexes.add(getNEBNextIndex("NEBNext27", "ATTCCT", "IND27"));
		indexes.add(getNEBNextIndex("NEBNext28", "CAAAAG", "IND28"));
		indexes.add(getNEBNextIndex("NEBNext29", "CAACTA", "IND29"));
		indexes.add(getNEBNextIndex("NEBNext30", "CACCGG", "IND30"));
		indexes.add(getNEBNextIndex("NEBNext31", "CACGAT", "IND31"));
		indexes.add(getNEBNextIndex("NEBNext32", "CACTCA", "IND32"));
		indexes.add(getNEBNextIndex("NEBNext33", "CAGGCG", "IND33"));
		indexes.add(getNEBNextIndex("NEBNext34", "CATGGC", "IND34"));
		indexes.add(getNEBNextIndex("NEBNext35", "CATTTT", "IND35"));
		indexes.add(getNEBNextIndex("NEBNext36", "CCAACA", "IND36"));
		indexes.add(getNEBNextIndex("NEBNext37", "CGGAAT", "IND37"));
		indexes.add(getNEBNextIndex("NEBNext38", "CTAGCT", "IND38"));
		indexes.add(getNEBNextIndex("NEBNext39", "CTATAC", "IND39"));
		indexes.add(getNEBNextIndex("NEBNext40b", "CTCAGA", "IND40b"));
		indexes.add(getNEBNextIndex("NEBNext42", "TAATCG", "IND42"));
		indexes.add(getNEBNextIndex("NEBNext43", "TACAGC", "IND43"));
		indexes.add(getNEBNextIndex("NEBNext44", "TATAAT", "IND44"));
		indexes.add(getNEBNextIndex("NEBNext45", "TCATTC", "IND45"));
		indexes.add(getNEBNextIndex("NEBNext46", "TCCCGA", "IND46"));
		indexes.add(getNEBNextIndex("NEBNext47", "TCGAAG", "IND47"));
		indexes.add(getNEBNextIndex("NEBNext48", "TCGGCA", "IND48"));

		indexes.forEach(index-> {
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				//Logger.info("creation index : "+ index.code +" / "+ index.categoryCode);
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
			} else {
				//Logger.info("index : "+ index.code + " already exists !!");
			}
		});			
	}
	
	private static Index getNEBNextIndex(String code, String seq, String shortName) {
		Index index = new IlluminaIndex();
		
		index.code = code;
		index.name = code;
		index.shortName = shortName;
		index.sequence = seq ; 
		index.categoryCode = "SINGLE-INDEX";
		index.supplierName = new HashMap<>();
		index.supplierName.put("NEB", code);
		index.traceInformation=new TraceInformation(Constants.NGL_DATA_USER);
		
		return index;
	}
	private void createIndexCustom(ContextValidation contextValidation) {
		List<Index> indexes = new ArrayList<>();
		
		indexes.add(getCustomIndex("EXT001","AAACAA","EXT001"));
		indexes.add(getCustomIndex("EXT002","ACATAC","EXT002"));
		indexes.add(getCustomIndex("EXT003","ACCATC","EXT003"));
		indexes.add(getCustomIndex("EXT004","ACGCAT","EXT004"));
		indexes.add(getCustomIndex("EXT005","ACTGCC","EXT005"));
		indexes.add(getCustomIndex("EXT006","AGATCG","EXT006"));
		indexes.add(getCustomIndex("EXT007","AGGGGA","EXT007"));
		indexes.add(getCustomIndex("EXT008","ATACCT","EXT008"));
		indexes.add(getCustomIndex("EXT009","ATGGTT","EXT009"));
		indexes.add(getCustomIndex("EXT010","ATTAAA","EXT010"));
		indexes.add(getCustomIndex("EXT011","ATTCTC","EXT011"));
		indexes.add(getCustomIndex("EXT012","CAAAAT","EXT012"));
		indexes.add(getCustomIndex("EXT013","CAACTG","EXT013"));
		indexes.add(getCustomIndex("EXT014","CACGAA","EXT014"));
		indexes.add(getCustomIndex("EXT015","CATAGA","EXT015"));
		indexes.add(getCustomIndex("EXT016","CCGAGT","EXT016"));
		indexes.add(getCustomIndex("EXT017","CGGCAC","EXT017"));
		indexes.add(getCustomIndex("EXT018","CTATCA","EXT018"));
		indexes.add(getCustomIndex("EXT019","CTCGGT","EXT019"));
		indexes.add(getCustomIndex("EXT020","CTCTAG","EXT020"));
		indexes.add(getCustomIndex("EXT021","GACCCC","EXT021"));
		indexes.add(getCustomIndex("EXT022","GATGCA","EXT022"));
		indexes.add(getCustomIndex("EXT023","GCAACG","EXT023"));
		indexes.add(getCustomIndex("EXT024","GCTAGC","EXT024"));
		indexes.add(getCustomIndex("EXT025","GGGCCG","EXT025"));
		indexes.add(getCustomIndex("EXT026","GTAAAC","EXT026"));
		indexes.add(getCustomIndex("EXT027","GTGGGG","EXT027"));
		indexes.add(getCustomIndex("EXT028","GTGTAT","EXT028"));
		indexes.add(getCustomIndex("EXT029","TAGTAA","EXT029"));
		indexes.add(getCustomIndex("EXT030","TCAGCT","EXT030"));
		indexes.add(getCustomIndex("EXT031","TCCCGG","EXT031"));
		indexes.add(getCustomIndex("EXT032","TCCTTT","EXT032"));
		indexes.add(getCustomIndex("EXT033","TCTCAA","EXT033"));
		indexes.add(getCustomIndex("EXT034","TGCATA","EXT034"));
		indexes.add(getCustomIndex("EXT035","TGTCTG","EXT035"));
		indexes.add(getCustomIndex("EXT036","TGTGAC","EXT036"));
		indexes.add(getCustomIndex("EXT037","TTTTGG","EXT037"));
	
		indexes.forEach(index-> {
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				logger.info("creation index : "+ index.code +" / "+ index.categoryCode);
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
			} else {
				logger.info("index : "+ index.code + " already exists !!");
			}
		});			
	}	
	
	private static Index getCustomIndex(String code, String seq, String shortName) {
		Index index = new IlluminaIndex();
		
		index.code = code;
		index.name = code;
		index.shortName = shortName;
		index.sequence = seq ; 
		index.categoryCode = "SINGLE-INDEX";
	//index.supplierName = new HashMap<>();
		//index.supplierName.put("NEB", code);
		index.traceInformation=new TraceInformation(Constants.NGL_DATA_USER);
		
		return index;
	}
}
