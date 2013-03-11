package lims.cng.dao;

import java.util.List;

import junit.framework.Assert;
import lims.cng.services.LimsRunServices;
import lims.models.instrument.Instrument;

import org.junit.Test;

import play.api.modules.spring.Spring;
import utils.AbstractTests;

public class LimsRunDAOTest extends AbstractTests {

	@Test
	 public void getInstruments() {
		LimsRunServices  limsRunServices = Spring.getBeanOfType(LimsRunServices.class);
		Assert.assertNotNull(limsRunServices);
		List<Instrument> instruments  = limsRunServices.getInstruments();
		Assert.assertTrue(instruments.size() > 0);
		
	}

}
