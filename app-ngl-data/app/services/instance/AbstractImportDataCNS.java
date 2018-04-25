package services.instance;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.LimsCNSDAO;
import models.TaraDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataCNS extends AbstractImportData {

	@Inject
	public AbstractImportDataCNS(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name, durationFromStart, durationFromNextIteration, ctx);
		limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
		taraServices = Spring.getBeanOfType(TaraDAO.class);
	}

	protected static LimsCNSDAO  limsServices;
	protected static TaraDAO taraServices;

}
