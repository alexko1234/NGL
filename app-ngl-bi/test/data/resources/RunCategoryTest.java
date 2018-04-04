package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ngl.bi.AbstractBIServerTest;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunCategoryTest extends AbstractBIServerTest {

	private static final play.Logger.ALogger logger = play.Logger.of(RunCategoryTest.class);
	
	@Test
	public void test1list()
	{
		logger.debug("list RunCategory");
//		WSResponse response = WSHelper.getAsBot(ws, "/api/run-categories", 200);
		WSResponse response = wsBot.get("/api/run-categories", 200);
		assertThat(response.asJson()).isNotNull();
	}

}
