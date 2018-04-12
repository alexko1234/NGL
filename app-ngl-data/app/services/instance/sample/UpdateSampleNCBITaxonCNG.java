package services.instance.sample;

import javax.inject.Inject;

import fr.cea.ig.play.NGLContext;
import scala.concurrent.duration.FiniteDuration;
import services.ncbi.TaxonomyServices;

public class UpdateSampleNCBITaxonCNG extends AbstractUpdateSampleNCBITaxon {

	@Inject
	public UpdateSampleNCBITaxonCNG(FiniteDuration durationFromStart,
			                        FiniteDuration durationFromNextIteration, 
			                        NGLContext ctx, 
			                        TaxonomyServices taxonomyServices) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration, ctx, taxonomyServices);
	}

}
