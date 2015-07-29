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

/**
 * @author dnoisett
 * Import Indexes from CNG's LIMS to NGL, ( no update for index)
 * FDS remplacement de l'appel a Logger par logger
 */

public class IndexImportCNG extends AbstractImportDataCNG{

	public IndexImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("IndexImportCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndex(); // NW: supression des arguments
	}

	// NW: suppression du mot clef "static" 
	// NW: supression des arguments et renommage de contextValidation en contextError
	public void createIndex() throws SQLException, DAOException{
		logger.info("start loading indexes");
		
		//-1- chargement depuis la base source Postgresql
		logger.info("1/3 loading from source database...");
		List<Index> indexes = limsServices.findIndexIlluminaToCreate(contextError) ;
		logger.info("found "+indexes.size() + " items");
		
		//-2a- trouver les samples concernés dans la base mongoDB et les supprimer
		logger.info("2/3 delete from dest database...");
		for (Index index:indexes) {
			if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code)) {
				MongoDBDAO.deleteByCode(InstanceConstants.PARAMETER_COLL_NAME, Index.class, index.code);
			}
		}

		//-3- sauvegarde dans la base cible MongoDb
		logger.info("3/3 saving to dest database...");
		InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,indexes,contextError);
		
		logger.info("end loading indexes");
	}
}
