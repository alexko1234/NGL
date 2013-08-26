import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import ls.dao.LimsManipDAO;
import ls.models.Manip;

import org.junit.Test;

import play.api.modules.spring.Spring;
import util.AbstractTests;


public class LimsPlaqueTest extends AbstractTests {
	
	@Test
	public void limsManipServicesGetManips(){
		
		LimsManipDAO  limsManipServices = Spring.getBeanOfType(LimsManipDAO.class);
        List<Manip> results = limsManipServices.findManips(13,2,"AAA");
        assertThat(results.size()).isNotNull();

	}
	

}
