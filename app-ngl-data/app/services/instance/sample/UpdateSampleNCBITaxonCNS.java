package services.instance.sample;

import javax.inject.Inject;

import fr.cea.ig.play.NGLContext;
import scala.concurrent.duration.FiniteDuration;
import services.ncbi.TaxonomyServices;

public class UpdateSampleNCBITaxonCNS extends AbstractUpdateSampleNCBITaxon {

	@Inject
	public UpdateSampleNCBITaxonCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx, TaxonomyServices taxonomyServices) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration, ctx, taxonomyServices);

	}
	
}
