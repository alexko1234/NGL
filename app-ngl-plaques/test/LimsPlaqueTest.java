import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import lims.dao.LimsManipDAO;
import lims.models.Manip;

import org.junit.Test;

import play.api.modules.spring.Spring;
import utils.AbstractTests;


public class LimsPlaqueTest extends AbstractTests {
	
	@Test
	public void limsManipServicesGetManips(){
		
		LimsManipDAO  limsManipServices = Spring.getBeanOfType(LimsManipDAO.class);
        List<Manip> results = limsManipServices.findManips(13,2,"AAA");
        assertThat(results.size()).isNotNull();

	}
	

}
