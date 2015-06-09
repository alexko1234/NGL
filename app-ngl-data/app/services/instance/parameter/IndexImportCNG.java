package services.instance.parameter;

import java.sql.SQLException;
import java.util.List;

import play.Logger;

import models.LimsCNGDAO;
import models.laboratory.parameter.Index;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class IndexImportCNG extends AbstractImportDataCNG{

	public IndexImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("IndexImportCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndex(limsServices,contextError);
	}

	
	public static void createIndex(LimsCNGDAO limsServices,ContextValidation contextValidation) throws SQLException, DAOException{
		
		Logger.debug("start loading indexes");
		
		List<Index> indexs = limsServices.findIndexIlluminaToCreate(contextValidation) ;
		
		for (Index index:indexs) {
			if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code)) {
				MongoDBDAO.deleteByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code);
			}
		}	
		InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,indexs,contextValidation);
		
		Logger.debug("end loading indexes");
	}
}
