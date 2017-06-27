package models.laboratory.reception.instance;

import static validation.utils.ValidationConstants.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import play.Logger;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.parameter.index.Index;
import models.laboratory.parameter.Parameter;// TEST
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.utils.InstanceConstants;
import validation.ContextValidation;

public class TagExcelFieldConfiguration extends AbstractFieldConfiguration {

	
	public String headerValue;
	public Integer cellSequence;
	public Integer cellCode;
	
	public Boolean tagCategory = Boolean.FALSE;
	
	public TagExcelFieldConfiguration() {
		super(AbstractFieldConfiguration.tagExcelType);		
	}

	@Override
	public void populateField(Field field, Object dbObject, Map<Integer, String> rowMap,
			ContextValidation contextValidation, Action action) throws Exception {

		if(rowMap.containsKey(cellSequence) && rowMap.containsKey(cellCode)){
			String sequence = rowMap.get(cellSequence);
			String code = rowMap.get(cellCode);
			
			//Get Index
			Index index = getIndex(contextValidation, sequence, code);
			if(null != index && !tagCategory.booleanValue()){
				populateField(field, dbObject, index.code);
			}else if(null != index && tagCategory.booleanValue()){
				populateField(field, dbObject, index.categoryCode);
			}
					
		} else if(rowMap.containsKey(cellSequence)){
			String sequence = rowMap.get(cellSequence);
			
			Index index = getIndex(contextValidation, sequence, null);
			if(null != index && !tagCategory.booleanValue()){
				populateField(field, dbObject, index.code);
			}else if(null != index && tagCategory.booleanValue()){
				populateField(field, dbObject, index.categoryCode);
			}
		} else if(required){
			contextValidation.addErrors(headerValue, ERROR_REQUIRED_MSG);
		}
	}

	private Index getIndex(ContextValidation contextValidation, String sequence, String code) {
		String additionalErrInfo="'"+sequence+"'/'"+code+"'";
		Logger.debug("DEBUG FDS : "+additionalErrInfo);
		

		DBQuery.Query q = DBQuery.in("typeCode", "index-illumina-sequencing","index-nanopore-sequencing").is("sequence", sequence);
		if(null != code){
			q.is("code", code);
		}
		
		Index index = null; 
		List<Index> indexes= MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, Index.class, q).toList();
		
		if(indexes.size() == 1){
			index = indexes.get(0);
		}else if(indexes.size() == 0){
			contextValidation.addErrors(headerValue, ERROR_NOTEXISTS_MSG, additionalErrInfo);
		}else{
			contextValidation.addErrors(headerValue, ERROR_SEVERAL_RESULT_MSG, additionalErrInfo);
		}
		return index;
	}

	
	
}
