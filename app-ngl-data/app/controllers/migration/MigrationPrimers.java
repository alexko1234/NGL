package controllers.migration;

import java.sql.SQLException;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.play.NGLContext;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.sample.UpdateSamplePropertiesCNS;
import validation.ContextValidation;
import workflows.experiment.ExpWorkflowsHelper;

public class MigrationPrimers extends UpdateSamplePropertiesCNS {
	final ExpWorkflowsHelper expWorkflowsHelper;
	public MigrationPrimers(FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration,
			NGLContext ctx) {
		super("MigrationPrimers", durationFromStart, durationFromNextIteration, ctx);
		expWorkflowsHelper = ctx.injector().instanceOf(ExpWorkflowsHelper.class);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		//updateSampleImported(contextError);
		updateSampleCreated(contextError);		
	}

	private void updateSampleImported(ContextValidation contextError) {
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.or(DBQuery.exists("properties.amplificationPrimers").notExists("life"),DBQuery.exists("properties.originAmplificationPrimers").notExists("life")))
				.sort("code").toList();
		Logger.info("Nb samples to update :"+samples.size());
		samples.stream().forEach(sample -> {
			//Logger.debug("Sample "+sample.code);
			try{
				updateOneSample(sample,contextError);
			}catch(Throwable t){
				logger.error(t.getMessage(),t);	
				if(null != t.getMessage())
					contextError.addErrors(sample.code, t.getMessage());
				else
					contextError.addErrors(sample.code, "null");
			}						
		});
		
		Logger.info("End : updateSampleImported");
		
	}

	
	private void updateSampleCreated(ContextValidation contextError) {
		Integer skip = 0;
		
		MongoDBResult<Experiment> result = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.exists("experimentProperties.amplificationPrimers").is("typeCode","tag-pcr").is("code", "TAG-PCR-20171120_093827CDA"));
		Integer nbResult = result.count(); 
		Logger.info("Nb samples to update :"+nbResult);
		while(skip < nbResult) {
			try {
				long t1 = System.currentTimeMillis();
					List<Experiment> cursor = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.exists("experimentProperties.amplificationPrimers").is("typeCode","tag-pcr").is("code", "TAG-PCR-20171120_093827CDA"))
						.sort("code").skip(skip).limit(1000)
						.toList();
	
					cursor.forEach(exp -> {
					try{
						expWorkflowsHelper.updateContentPropertiesWithExperimentContentProperties(contextError, exp);				
					}catch(Throwable e){
						logger.error("Experiment : "+exp.code+" - "+e,e);
						if(null != e.getMessage())
							contextError.addErrors(exp.code, e.getMessage());
						else
							contextError.addErrors(exp.code, "null");
					}
				});
					skip = skip+1000;
					long t2 = System.currentTimeMillis();
					logger.debug("time "+skip+" - "+((t2-t1)/1000));
				}catch(Throwable e){
					logger.error("Error : "+e,e);
					if(null != e.getMessage())
						contextError.addErrors("Error", e.getMessage());
					else
						contextError.addErrors("Error", "null");
				}
		}
	}
	
}
