package services.instance.sample;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.NGLContext;
import models.utils.dao.DAOException;
import services.ncbi.TaxonomyServices;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSampleNCBITaxonCNG extends AbstractUpdateSampleNCBITaxon{

	@Inject
	public UpdateSampleNCBITaxonCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx, TaxonomyServices taxonomyServices) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration, ctx, taxonomyServices);

	}

	

	
}
