import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import ls.models.Manip;
import ls.services.LimsManipServices;

import org.junit.Test;

import play.api.modules.spring.Spring;
import util.AbstractTests;


public class LimsPlaqueTest extends AbstractTests {
	
	@Test
	public void limsManipServicesGetManips(){
		
		LimsManipServices  limsManipServices = Spring.getBeanOfType(LimsManipServices.class);
        List<Manip> results = limsManipServices.getManips(13,2,"AAA");
        assertThat(results.size()).isNotNull();

	}
	

}
