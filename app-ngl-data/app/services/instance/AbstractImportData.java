package services.instance;

import java.sql.SQLException;

import javax.inject.Inject;

import org.slf4j.MDC;

import com.mongodb.MongoException;

import fr.cea.ig.play.migration.NGLContext;
import models.Constants;
import models.utils.dao.DAOException;
// import play.libs.Akka;
//import static fr.cea.ig.play.IGGlobals.akkaSystem;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import validation.ContextValidation;

public abstract class AbstractImportData implements Runnable {

	public    final ContextValidation   contextError;
	protected final String              name;
	protected final play.Logger.ALogger logger;
	protected final NGLContext          ctx;
	
	public abstract void runImport() throws SQLException, DAOException, MongoException, RulesException;

	@Inject
	public AbstractImportData(String name, FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration, NGLContext ctx) {
		this.contextError = new ContextValidation(Constants.NGL_DATA_USER);
		this.name         = name;
		this.ctx          = ctx;
		logger            = play.Logger.of(this.getClass().getName());
		logger.info(name+" start in "+durationFromStart.toMinutes()+" minutes and other iterations every "+durationFromNextIteration.toMinutes()+" minutes");
		
		//Akka.system()
		ctx.akkaSystem().scheduler().schedule(durationFromStart,
				                              durationFromNextIteration, 
				                              this, 
				                              // Akka.system()
				                              ctx.akkaSystem().dispatcher());
	}

	@Override
	public void run() {
		boolean error = false;

		MDC.put("name", name);
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		long t1 = System.currentTimeMillis();
//<<<<<<< HEAD
		logger.info("AbstractImportData - run - ImportData execution :"+name);

		try{
//=======
//		try {
//>>>>>>> V2.0.2
			contextError.setCreationMode();
			runImport();
			contextError.removeKeyFromRootKeyName("import");

//<<<<<<< HEAD
		}catch (Throwable e) {
			logger.error("AbstractImportData - run - try runImport : error",e);
//=======
//		} catch (Throwable e) {
//			logger.error("",e);
//>>>>>>> V2.0.2
			error=true;
		} finally {
			error=contextError.hasErrors()?true:error;
			/* Display error messages  */
			contextError.displayErrors(logger);
			/* Logger send an email */
			long t2 = System.currentTimeMillis();
			if(error){
				logger.error("AbstractImportData - run - ImportData End Error");
			}else {
				logger.info("ImportData End - "+(t2-t1)/1000+" s");
				logger.info("AbstractImportData - run - ImportData End");
			}
			MDC.remove("name");
		}
	}

}
