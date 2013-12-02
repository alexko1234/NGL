package services.instance;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import models.LimsCNSDAO;
import models.TaraDAO;
import models.utils.dao.DAOException;
import play.Logger;
import play.api.modules.spring.Spring;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;
import validation.ContextValidation;
import org.slf4j.MDC;

public abstract class AbstractImportData implements Runnable{

	protected static ContextValidation contextError = new ContextValidation();
	//que fait-on pour CNG ??
	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	protected static TaraDAO taraServices = Spring.getBeanOfType(TaraDAO.class);
	
	public abstract void runImport() throws SQLException, DAOException;
	
	public AbstractImportData(FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration){
		Akka.system().scheduler().schedule(durationFromStart,durationFromNextIteration
                , this, Akka.system().dispatcher()
				); 
	}
	
	
	public void run() {
		//MDC.put("", this.getClass().getName());
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		Logger.info("ImportData execution");
		
		try{
			contextError.setCreationMode();
			runImport();
			contextError.removeKeyFromRootKeyName("import");

		}catch (Exception e) {
			Logger.debug("",e);
		}

		/* Display error messages  */
		contextError.displayErrors();
		/* Logger send an email */
		Logger.info("ImportData End");
	};
	
	
	
}
