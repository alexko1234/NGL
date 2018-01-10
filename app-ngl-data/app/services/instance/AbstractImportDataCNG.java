package services.instance;

import javax.inject.Inject;

import fr.cea.ig.play.NGLContext;
import models.LimsCNGDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataCNG extends AbstractImportData {

	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);
	@Inject
	public AbstractImportDataCNG(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name, durationFromStart, durationFromNextIteration, ctx);
	}



}
