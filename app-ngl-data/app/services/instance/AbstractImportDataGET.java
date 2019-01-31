package services.instance;

import fr.cea.ig.play.migration.NGLContext;
import models.LimsGETDAO;
//import models.TaraDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataGET extends AbstractImportData {

	protected static LimsGETDAO  limsServices = Spring.getBeanOfType(LimsGETDAO.class);

	public AbstractImportDataGET(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name, durationFromStart, durationFromNextIteration, ctx);
	}

}