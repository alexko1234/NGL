package services.instance.parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.LimsCNSDAO;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.parameter.Parameter;
import models.laboratory.parameter.index.Index;
import models.laboratory.parameter.index.NanoporeIndex;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class IndexImportCNS extends AbstractImportDataCNS{

	public IndexImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("IndexImportCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndexIllumina(limsServices,contextError);
		createIndexNanopore(contextError);
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
	
	public static void createIndexNanopore(ContextValidation contextValidation){
		
			for(int i = 1 ; i <= 12 ; i++){
				Index index = getNanoporeIndex(i);				
				if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
					InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
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
		index.supplierName = new HashMap<String,String>();
		index.supplierName.put("oxfordNanopore", code);
		index.traceInformation=new TraceInformation("ngl-data");
		return index;
	}
}
