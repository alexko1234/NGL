package services.instance.sample;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.ncbi.TaxonomyServices;

public class UpdateSampleNCBITaxonCNS extends AbstractUpdateSampleNCBITaxon{

	
	public UpdateSampleNCBITaxonCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		updateSampleNCBI(contextError, null);

	}

	public String getScientificName(String taxonCode) 
	{
		return getScientificNameCNS(taxonCode);
	}
	
	public String getLineage(String taxonCode)
	{
		return getLineageCNS(taxonCode);
	}
	
	public static String getScientificNameCNS(String taxonCode)
	{
		return TaxonomyServices.getScientificName(taxonCode);
	}

	public static String getLineageCNS(String taxonCode)
	{
		return TaxonomyServices.getLineage(taxonCode);
	}



}
