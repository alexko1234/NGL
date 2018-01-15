package services.instance.sample;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.libs.concurrent.Futures;
// import play.libs.F.Callback;
// import play.libs.F.Promise;
import rules.services.RulesException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.slf4j.MDC;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;
import services.ncbi.NCBITaxon;
import services.ncbi.TaxonomyServices;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;

public abstract class AbstractUpdateSampleNCBITaxon extends AbstractImportData{

	@Inject
	public AbstractUpdateSampleNCBITaxon(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name, durationFromStart, durationFromNextIteration, ctx);
	}

	public void run() {
		MDC.put("name", name);
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		logger.info("ImportData execution :"+name);

		try{
			contextError.setUpdateMode();
			runImport();
			contextError.removeKeyFromRootKeyName("import");

		}catch (Throwable e) {
			logger.error("",e);
			logger.error("ImportData End Error");
		}
	};
	
	public void updateSampleNCBI(ContextValidation contextError,
			List<String> sampleCodes) {

		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("taxonCode", 1);
		
		
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, 
				DBQuery.notEquals("taxonCode",null).or(DBQuery.is("ncbiScientificName", null),DBQuery.is("ncbiLineage", null)),keys).toList();
		Logger.info("update sample without ncbi data : "+samples.size());
		Map<String, List<Sample>> samplesByTaxon = samples.stream().collect(Collectors.groupingBy(sample -> sample.taxonCode));
		
		// List<Promise<NCBITaxon>> promises = samplesByTaxon.keySet()
		List<CompletionStage<NCBITaxon>> promises = samplesByTaxon.keySet()
			.stream()
			.map(taxonCode -> TaxonomyServices.getNCBITaxon(taxonCode))
			.collect(Collectors.toList());
		
		
		// Promise.
		Futures.sequence(promises)
		//.onRedeem(
		.thenAcceptAsync(
				// new Callback<List<NCBITaxon>>() {
				new Consumer<List<NCBITaxon>>() {
			    @Override
			    // public void invoke(List<NCBITaxon> taxons)  {
			    public void accept(List<NCBITaxon> taxons)  {
			        taxons.forEach(taxon -> {
				        if(taxon.error){
				        	contextError.addErrors(taxon.code, "error to find taxon");
				        }
				        if(!taxon.exists){
				        	contextError.addErrors(taxon.code, "taxon code not exists !!");
				        }
				        
			        	String ncbiScientificName=taxon.getScientificName();
						String ncbiLineage=taxon.getLineage();
						
						DBUpdate.Builder builder = DBUpdate.set("traceInformation.modifyDate",new Date() ).set("traceInformation.modifyUser","ngl-data");
						
						if(ncbiScientificName==null){
							contextError.addErrors(taxon.code, "no ncbi scientific name");
							builder.set("ncbiScientificName", "no ncbi scientific name");
						}else{
							builder.set("ncbiScientificName", ncbiScientificName);
						}
						if(ncbiLineage==null){
							contextError.addErrors(taxon.code, "no ncbi lineage");
							builder.set("ncbiLineage", "no ncbi lineage");
						}else{
							builder.set("ncbiLineage", ncbiLineage);
						}
						samplesByTaxon.get(taxon.code).forEach(sample ->{
								//Logger.info("Update sample taxon info "+sample.code+" / "+taxon.code);
								MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,  Sample.class, 
										DBQuery.is("code", sample.code), builder);	
						});					
						
			        });
			        Logger.debug("finish update");
			        if(contextError.hasErrors()){
			        	contextError.displayErrors(logger);
			        	logger.error("ImportData End Error");
			        }else {
						logger.info("ImportData End");
					}
					MDC.remove("name");
			    }
			});
						
	}
	
	
	

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		updateSampleNCBI(contextError, null);

	}

}
