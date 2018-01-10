package services.instance.sample;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.NGLContext;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.ncbi.TaxonomyServices;

public class UpdateSampleNCBITaxonCNS extends AbstractUpdateSampleNCBITaxon{

	@Inject
	public UpdateSampleNCBITaxonCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration, ctx);

	}



}
