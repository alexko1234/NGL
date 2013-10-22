package validation.container;

import static org.fest.assertions.Assertions.assertThat;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Helpers;
import utils.AbstractTests;
import validation.ContextValidation;
import validation.container.instance.ContainerSupportValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ContainerSupportValidationHelperTest extends AbstractTests {
	static Container container;
	static Project project;
	static Stock stock;
	
	
	@BeforeClass
	public static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		app = getFakeApplication();
		Helpers.start(app);
		initData();
	}

	@AfterClass
	public static void endTest(){
		app = getFakeApplication();
		deleteData();
		Helpers.stop(app);
	}


	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		stock=saveDBOject(Stock.class, InstanceConstants.STOCK_COLL_NAME, "stock");
		
		container=new Container();
		container.categoryCode=ContainerCategory.find.findAll().get(0).code;
		container.support=ContainerSupportHelper.getContainerSupport(ContainerSupportCategory.find.findAll().get(0).code, 1, "test", "1", "1");

	}

	private static void deleteData() {
		MongoDBDAO.getCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class).drop();
		MongoDBDAO.getCollection(InstanceConstants.STOCK_COLL_NAME,Stock.class).drop();

	}
	
	
	/**
	 *  BarCode / Line / Column unique position in creation
	 */
	
	@Test
	public void validateUniqueBarCodePositionCode() {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		//Container n'a pas encore ete serialize
		ContainerSupportValidationHelper.validateUniqueBarCodePosition(container.support, contextValidation);
		container=MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,container);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateUniqueBarCodePositionCodeExist() {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		//Container est dans la base
		ContainerSupportValidationHelper.validateUniqueBarCodePosition(container.support, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}
	
	@Test
	public void validateUniqueBarCodePositionCodeNotCreationMode() {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setUpdateMode();
		ContainerSupportValidationHelper.validateUniqueBarCodePosition(container.support, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
		contextValidation.setDeleteMode();
		ContainerSupportValidationHelper.validateUniqueBarCodePosition(container.support, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	
	
	/**
	 *  Container support category 
	 * @throws DAOException 
	 */

	@Test
	public void validateContainerSupportCategoryCode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(ContainerSupportCategory.find.findAll().get(0).code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateContainerSupportCategoryCodeRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	@Test
	public void validateContainerSupportCategoryCodeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}	
	
	/**
	 *  Stock 
	 */

	@Test
	public void validateStockCode() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode(stock.code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateStockCodeNotRequired() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}

	@Test
	public void validateStockCodeNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		ContainerSupportValidationHelper.validateStockCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}		


}
