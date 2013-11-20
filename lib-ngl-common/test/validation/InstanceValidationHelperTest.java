package validation;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;

import utils.AbstractTests;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerSupportValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class InstanceValidationHelperTest extends AbstractTests {
		
	
	static Project project;
	static Project project1;
	
	static Sample sample;
	static Sample sample1;
	static Sample sample2;
	
	static Stock stock;
	
	static Container container;

	static ReagentInstance reagentInstance;
		
	@BeforeClass
	public static  void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		project=saveDBOject(Project.class,InstanceConstants.PROJECT_COLL_NAME,"project");
		project1=saveDBOject(Project.class,InstanceConstants.PROJECT_COLL_NAME,"project1");
		
		sample=saveDBOject(Sample.class,InstanceConstants.SAMPLE_COLL_NAME,"sample");
		sample1=saveDBOject(Sample.class,InstanceConstants.SAMPLE_COLL_NAME,"sample1");
		
		
		sample2 = new Sample(); 
		sample2.code = "SampleCode";
		List<String> l =new ArrayList<String>();
		l.add("ProjectCode"); 
		sample2.projectCodes = l;
		
		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample2);
		
		
		stock=saveDBOject(Stock.class,InstanceConstants.STOCK_COLL_NAME,"stock");
		
		container=saveDBOject(Container.class,InstanceConstants.CONTAINER_COLL_NAME,"container");
		
		reagentInstance=saveDBOject(ReagentInstance.class, InstanceConstants.REAGENT_INSTANCE_COLL_NAME, "reagent");

	}
	
	@AfterClass
	public static void deleteData(){
		MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, project);
		MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, project1);
		
		MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample1);
		
		MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample2);

		MongoDBDAO.delete(InstanceConstants.STOCK_COLL_NAME, stock);
		
		MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		
		MongoDBDAO.delete(InstanceConstants.REAGENT_INSTANCE_COLL_NAME,reagentInstance);
	}
	

	@Test
	public void validationProjectCodesTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> projects=new ArrayList<String>();
		projects.add(project.code);
		projects.add(project1.code);
		CommonValidationHelper.validateProjectCodes(projects,contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationProjectCodesRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> projects=new ArrayList<String>();
		CommonValidationHelper.validateProjectCodes(projects,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public  void validationProjectCodesNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> projects=new ArrayList<String>();
		projects.add("notexist");
		CommonValidationHelper.validateProjectCodes(projects,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}

	
	@Test
	public  void validationProjectCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateProjectCode(project.code,contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationProjectCodeRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateProjectCode(null,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public  void validationProjectCodeNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateProjectCode("notexist",contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	
	@Test
	public  void validationSampleCodesTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> samples=new ArrayList<String>();
		samples.add(sample.code);
		samples.add(sample1.code);
		CommonValidationHelper.validateSampleCodes(samples,contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationSampleCodesRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> samples=new ArrayList<String>();
		CommonValidationHelper.validateSampleCodes(samples,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public  void validationSampleCodesNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		List<String> samples=new ArrayList<String>();
		samples.add("notexist");
		CommonValidationHelper.validateSampleCodes(samples,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}


	@Test
	public  void validationSampleCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		Logger.debug("sample2.code=" + sample2.code);
		Logger.debug("sample2.projectCodes.get(0)=" + sample2.projectCodes.get(0));
		CommonValidationHelper.validateSampleCode(sample2.code, sample2.projectCodes.get(0), contextValidation );
		Logger.debug(contextValidation.errors.toString());
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationSampleCodeNotRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateSampleCode(null,null, contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}
	
	@Test
	public  void validationSampleCodeNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateSampleCode("notexist","notexist", contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}



	
	@Test
	public  void validationStockCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode(stock.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}


	@Test
	public  void validationStockNotRequiredCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationStockNotExistCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}


	@Test
	public  void validationContainerCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateContainerCode(container.code,contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationContainerCodeRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateContainerCode(null,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public  void validationContainerCodeNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateContainerCode("notexist",contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	

	
	@Test
	public  void validationReagentInstanceCodeTest(){
		ContextValidation contextValidation=new ContextValidation();
		InstanceValidationHelper.validationReagentInstanceCode(reagentInstance.code,contextValidation );
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public  void validationReagentInstanceCodeRequiredTest(){
		ContextValidation contextValidation=new ContextValidation();
		InstanceValidationHelper.validationReagentInstanceCode(null,contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public  void validationReagentCodeNotExistTest(){
		ContextValidation contextValidation=new ContextValidation();
		InstanceValidationHelper.validationReagentInstanceCode("notexist",contextValidation );
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}

	
	
	
	
}
