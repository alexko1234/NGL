package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import play.data.validation.Constraints.Required;

import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class ReadSet extends DBObject implements IValidation{

	public String typeCode;
	public String stateCode;
	public List<String> resolutionCode;
	
	public String runCode;
	public Integer laneNumber;
	public Boolean dispatch;
	public String sampleContainerCode; //code bar de la banque ou est l'echantillon
	public String sampleCode; //nom de l'ind / ech
	public String projectCode;
	public TBoolean validProduction = TBoolean.UNSET;
    public Date validProductionDate;
    public TBoolean validBioinformatic = TBoolean.UNSET;
    public Date validBioinformaticDate;
    
	public String path;
	public String archiveId;
	public Date archiveDate;
	public TraceInformation traceInformation;
	public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	
	public List<File> files;
	
	/*
	 * for "archives" optimization purpose (query links)
	*/
	

	/*
	indexSequence 			tag lié au ls
	nbRead 					nombre de read de sequencage du ls
	???						ssid du ls (archivage)
	???						date d'archivage du ls
	nbClusterInternalFilter	nombre de clusters passant les filtres du ls
	nbBaseInternalFilter	nombre de bases correspondant au clusters passant les filtres du ls
	fraction				fraction de run du ls
	insertLength			id de la taille d'insert
	nbUsefulBase				nombre de bases utiles ls
	nbUsefulCluster			nombre de clusters utiles passant les filtres du ls
	q30 					q30 du ls
	score					score qualite moyen du ls
	 */

	@Override
	public void validate(ContextValidation contextValidation) {
		InstanceValidationHelper.validateId(this, contextValidation);
		InstanceValidationHelper.validateCode(this, InstanceConstants.READSET_ILLUMINA_COLL_NAME, contextValidation);
		
		if(contextValidation.isUpdateMode() && !checkReadSetInRun()){
				contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, this.code);
		}			
				
		if(ValidationHelper.required(contextValidation, this.stateCode, "stateCode")){
			if(!RunPropertyDefinitionHelper.getReadSetStateCodes().contains(this.stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.stateCode);
			}
		}
		
		DescriptionValidationHelper.validationReadSetTypeCode(this.typeCode, contextValidation);
		
		InstanceValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, this.runCode, "runCode",  Run.class, InstanceConstants.RUN_ILLUMINA_COLL_NAME);
		
		if(ValidationHelper.required(contextValidation, this.runCode, "runCode") && 
				ValidationHelper.required(contextValidation, this.laneNumber, "laneNumber")){
			if(!isLaneExist(contextValidation)){
				contextValidation.addErrors("runCode",ValidationConstants.ERROR_NOTEXISTS_MSG, this.runCode);
				contextValidation.addErrors("laneNumber",ValidationConstants.ERROR_NOTEXISTS_MSG, this.laneNumber);
			}
		}
		
		if(ValidationHelper.required(contextValidation, this.projectCode, "projectCode")){
			//TODO validate if exist projectCode
		}
		if(ValidationHelper.required(contextValidation, this.sampleCode, "sampleCode")){
			//TODO validate if exist sampleCode
		}
		if(ValidationHelper.required(contextValidation, this.sampleContainerCode, "sampleContainerCode")){
			//TODO validate if exist sampleContainerCode
		}
		ValidationHelper.required(contextValidation, this.path, "path");

		
		contextValidation.addKeyToRootKeyName("properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getReadSetPropertyDefinitions());
		contextValidation.removeKeyFromRootKeyName("properties");
		
		contextValidation.putObject("readSet", this);
		contextValidation.putObject("level", Level.CODE.ReadSet);
		InstanceValidationHelper.validationTreatments(this.treatments, contextValidation);
		InstanceValidationHelper.validationFiles(this.files, contextValidation);
	}


	private boolean checkReadSetInRun() {
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(
						DBQuery.is("code", this.runCode), 
						DBQuery.elemMatch("lanes", 
							DBQuery.and(
								DBQuery.is("number", this.laneNumber),
								DBQuery.is("readSetCodes", this.code)))));
	}
	
	private boolean isLaneExist(ContextValidation contextValidation) {		
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", this.runCode), DBQuery.is("lanes.number", this.laneNumber)));
		
	}
}