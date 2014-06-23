package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;



public class FileValidationHelper extends CommonValidationHelper {
	
	
	public static void validationFiles(List<File> files, ContextValidation contextValidation) {
		if((null != files) && (files.size() > 0)) {
			int index = 0;
			List<String> lstFullName = new ArrayList<String>();
			for (File file : files) {
				contextValidation.addKeyToRootKeyName("files[" + index + "]");
				if (!lstFullName.contains(file.fullname)) {
					file.validate(contextValidation);
					lstFullName.add(file.fullname);
				}
				else { contextValidation.addErrors("fullname", ValidationConstants.ERROR_NOTUNIQUE_MSG, file.fullname); }
				contextValidation.removeKeyFromRootKeyName("files[" + index + "]");
				index++;
			}
		}
	}
	
	
	private static ReadSet getReadSetFromContext(ContextValidation contextValidation) {
		return getObjectFromContext("readSet", ReadSet.class, contextValidation);
	}
	

	public static void validateFileFullName(String fullname, ContextValidation contextValidation) {
		ReadSet readSet = getReadSetFromContext(contextValidation);
		if(ValidationHelper.required(contextValidation, fullname, "fullname")) {
			//Validate unique file.code if not already exists
			if(contextValidation.isCreationMode() && MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", fullname)))){
				contextValidation.addErrors("fullname",ValidationConstants.ERROR_NOTUNIQUE_MSG, fullname);
			}else if(contextValidation.isUpdateMode() && !MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", fullname)))){
				contextValidation.addErrors("fullname",ValidationConstants.ERROR_NOTEXISTS_MSG, fullname);
			}
		}
		
	}

	public static void validateFileProperties(Map<String, PropertyValue> properties,	ContextValidation contextValidation) {
		ReadSet readSet = getReadSetFromContext(contextValidation);
		try {
			ReadSetType readSetType = ReadSetType.find.findByCode(readSet.typeCode);
			if(null != readSetType){
				contextValidation.addKeyToRootKeyName("properties");
				ValidationHelper.validateProperties(contextValidation, properties, readSetType.getPropertyDefinitionByLevel(Level.CODE.File), true);
				contextValidation.removeKeyFromRootKeyName("properties");
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}		
	}
}
