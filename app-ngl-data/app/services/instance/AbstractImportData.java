package services.instance;

import java.sql.SQLException;

import models.Constants;
import models.utils.dao.DAOException;

import org.slf4j.MDC;

import com.mongodb.MongoException;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import validation.ContextValidation;

public abstract class AbstractImportData implements Runnable{

	public ContextValidation contextError;
	final String name;
	protected ALogger logger;

	public abstract void runImport() throws SQLException, DAOException, MongoException, RulesException;

	public AbstractImportData(String name,FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration){
		this.contextError=new ContextValidation(Constants.NGL_DATA_USER);
		this.name=name;
		logger=Logger.of(this.getClass().getName());
		Akka.system().scheduler().schedule(durationFromStart,durationFromNextIteration
				, this, Akka.system().dispatcher()
				); 
	}

	public void run() {
		boolean error=false;

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
			error=true;
		}
		finally{
			error=contextError.hasErrors()?true:error;
			/* Display error messages  */
			contextError.displayErrors(logger);
			/* Logger send an email */
			if(error){
				logger.error("ImportData End Error");
			}else {
				logger.info("ImportData End");
			}
			MDC.remove("name");
		}
	};



}
