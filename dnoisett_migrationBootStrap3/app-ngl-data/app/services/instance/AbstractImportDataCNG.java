package services.instance;

import models.LimsCNGDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataCNG extends AbstractImportData {

	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);

	public AbstractImportDataCNG(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name, durationFromStart, durationFromNextIteration);
	}



}
