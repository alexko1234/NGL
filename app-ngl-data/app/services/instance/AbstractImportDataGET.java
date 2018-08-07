package services.instance;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.LimsGETDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataGET extends AbstractImportData {

	
	@Inject
	public AbstractImportDataGET(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name, durationFromStart, durationFromNextIteration, ctx);
		limsServices = Spring.getBeanOfType(LimsGETDAO.class);
	}
	protected static LimsGETDAO  limsServices;

}