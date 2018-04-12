package services.instance.sample;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.Constants;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import validation.ContextValidation;

public class UpdateSampleCNS extends UpdateSamplePropertiesCNS{

	@Inject
	public UpdateSampleCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("UpdateSample", durationFromStart, durationFromNextIteration, ctx);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		updateSampleFromTara(contextError, null);

	}

	public void updateSampleFromTara(ContextValidation contextError,
			List<String> sampleCodes) throws SQLException, DAOException {

		List<String> results=limsServices.findSampleUpdated(sampleCodes);

		for(String sampleCode:results){
			Sample sample=limsServices.findSampleToCreate(contextError, sampleCode);
			ContextValidation contextValidation = new ContextValidation(Constants.NGL_DATA_USER);
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
//			Sample newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextValidation,true);
			Sample newSample = InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextValidation,true);
			if(!contextValidation.hasErrors()){
				limsServices.updateMaterielLims(newSample, contextError);
				/*
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
				SampleHelper.updateSampleReferenceCollab(sample,contextError);
				*/
				
				super.updateOneSample(newSample, contextError);
				
			}else {
				contextError.errors.putAll(contextValidation.errors);
			}
		}



	}




}
