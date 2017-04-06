package services.instance.sample;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Promise;
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

public abstract class AbstractUpdateSampleNCBITaxon extends AbstractImportData{

	
	public AbstractUpdateSampleNCBITaxon(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name, durationFromStart, durationFromNextIteration);
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
				DBQuery.or(DBQuery.notExists("ncbiScientificName"),DBQuery.notExists("ncbiLineage")),keys).toList();
		
		Map<String, List<Sample>> samplesByTaxon = samples.stream().collect(Collectors.groupingBy(sample -> sample.taxonCode));
		
		List<Promise<NCBITaxon>> promises = samplesByTaxon.keySet()
			.stream()
			.map(taxonCode -> TaxonomyServices.getNCBITaxon(taxonCode))
			.collect(Collectors.toList());
		
		
		Promise.sequence(promises).onRedeem(new Callback<List<NCBITaxon>>() {
			    @Override
			    public void invoke(List<NCBITaxon> taxons)  {
			        taxons.forEach(taxon -> {
				        String ncbiScientificName=taxon.getScientificName();
						String ncbiLineage=taxon.getLineage();
						
						if(ncbiScientificName==null){
							contextError.addErrors(taxon.code, "no scientific name ");
						}
						if(ncbiLineage==null){
							contextError.addErrors(taxon.code, "no lineage ");
						}
				        
						samplesByTaxon.get(taxon.code).forEach(sample ->{
							//Logger.info("Update sample taxon info "+sample.code+" / "+taxon.code);
							MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,  Sample.class, 
									DBQuery.is("code", sample.code), DBUpdate.set("ncbiScientificName", ncbiScientificName).set("ncbiLineage", ncbiLineage)
									.set("traceInformation.modifyDate",new Date() ).set("traceInformation.modifyUser","ngl-data"));	
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
