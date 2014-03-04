package models.dao.mapping;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Institute;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentQueryParams;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.utils.dao.DAOException;

import org.junit.Test;

import utils.AbstractTests;

public class FindDAOTest extends AbstractTests {
	@Test
	public void CommonInfoTypeFindTest() throws DAOException {
		CommonInfoType type = CommonInfoType.find.findAll().get(0);
		Assert.assertNotNull(type);
		CommonInfoType cType = CommonInfoType.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		CommonInfoType cTypeId = CommonInfoType.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(CommonInfoType.find.isCodeExist(""));
	}

	@Test
	public void ContainerSupportCategoryFindTest() throws DAOException {
		ContainerSupportCategory type = ContainerSupportCategory.find.findAll()
				.get(0);
		Assert.assertNotNull(type);
		ContainerSupportCategory cType = ContainerSupportCategory.find
				.findByCode(type.code);
		Assert.assertNotNull(cType);
		ContainerSupportCategory cTypeId = ContainerSupportCategory.find
				.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(ContainerSupportCategory.find.isCodeExist(""));
		Assert.assertNotNull(ContainerSupportCategory.find.findByContainerCategoryCode(""));

	}

	@Test
	public void ExperimentTypeNodeFindTest() throws DAOException {
		ExperimentTypeNode type = ExperimentTypeNode.find.findAll().get(0);
		Assert.assertNotNull(type);
		ExperimentTypeNode cType = ExperimentTypeNode.find
				.findByCode(type.code);
		Assert.assertNotNull(cType);
		ExperimentTypeNode cTypeId = ExperimentTypeNode.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(ExperimentTypeNode.find.isCodeExist(""));

	}

	@Test
	public void InstituteFindTest() throws DAOException {
		Institute type = Institute.find.findAll().get(0);
		Assert.assertNotNull(type);
		Institute cType = Institute.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		Institute cTypeId = Institute.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(Institute.find.isCodeExist(""));
	}

	@Test
	public void MeasureUnitFindTest() throws DAOException {
		MeasureUnit type = MeasureUnit.find.findAll().get(0);
		Assert.assertNotNull(type);
		MeasureUnit cType = MeasureUnit.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		MeasureUnit cTypeId = MeasureUnit.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(MeasureUnit.find.isCodeExist(""));
	}

	@Test
	public void ObjectTypeFindTest() throws DAOException {
		ObjectType type = ObjectType.find.findAll().get(0);
		Assert.assertNotNull(type);
		ObjectType cType = ObjectType.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		ObjectType cTypeId = ObjectType.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(ObjectType.find.isCodeExist(""));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void PropertyDefinitionFindTest() throws DAOException {
		PropertyDefinition type = PropertyDefinition.find.findAll().get(0);
		PropertyDefinition cType = PropertyDefinition.find
				.findByCode(type.code);
		PropertyDefinition cTypeId = PropertyDefinition.find.findById(type.id);
	}

	@Test
	public void ProtocolFindTest() throws DAOException {
		Protocol type = Protocol.find.findAll().get(0);
		Assert.assertNotNull(type);
		Protocol cType = Protocol.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		Protocol cTypeId = Protocol.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(Protocol.find.isCodeExist(""));
	}

	@Test
	public void ResolutionFindTest() throws DAOException {
		Resolution type = Resolution.find.findAll().get(0);
		Assert.assertNotNull(type);
		Resolution cType = Resolution.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		Resolution cTypeId = Resolution.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(Resolution.find.isCodeExist(""));
	}

	@Test
	public void StateFindTest() throws DAOException {
		State type = State.find.findAll().get(0);
		Assert.assertNotNull(type);
		State cType = State.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		State cTypeId = State.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(State.find.isCodeExist(""));
		Assert.assertNotNull(State.find.findAllForContainerList());
		Assert.assertNotNull(State.find.findByCategoryCode(""));
		Assert.assertNotNull(State.find.findByObjectTypeCode(ObjectType.CODE.Sample));
		Assert.assertNotNull(State.find.findByTypeCode(""));
		Assert.assertFalse(State.find.isCodeExistForTypeCode("",""));
	}
	
	@Test
	public void InstrumentUsedTypeTest() throws DAOException {
		InstrumentUsedType type = InstrumentUsedType.find.findAll().get(0);
		Assert.assertNotNull(type);
		InstrumentUsedType cType = InstrumentUsedType.find.findByCode(type.code);
		Assert.assertNotNull(cType);
		InstrumentUsedType cTypeId = InstrumentUsedType.find.findById(type.id);
		Assert.assertNotNull(cTypeId);
		Assert.assertFalse(InstrumentUsedType.find.isCodeExist(""));
		Assert.assertNotNull(InstrumentUsedType.find.findByExperimentTypeCode(""));		
	}
	
	@Test
	public void InstrumentTest() throws DAOException {
		InstrumentQueryParams instrumentQuery = new InstrumentQueryParams();
		instrumentQuery.typeCode = "ARGUS";
		List<Instrument> intruments = Instrument.find.findByQueryParams(instrumentQuery);
		Assert.assertNotNull(intruments);
		
		instrumentQuery = new InstrumentQueryParams();
		instrumentQuery.typeCodes = new ArrayList();
		instrumentQuery.typeCodes.add("ARGUS");
		intruments = Instrument.find.findByQueryParams(instrumentQuery);
		Assert.assertNotNull(intruments);
		
		instrumentQuery = new InstrumentQueryParams();
		instrumentQuery.categoryCodes = new ArrayList();
		instrumentQuery.categoryCodes.add("covaris");
		intruments = Instrument.find.findByQueryParams(instrumentQuery);
		Assert.assertNotNull(intruments);
		
		instrumentQuery = new InstrumentQueryParams();
		instrumentQuery.typeCode = "ARGUS";
		instrumentQuery.categoryCode = "opt-map-opgen";
		intruments = Instrument.find.findByQueryParams(instrumentQuery);
		
		instrumentQuery = new InstrumentQueryParams();
		instrumentQuery.typeCodes =new ArrayList();
		instrumentQuery.typeCodes.add("ARGUS");
		instrumentQuery.categoryCodes = new ArrayList();
		instrumentQuery.categoryCodes.add("opt-map-opgen");
		intruments = Instrument.find.findByQueryParams(instrumentQuery);
		Assert.assertNotNull(intruments);
	}

}
