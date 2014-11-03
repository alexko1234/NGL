package validation.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentQueryParams;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfigurations;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.cea.ig.MongoDBDAO;

import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;


public class ExperimentValidationHelperTest extends AbstractTests {

	static ExperimentType experimentType;
	static Protocol protocol;
	static List<String> resolutionList;
	static Instrument instrument;
	static InstrumentUsedType instrumentUsedType;
	
	@BeforeClass
	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		experimentType=ExperimentType.find.findByCategoryCode("transformation").get(0);
		
		protocol=new Protocol();
		protocol=Protocol.find.findByExperimentTypeCode(experimentType.code).get(0);

		List<String> experimentTypes = new ArrayList<String>();
		experimentTypes.add(experimentType.code);		
		List<ResolutionConfigurations> resolutionConfigurations = MongoDBDAO.find(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, DBQuery.in("typeCodes", experimentTypes)).toList();
		resolutionList=new ArrayList<String>();
		for (ResolutionConfigurations rc : resolutionConfigurations) {
			for(Resolution reso: rc.resolutions) {
				resolutionList.add(reso.code);
			}
		}
		instrumentUsedType=InstrumentUsedType.find.findByExperimentTypeCode(experimentType.code).get(0);
		InstrumentQueryParams instrumentsQueryParams=new InstrumentQueryParams();
		instrumentsQueryParams.typeCode=instrumentUsedType.code;
		instrument=Instrument.find.findByQueryParams(instrumentsQueryParams).get(0);
	}

	@AfterClass
	public static void deleteData() {
	}
	
	@Test
	public void validationProtocolFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validationProtocol(experimentType.code,protocol.code, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==0);
	}
	
	@Test
	public void validationProtocolNullFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validationProtocol(experimentType.code,null, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==1);
	}
	
	@Test
	public void validationProtocolNotExperimentTypeFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validationProtocol("test",protocol.code, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==1);
	}

	@Test
	public void validationProtocolNewTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "N");
		ExperimentValidationHelper.validationProtocol(experimentType.code,null, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==0);
	}
	
	
	@Test
	public void validationResolutionFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validateResolutionCodes(experimentType.code,resolutionList, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==0);
	}
	
	@Test
	public void validationResolutionNullFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validateResolutionCodes(experimentType.code,null, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==1);
	}
	
	@Test
	public void validationResolutionNotExperimentTypeFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		ExperimentValidationHelper.validateResolutionCodes("test",resolutionList, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==resolutionList.size());
	}

	@Test
	public void validationResolutionNewTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "N");
		ExperimentValidationHelper.validateResolutionCodes(experimentType.code,null, contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==0);
	}
			
	//@Test
	public void validationInstrumentUsedFinishTest() throws DAOException{
		ContextValidation contextValidation=new ContextValidation(Constants.TEST_USER);
		contextValidation.getContextObjects().put("stateCode", "F");
		Assert.assertTrue(contextValidation.errors.size()==0);
	}
	
	
	//@Test
	public	void validationExperimentType(String typeCode, Map<String, PropertyValue> properties, ContextValidation contextValidation){

	}
	//@Test
	public	void validationExperimentCategoryCode(String categoryCode, ContextValidation contextValidation){

	}
	//@Test
	public void	validateState(Experiment experiment, State state, ContextValidation contextValidation){

	}
	//@Test
	public void validateNewState(Experiment experiment , ContextValidation contextValidation){

	}

}
