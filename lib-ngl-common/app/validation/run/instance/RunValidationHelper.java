package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;



public class RunValidationHelper extends CommonValidationHelper {
		
	public static void validateRunInstrumentUsed(InstrumentUsed instrumentUsed, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
			contextValidation.addKeyToRootKeyName("instrumentUsed");
			instrumentUsed.validate(contextValidation); 
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
		}
	}

	public static void validateRunType(String typeCode,	Map<String, PropertyValue> properties,	ContextValidation contextValidation) {
		RunType runType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", RunType.find,true);
		if(null != runType){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, runType.getPropertyDefinitionByLevel(Level.CODE.Run), true);
			contextValidation.removeKeyFromRootKeyName("properties");
		}		
	}
	
	
	
	public static void validateRunProjectCodes(List<String> projectCodes, ContextValidation contextValidation) {
		if(projectCodes != null && projectCodes.size() > 0){
			List<String> lProjectCodes = new ArrayList<String>();
			for(int i=0; i< projectCodes.size(); i++){
				
				if (! MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class,  DBQuery.is("code", projectCodes.get(i)))) {
					contextValidation.addErrors("projectCodes["+i+"]", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, projectCodes.get(i), "Run");
				}
				
				//Validation + advanced : projectCode for the run is a projectCode for at least one of his readsets ?
				//if (this.state.equals("F")) ///run finished
				//MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode",this.code),DBQuery.is("projectCode", projectCodes.get(i)))); 
				
				if(lProjectCodes.contains(projectCodes.get(i))){
					contextValidation.addErrors("projectCodes["+i+"]",ValidationConstants.ERROR_CODE_DOUBLE_MSG,  projectCodes.get(i));
				}
				lProjectCodes.add(projectCodes.get(i));
			}
		}	
	}

	public static void validateRunSampleCodes(List<String> sampleCodes, ContextValidation contextValidation) {
		if(sampleCodes != null && sampleCodes.size() > 0){
			List<String> lSampleCodes = new ArrayList<String>();
			for(int i=0; i< sampleCodes.size(); i++){

				if (! MongoDBDAO.checkObjectExist(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,  DBQuery.is("code", sampleCodes.get(i)))) {
					contextValidation.addErrors("sampleCodes["+i+"]", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, sampleCodes.get(i), "Run");
				}
				
				//Validation + advanced : sampleCode for the run is a sampleCode for at least one of his readsets ?
				//if (this.state.equals("F")) ///run finished
				//MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode",this.code),DBQuery.is("sampleCode", sampleCodes.get(i))));
				
				if(lSampleCodes.contains(sampleCodes.get(i))){
					contextValidation.addErrors("sampleCodes["+i+"]",ValidationConstants.ERROR_CODE_DOUBLE_MSG,  sampleCodes.get(i));
				}
				lSampleCodes.add(sampleCodes.get(i));
			}
		}	
		
	}

}
