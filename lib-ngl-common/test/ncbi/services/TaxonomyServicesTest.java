package ncbi.services;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTests;

public class TaxonomyServicesTest extends AbstractTests{

	/**
	 * Test with taxonId =1358
	 * scientific name : Lactococcus lactis
	 * Lineage : cellular organisms; Bacteria; Terrabacteria group; Firmicutes; Bacilli; Lactobacillales; Streptococcaceae; Lactococcus
	 * @throws XPathExpressionException
	 */
	@Test
	public void shouldGetTaxonomyInfo() throws XPathExpressionException
	{
		String scientificName = TaxonomyServices.getScientificName("1358");
		Assert.assertNotNull(scientificName);
		Assert.assertEquals("Lactococcus lactis", scientificName);
		String lineage = TaxonomyServices.getLineage("1358");
		Assert.assertNotNull(lineage);
		Assert.assertEquals("cellular organisms; Bacteria; Terrabacteria group; Firmicutes; Bacilli; Lactobacillales; Streptococcaceae; Lactococcus", lineage);
	}
}
