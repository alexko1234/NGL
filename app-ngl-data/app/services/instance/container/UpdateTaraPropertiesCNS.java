package services.instance.container;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.LimsCNSDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.util.DataMappingCNS;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateTaraPropertiesCNS extends AbstractImportDataCNS{

	public UpdateTaraPropertiesCNS(
			FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateTara", durationFromStart, durationFromNextIteration);
	
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
			RulesException {
		updateSampleFromTara(contextError, null);
	}
	
	public static void updateSampleFromTara(ContextValidation contextError, List<String> limsCodes) throws SQLException, DAOException{
		
		List<Map<String, PropertyValue>> taraPropertyList = taraServices.findTaraSampleUpdated(limsCodes);
	
		//Logger.debug("Nb Map Tara"+taraPropertyList.size());
		for(Map<String,PropertyValue> taraProperties : taraPropertyList){
	
			Integer limsCode=Integer.valueOf(taraProperties.get(LimsCNSDAO.LIMS_CODE).value.toString());
			//Logger.debug("Tara lims Code :"+limsCode);
			if(!taraProperties.containsKey(LimsCNSDAO.LIMS_CODE)){
				contextError.addErrors(LimsCNSDAO.LIMS_CODE,"error.codeNotExist","");
			}else {
	
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
					
					if(!importTypeCode.equals(sample.importTypeCode)){
						MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sample.code),DBUpdate.set("importTypeCode",sample.importTypeCode));
					}

					SampleHelper.updateSampleProperties(sample.code,taraProperties);
					
				}
			}
	
	
		}
	
	}

}
