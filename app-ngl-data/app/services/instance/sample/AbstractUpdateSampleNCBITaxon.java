package services.instance.sample;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

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
					DBQuery.is("code", sample.code), DBUpdate.set("ncbiScientificName", ncbiScientificName).set("ncbiLineage", ncbiLineage)
					.set("traceInformation.modifyDate",new Date() ));
						
			if(ncbiScientificName==null)
				contextError.addErrors(sample.code, "no scientific name ");
			if(ncbiLineage==null)
				contextError.addErrors(sample.code, "no lineage ");
		}
	}
	
	public abstract String getScientificName(String taxonCode);
	
	
	public abstract String getLineage(String taxonCode);
	




}
