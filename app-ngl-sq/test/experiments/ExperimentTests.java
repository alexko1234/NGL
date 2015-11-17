package experiments;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
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
	
	@Test
	public void updateDataMethodExperiment(){

		Experiment exp=ExperimentTestHelper.getFakeExperiment();
		ManyToOneContainer atomicTransfert1 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert1.line="1";
		atomicTransfert1.column="0";
		ManyToOneContainer atomicTransfert2 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert2.line="2";
		atomicTransfert2.column="0";
		
		exp.atomicTransfertMethods.add(0,atomicTransfert1);
		exp.atomicTransfertMethods.add(1, atomicTransfert2);
		
		atomicTransfert1.inputContainerUseds=new ArrayList<ContainerUsed>();
		ContainerUsed containerUsed = new ContainerUsed(CONTAINER_CODE);
		containerUsed.locationOnContainerSupport=new LocationOnContainerSupport();
		containerUsed.locationOnContainerSupport.code=CONTAINER_CODE;
		atomicTransfert1.inputContainerUseds.add(containerUsed);

		assertThat(exp.projectCodes).isNull();
		assertThat(exp.sampleCodes).isNull();
		assertThat(exp.inputContainerSupportCodes).isNull();
		exp=ExperimentHelper.updateXCodes(exp);
		assertThat("ADI").isIn(exp.projectCodes);
		assertThat("ADI_RD").isIn(exp.sampleCodes);
		assertThat("ADI_RD1").isIn(exp.inputContainerSupportCodes);
		
	}

	
	@Test
	public void diffInputContainerExperiment(){
		
		ContainerUsed containerA1=new ContainerUsed("A");
		ContainerUsed containerA2=new ContainerUsed("A");
		ContainerUsed containerA3=new ContainerUsed("A");
		ContainerUsed containerA4=new ContainerUsed("A");
		ContainerUsed containerA5=new ContainerUsed("A");
		ContainerUsed containerA6=new ContainerUsed("A");
		ContainerUsed containerA7=new ContainerUsed("A");
		ContainerUsed containerA8=new ContainerUsed("A");
		ContainerUsed containerB1=new ContainerUsed("B");
		ContainerUsed containerB2=new ContainerUsed("B");
		ContainerUsed containerC1=new ContainerUsed("C");
		ContainerUsed containerD1=new ContainerUsed("D");
		ContainerUsed containerD2=new ContainerUsed("D");
		
		List<ContainerUsed> containersFrom=new ArrayList<ContainerUsed>();
		containersFrom.add(containerA1);
		containersFrom.add(containerA2);
		containersFrom.add(containerA3);
		containersFrom.add(containerA4);
		containersFrom.add(containerA5);
		containersFrom.add(containerA6);
		containersFrom.add(containerA7);
		containersFrom.add(containerA8);
		containersFrom.add(containerB1);
		containersFrom.add(containerB2);
		containersFrom.add(containerC1);
		List<ContainerUsed> containersTo=new ArrayList<ContainerUsed>(containersFrom);
		
		List<String> containersDiff=null;
		containersDiff=ExperimentHelper.getDiff(containersFrom,containersTo);
		assertThat(containersDiff).isEmpty();
		
		//Remove C 
		containersTo.remove(containerC1);
		containersDiff=ExperimentHelper.getDiff(containersFrom,containersTo);
		assertThat(containersDiff).containsOnly(containerC1.code);
		containersTo.add(containerC1);

		//incomplete remove B
		containersTo.remove(containerB2);
		containersDiff=ExperimentHelper.getDiff(containersFrom,containersTo);
		assertThat(containersDiff).isEmpty();
		containersTo.add(containerB2);
		
		//remove B completely
		containersTo.remove(containerB2);
		containersTo.remove(containerB1);
		containersDiff=ExperimentHelper.getDiff(containersFrom,containersTo);
		assertThat(containersDiff.size()).isEqualTo(1);
		assertThat(containersDiff.get(0)).isEqualTo("B");
		containersTo.add(containerB2);
		containersTo.add(containerB1);
		
		// remove A completely and add D
		containersTo.remove(containerA1);
		containersTo.remove(containerA2);
		containersTo.remove(containerA3);
		containersTo.remove(containerA4);
		containersTo.remove(containerA5);
		containersTo.remove(containerA6);
		containersTo.remove(containerA7);
		containersTo.remove(containerA8);
		containersTo.add(containerD1);
		containersTo.add(containerD2);
		containersDiff=ExperimentHelper.getDiff(containersFrom,containersTo);
		assertThat(containersDiff.size()).isEqualTo(1);
		assertThat(containersDiff.get(0)).isEqualTo("A");
		containersDiff=ExperimentHelper.getDiff(containersTo,containersFrom);
		assertThat(containersDiff.size()).isEqualTo(1);
		assertThat(containersDiff.get(0)).isEqualTo("D");
		containersTo.add(containerA1);
		containersTo.add(containerA2);
		containersTo.add(containerA3);
		containersTo.add(containerA4);
		containersTo.add(containerA5);
		containersTo.add(containerA6);
		containersTo.add(containerA7);
		containersTo.add(containerA8);
		containersTo.remove(containerD1);
		containersTo.remove(containerD2);
		
	}

	
	
	
	
}
