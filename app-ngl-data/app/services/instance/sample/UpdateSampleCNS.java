package services.instance.sample;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constants;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateSampleCNS extends AbstractImportDataCNS{

	public UpdateSampleCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSample", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		updateSampleFromTara(contextError, null);

	}

	public static void updateSampleFromTara(ContextValidation contextError,
			List<String> sampleCodes) throws SQLException, DAOException {

		List<String> results=limsServices.findSampleUpdated(sampleCodes);

		for(String sampleCode:results){
			Sample sample=limsServices.findSampleToCreate(contextError, sampleCode);
			ContextValidation contextValidation = new ContextValidation(Constants.NGL_DATA_USER);
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			Sample newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextValidation,true);
			if(!contextValidation.hasErrors()){
				limsServices.updateMaterielLims(newSample, contextError);

				SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
				ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

				Map<String, PropertyValue> properties=new HashMap<String, PropertyValue>();

				if(importType !=null){
					InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), newSample.properties,properties);
				}
				if(sampleType !=null){
					InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), newSample.properties,properties);
				}

				SampleHelper.updateSampleProperties(sampleCode, properties,contextError);

			}else {
				contextError.errors.putAll(contextValidation.errors);
			}
		}



	}




}
