package services.instance.sample;

import java.sql.SQLException;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;
import validation.ContextValidation;

public abstract class AbstractUpdateSampleNCBITaxon extends AbstractImportData{

	
	public AbstractUpdateSampleNCBITaxon(String name, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name, durationFromStart, durationFromNextIteration);
	}

	public void updateSampleNCBI(ContextValidation contextError,
			List<String> sampleCodes) throws SQLException, DAOException {

		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.or(DBQuery.notExists("ncbiScientificName"),DBQuery.notExists("ncbiLineage"))).toList();
		for(Sample sample: samples)
		{
			String ncbiScientificName=getScientificName(sample.taxonCode);
			String ncbiLineage=getLineage(sample.taxonCode);
			MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,  Sample.class, 
					DBQuery.is("code", sample.code), DBUpdate.set("ncbiScientificName", ncbiScientificName).set("ncbiLineage", ncbiLineage));
			if(ncbiScientificName==null)
				contextError.addErrors(sample.code, "no scientific name ");
			if(ncbiLineage==null)
				contextError.addErrors(sample.code, "no lineage ");
		}
	}
	
	public abstract String getScientificName(String taxonCode);
	
	
	public abstract String getLineage(String taxonCode);
	




}
