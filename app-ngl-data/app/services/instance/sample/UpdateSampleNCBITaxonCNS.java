package services.instance.sample;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import ncbi.services.TaxonomyServices;
import play.i18n.Messages;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

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
		if(taxonCode.equals("0"))
			return Messages.get("cns.ncbiScientificName.taxon.zero");
		else if(taxonCode.equals("1"))
			return Messages.get("cns.ncbiScientificName.taxon.one");
		else if(taxonCode.equals("2"))
			return Messages.get("cns.ncbiScientificName.taxon.two");
		else if(taxonCode.equals("3"))
			return Messages.get("cns.ncbiScientificName.taxon.three");
		else
			return TaxonomyServices.getScientificName(taxonCode);
	}

	public static String getLineageCNS(String taxonCode)
	{
		if(taxonCode.equals("0"))
			return Messages.get("cns.ncbiLineage.taxon.zero");
		else if(taxonCode.equals("1"))
			return Messages.get("cns.ncbiLineage.taxon.one");
		else if(taxonCode.equals("2"))
			return Messages.get("cns.ncbiLineage.taxon.two");
		else if(taxonCode.equals("3"))
			return Messages.get("cns.ncbiLineage.taxon.three");
		else
			return TaxonomyServices.getLineage(taxonCode);
	}



}
