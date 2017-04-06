package ncbi.services;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.junit.Test;

import play.libs.F.Callback;
import services.ncbi.NCBITaxon;
import services.ncbi.TaxonomyServices;
import utils.AbstractTests;

public class TaxonomyServicesTest extends AbstractTests{

	/**
	 * Test with taxonId =1358
	 * scientific name : Lactococcus lactis
	 * Lineage : cellular organisms; Bacteria; Terrabacteria group; Firmicutes; Bacilli; Lactobacillales; Streptococcaceae; Lactococcus
	 * @throws XPathExpressionException
	 */
	public void shouldGetTaxonomyInfo() throws XPathExpressionException
	{
		String scientificName = TaxonomyServices.getScientificName("1358");
		Assert.assertNotNull(scientificName);
		Assert.assertEquals("Lactococcus lactis", scientificName);
		String lineage = TaxonomyServices.getLineage("1358");
		Assert.assertNotNull(lineage);
		Assert.assertEquals("cellular organisms; Bacteria; Terrabacteria group; Firmicutes; Bacilli; Lactobacillales; Streptococcaceae; Lactococcus", lineage);
	}
	
	public void shouldNoTaxonomyInfo()
	{
		String scientificName = TaxonomyServices.getScientificName("135");
		Assert.assertNull(scientificName);
		String lineage = TaxonomyServices.getLineage("135");
		Assert.assertNull(lineage);
	}
	
	public void shouldNoTaxonomyInfoTaxonCodeNull()
	{
		Assert.assertNull(TaxonomyServices.getScientificName(null));
	}
	@Test
	public void shouldGetNCBITaxonTest() throws InterruptedException
	{
		
		TaxonomyServices.getNCBITaxon("1358").onRedeem(new Callback<NCBITaxon>() {
		    @Override
		    public void invoke(NCBITaxon taxon) throws Throwable {
		    	String scientificName = taxon.getScientificName();
		    	Assert.assertNotNull(scientificName);
				Assert.assertEquals("Lactococcus lactis", scientificName);
				String lineage = taxon.getLineage();
				Assert.assertNotNull(lineage);
				Assert.assertEquals("cellular organisms; Bacteria; Terrabacteria group; Firmicutes; Bacilli; Lactobacillales; Streptococcaceae; Lactococcus", lineage);
			
		    }
		});
		Thread.sleep(10000);
	}
	
	public void shouldNotNCBITaxonTest() throws InterruptedException
	{
		
		TaxonomyServices.getNCBITaxon("135").onRedeem(new Callback<NCBITaxon>() {
		    @Override
		    public void invoke(NCBITaxon taxon) throws Throwable {
		    	String scientificName = taxon.getScientificName();
		    	Assert.assertNull(scientificName);
				String lineage = taxon.getLineage();
				Assert.assertNull(lineage);
		    }
		});
		Thread.sleep(10000);
	}
	
	public void shouldNotNCBITaxonForNullCodeTest() throws InterruptedException
	{
		TaxonomyServices.getNCBITaxon(null).onRedeem(new Callback<NCBITaxon>() {
		    @Override
		    public void invoke(NCBITaxon taxon) throws Throwable {
		    	String scientificName = taxon.getScientificName();
		    	Assert.assertNull(scientificName);
				String lineage = taxon.getLineage();
				Assert.assertNull(lineage);
		    }
		});
		Thread.sleep(10000);
	}
}
