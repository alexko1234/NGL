package services.instance;

import java.sql.SQLException;

import models.utils.dao.DAOException;

import org.slf4j.MDC;

import com.mongodb.MongoException;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;
import validation.ContextValidation;

public abstract class AbstractImportData implements Runnable{

	protected static ContextValidation contextError = new ContextValidation();
	final String name;
	protected ALogger logger;

	public abstract void runImport() throws SQLException, DAOException, MongoException;

	public AbstractImportData(String name,FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration){
		this.name=name;
		logger=Logger.of(this.getClass().getName());
		Akka.system().scheduler().schedule(durationFromStart,durationFromNextIteration
				, this, Akka.system().dispatcher()
				); 
	}
	

	public void run() {
		MDC.put("name", name);
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		logger.info("ImportData execution :"+name);

		try{
			contextError.setCreationMode();
			runImport();
			contextError.removeKeyFromRootKeyName("import");

		}catch (Throwable e) {
			
			logger.error("",e);
		}
		finally{
			/* Display error messages  */
			contextError.displayErrors(logger);
			/* Logger send an email */
			logger.info("ImportData End");
			MDC.remove("name");
		}
	};



}
