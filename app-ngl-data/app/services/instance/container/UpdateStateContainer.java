package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;

import com.mongodb.MongoException;

import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;

public class UpdateStateContainer extends AbstractImportDataCNS {

	public UpdateStateContainer(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name, durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
			RulesException {
		
		
	}
	
	public void updateStateContainer(){
		// Problème appeler le workflow ngl-sq
	}

}
