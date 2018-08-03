package services.instance.parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.LimsGETDAO;
import models.laboratory.parameter.index.Index;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataGET;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;

public class IndexImportGET extends AbstractImportDataGET{

	public IndexImportGET(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		
		super("IndexImportGET",durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndex(limsServices,contextError);
	}

	
	public static void createIndex(LimsGETDAO limsServices,ContextValidation contextValidation) throws SQLException, DAOException{

		
	List<Index> indexs = limsServices.findIndexIlluminaToCreate(contextValidation) ;
	
		for(Index index:indexs){
	
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code);
			}
		}
	
		InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,indexs,contextValidation);
		
	}
}
