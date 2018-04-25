package services.instance.container;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import models.LimsCNSDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.sample.instance.Sample;
import models.util.DataMappingCNS;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;

public class UpdateTaraPropertiesCNS extends AbstractImportDataCNS{

	@Inject
	public UpdateTaraPropertiesCNS(
			FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, 
			NGLContext ctx) {
		super("UpdateTara", durationFromStart, durationFromNextIteration, ctx);
	
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
			RulesException {
		updateSampleFromTara(contextError, null);
	}
	
	public static void updateSampleFromTara(ContextValidation contextError, List<String> limsCodes) throws SQLException, DAOException{
		
		List<Map<String, PropertyValue>> taraPropertyList = taraServices.findTaraSampleUpdated(limsCodes);
	
		//Logger.debug("Nb Map Tara"+taraPropertyList.size());
		for (Map<String,PropertyValue> taraProperties : taraPropertyList) {
	
			if(!taraProperties.containsKey(LimsCNSDAO.LIMS_CODE)){
				contextError.addErrors(LimsCNSDAO.LIMS_CODE,"error.codeNotExist","");
			}else {
				Integer limsCode=Integer.valueOf(taraProperties.get(LimsCNSDAO.LIMS_CODE).value.toString());
				Logger.debug("Tara lims Code :"+limsCode);
				
				List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("properties.limsCode.value",limsCode)).toList();
	
				if(samples.size()==1 ){
					Sample sample =samples.get(0);
	
					Boolean adaptater;
					if(sample.properties.get("isAdapters")==null){
						adaptater=false;
					}else {
						adaptater=(Boolean) sample.properties.get("isAdapters").value;
					}
					
					String importTypeCode=DataMappingCNS.getImportTypeCode(true,adaptater);
					
					/*NEW ALGO*/
					sample.properties.putAll(taraProperties);
					sample.importTypeCode = importTypeCode;
					sample.traceInformation.setTraceInformation("ngl-data");
					contextError.setUpdateMode();
					sample.validate(contextError);
					if(!contextError.hasErrors()){
						
						MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, sample);
					}
					//OLD ALGO
					//taraProperties.remove(LimsCNSDAO.LIMS_CODE);
					//ValidationHelper.validateProperties(contextError,taraProperties, ImportType.find.findByCode(importTypeCode).getPropertyDefinitionByLevel(Level.CODE.Content));
					/*
					if(!importTypeCode.equals(sample.importTypeCode)){
						MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sample.code),DBUpdate.set("importTypeCode",importTypeCode));
					}
					
					SampleHelper.updateSampleProperties(sample.code,taraProperties,contextError);
					*/
				}
			}
	
	
		}
	
	}

}
