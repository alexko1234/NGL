package resources;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.bi.Global.devapp;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import fr.cea.ig.play.test.WSHelper;
import play.Logger;
import play.libs.ws.WSResponse;
import utils.AbstractTests;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunCategoryTest extends AbstractTests{

	
	@Test
	public void test1list()
	{
		testInServer(devapp(),
				ws -> {	
					Logger.debug("list RunCategory");
					WSResponse response = WSHelper.get(ws, "/api/run-categories", 200);
					assertThat(response.asJson()).isNotNull();
				});
	}


}
