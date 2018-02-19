package data.resources;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import fr.cea.ig.play.test.WSHelper;
import ngl.bi.AbstractBIServerTest;
import play.Logger;
import play.libs.ws.WSResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreatmentTypesTest extends AbstractBIServerTest{

	@Test
	public void test1list()
	{
		Logger.debug("list TreatmentType");
		WSResponse response = WSHelper.get(ws, "/api/treatment-types", 200);
		assertThat(response.asJson()).isNotNull();
	}

	@Test
	public void test2get()
	{
		Logger.debug("get TreatmentType");
		WSResponse response = WSHelper.get(ws, "/api/treatment-types/ngsrg-illumina", 200);
		assertThat(response.asJson()).isNotNull();
	}
}
