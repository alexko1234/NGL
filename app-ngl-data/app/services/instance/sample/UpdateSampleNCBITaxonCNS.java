package services.instance.sample;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.ncbi.TaxonomyServices;

public class UpdateSampleNCBITaxonCNS extends AbstractUpdateSampleNCBITaxon{

	
	public UpdateSampleNCBITaxonCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration);

	}



}
