package experiments;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.ManyToOneContainer;
import models.utils.instance.ExperimentHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.InitDataHelper;

public class ExperimentTests extends AbstractTests{

	protected static ALogger logger=Logger.of("ExperimentTest");
	final String CONTAINER_CODE="ADI_RD1";
	
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}

	@AfterClass	
	public static  void resetData(){
		InitDataHelper.endTest();
	}
	
	
	
}
