package containers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;

import org.junit.Test;

import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ContainerTests extends AbstractTests {


	protected static ALogger logger=Logger.of("ContainerTest");
	
/**********************************Tests of ContainerHelper class methods (DAO Helper)***************************************************/	
	@Test
	public void validateCalculPercentageContent() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Container cnt =  ContainerTestHelper.getFakeContainer();
				
		//good value
		Content c1 = new Content();
		c1.percentage = 2.00;	
		cnt.contents.add(c1);		
		
		//null value
		Content c2 = new Content();
		c2.percentage = null;
		cnt.contents.add(c2);
		
		//empty value
		Content c3 = new Content();			
		cnt.contents.add(c3);
		
		//Float value greater than 100
		Content c4 = new Content();
		c4.percentage = 300.0;	
		cnt.contents.add(c4);
		
		//Float value less than 0		
		Content c5 = new Content();
		c5.percentage = 40.99;	
		cnt.contents.add(c5);
		
		//Float value between 0 and 1
		Content c6 = new Content();
		c6.percentage = 0.45;	
		cnt.contents.add(c6);
		
		Content c7 = new Content();
		c7.percentage = 0.03;	
		cnt.contents.add(c7);		
		
		//good given ContentPercentage
		ContainerHelper.calculPercentageContent(cnt.contents, 80.0);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(1.6);
		assertThat(c2.percentage).isEqualTo(80.0);
		assertThat(c3.percentage).isEqualTo(80.0);
		assertThat(c4.percentage).isEqualTo(240.0);	
		assertThat(c5.percentage).isEqualTo(32.79);
		assertThat(c6.percentage).isEqualTo(0.36);
		assertThat(c7.percentage).isEqualTo(0.02);
				
		//Big Percentage Content
		c1.percentage = 10.0;
		c2.percentage = null;
		Content c8 = new Content();
		cnt.contents.add(c8);
		c5.percentage = 0.94;
		c6.percentage = 0.98;		
		ContainerHelper.calculPercentageContent(cnt.contents, 380.0);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(38.0);
		assertThat(c2.percentage).isEqualTo(380.0);
		assertThat(c8.percentage).isEqualTo(380.0);
		assertThat(c5.percentage).isEqualTo(3.57);
		assertThat(c6.percentage).isEqualTo(3.72);
		
		//Zero Percentage Content
		c1.percentage = 10.0;
		c2.percentage = null;
		Content c9 = new Content();
		cnt.contents.add(c9);
		c5.percentage = 0.94;
		c6.percentage = 0.98;
		ContainerHelper.calculPercentageContent(cnt.contents, 0.00);
		contextValidation.displayErrors(logger);
		assertThat(c1.percentage).isEqualTo(0.00);
		assertThat(c2.percentage).isEqualTo(0.00);
		assertThat(c9.percentage).isEqualTo(0.00);
		assertThat(c5.percentage).isEqualTo(0.00);
		assertThat(c6.percentage).isEqualTo(0.00);		
	}
	
	@Test
	public void validateAddContent() {
		
	}
	
/**********************************Tests of Container class methods (DBObject)***************************************************/		
	
	@Test
	public void validateGetCurrentProcesses() {		
		Container cnt =  ContainerTestHelper.getFakeContainer("tube");
		cnt.inputProcessCodes=new HashSet<String>();
		Process process=new Process();
		process.code="validateGetCurrentProcesses";
		Process p=MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME,process);
		cnt.inputProcessCodes.add("validateGetCurrentProcesses");
		List<Process> processes =  cnt.getCurrentProcesses();
		MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, p);		
		assertThat(processes).isNotNull().isNotEmpty();		
	}
	
	@Test
	public void validateGetNullCurrentProcesses() {		
		Container cnt =  ContainerTestHelper.getFakeContainer();
		cnt.inputProcessCodes = null;
		assertThat(cnt.getCurrentProcesses()).isNullOrEmpty();				
	}
	
/**********************************Tests of Containers class methods (Controller)***************************************************/	
	
	
	/*
	@Test
	public void test() {
		
	}
	*/

}
