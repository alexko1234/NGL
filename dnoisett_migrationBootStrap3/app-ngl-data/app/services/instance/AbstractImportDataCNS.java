package services.instance;

import models.LimsCNSDAO;
import models.TaraDAO;
import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractImportDataCNS extends AbstractImportData {

	public AbstractImportDataCNS(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name, durationFromStart, durationFromNextIteration);
	}

	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	protected static TaraDAO taraServices = Spring.getBeanOfType(TaraDAO.class);

}
