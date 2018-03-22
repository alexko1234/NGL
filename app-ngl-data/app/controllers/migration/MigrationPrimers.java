package controllers.migration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AbstractContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.libs.Json;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.sample.UpdateSamplePropertiesCNS;
import validation.ContextValidation;
import workflows.experiment.ExpWorkflows;
import workflows.experiment.ExpWorkflowsHelper;

public class MigrationPrimers extends UpdateSamplePropertiesCNS {
	final ExpWorkflows expWorkflows;
	public MigrationPrimers(FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration,
			NGLContext ctx) {
		super("MigrationPrimers", durationFromStart, durationFromNextIteration, ctx);
		expWorkflows = ctx.injector().instanceOf(ExpWorkflows.class);
		mapping = new HashMap<String, String>();
		mapping.put("16SV4 Procaryote", "16S V4 Prok 515FF/806R");
		mapping.put("16SV4V5 Archae", "16S V4V5 Archae 517F/958R");
		mapping.put("16SV5V6 Prok", "16S V5V6 Prok 784F/1061R");
		mapping.put("18S_V4 primer", "18S V4 Euk V4f (TAReukF1)/V4r (TAReukR)");
		mapping.put("18SV1V2 Metazoaire", "18S V1V2 Metazoaire SSUF04/SSURmod");
		mapping.put("ITS2 primer", "ITS2/SYM_VAR_5,8S2/SYM_VAR_REV");
		mapping.put("SYM_VAR_5.8S2 / SYM_VAR_REV", "ITS2/SYM_VAR_5,8S2/SYM_VAR_REV");
		mapping.put("V9 primer", "18S V9 1389F/1510R");
		mapping.put("Fuhrman primer", "Fuhrman primers");
		mapping.put("COI primer LCO1490/ HC02198", "COI primers LCOI1490/HC022198");
		mapping.put("COI primer m1COIintF / jgHCO2198", "COI primers m1COIintF/jgHCO2198");
		mapping.put("Sneed2015 27F / 519Rmodbio", "16S V1V2V3 Prok Sneed2015 27F/519Rmodbio");
		mapping.put("5.8S F1 / R1", "5.8S F1/R1");
		mapping.put("16S primer + Fuhrman primer", "16S FL 27F/1492R + Fuhrman primers");
		mapping.put("16S primer + Fuhrman primer 2", "16S FL 27F/1390R + Fuhrman primers");
		mapping.put("ITSD / ITS2REV", "ITSD/ITS2REV");
		mapping.put("ITSintfor2 / ITS-Reverse", "ITSintfor2/ITS-Reverse");
		mapping.put("Amp 48-1", "Amp 48-1");
		mapping.put("Amp 48-2", "Amp 48-2");
		mapping.put("autre", "autre");
	}

	final Map<String, String> mapping;
	
	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		//updateSampleImported(contextError);
		updateExperiments(contextError);		
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

	
	private void updateExperiments(ContextValidation contextError) {
		Integer skip = 0;
		final ContextValidation contextVal = new ContextValidation("galbini");
		contextVal.putObject("updateContentProperties", Boolean.TRUE);
		MongoDBResult<Experiment> result = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.exists("experimentProperties.amplificationPrimers").is("typeCode","tag-pcr").is("code", "TAG-PCR-20171023_094723AIG"));
		Integer nbResult = result.count(); 
		logger.info("Nb exp to update :"+nbResult);
		while(skip < nbResult) {
			try {
				long t1 = System.currentTimeMillis();
					List<Experiment> cursor = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.exists("experimentProperties.amplificationPrimers").is("typeCode","tag-pcr").is("code", "TAG-PCR-20171023_094723AIG"))
						.sort("code").skip(skip).limit(1000)
						.toList();
	
					cursor.forEach(exp -> {
						try{
							logger.info("treat :"+exp.code);
							String primer = exp.experimentProperties.get("amplificationPrimers").value.toString();
							if("TAG-PCR-20160916_135613JDD".equals(exp.code)){
								primer = "16S primer + Fuhrman primer 2";			
							}
							if(mapping.containsKey(primer)){
								exp.experimentProperties.get("amplificationPrimers").value = mapping.get(primer);
								expWorkflows.applyPreValidateCurrentStateRules(contextVal, exp);
								expWorkflows.applyPostValidateCurrentStateRules(contextVal, exp);
								MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, exp);	
							}else{
								logger.error("not managed "+primer +" "+exp.code);
							}
							logger.info("treat end :"+exp.code);
							
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
