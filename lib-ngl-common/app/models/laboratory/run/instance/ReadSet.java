package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import play.data.validation.Constraints.Required;
import validation.InstanceValidationHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import static validation.utils.ValidationHelper.*;

public class ReadSet implements IValidation{

	

	@Required
	public String code;
	@Required
	public String sampleContainerCode; //code bar de la banque ou est l'echantillon
	@Required
	public String sampleCode; //nom de l'ind / ech
	@Required
	public String projectCode;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;	
	@Required
	public String path;	
	public String archiveId;
	public Date archiveDate;
	@Valid
	public List<File> files;
	
	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	
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
		
		if(required(contextValidation.errors, this.code, getKey(contextValidation.rootKeyName,"code"))){
			
			Lane lane = (Lane) contextValidation.getObject("lane");
			Run run = (Run) contextValidation.getObject("run");
			
			//Validate unique readSet.code if not already exist
			Run runExist = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", this.code));
			
			if(runExist != null && run._id == null){ //when new run 
				addErrors(contextValidation.errors, getKey(contextValidation.rootKeyName,"code"),ValidationConstants.ERROR_NOTUNIQUE, this.code);
				
			} else if(runExist != null && run._id != null) { //when run exist
				if(!runExist.code.equals(run.code) || !runExist._id.equals(run._id)) {
					addErrors(contextValidation.errors, getKey(contextValidation.rootKeyName,"code"), ValidationConstants.ERROR_NOTUNIQUE, this.code);
				}else if(lane.number != -1){
					for(Lane l:run.lanes){
						if(l.readsets!=null){ 
							for(ReadSet r: l.readsets){
								if(r.code.equals(this.code)){
									if(l.number != lane.number){
										addErrors(contextValidation.errors,getKey(contextValidation.rootKeyName,"code"), ValidationConstants.ERROR_NOTUNIQUE, this.code);
										break;
									}
								}
							}
						}
					}
				}
			}
			
		}
		if(required(contextValidation.errors, this.projectCode, getKey(contextValidation.rootKeyName,"projectCode"))){
			//TODO validate if exist readSet.projectCode
		}
		if(required(contextValidation.errors, this.sampleCode, getKey(contextValidation.rootKeyName,"sampleCode"))){
			//TODO validate if exist
		}
		if(required(contextValidation.errors, this.sampleContainerCode, getKey(contextValidation.rootKeyName,"sampleContainerCode"))){
			//TODO validate if exist
		}
		required(contextValidation.errors, this.path, getKey(contextValidation.rootKeyName,"path"));
		
		String rootKeyNameProp = getKey(contextValidation.rootKeyName,"properties");
		validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getReadSetPropertyDefinitions(), rootKeyNameProp);
		
		contextValidation.putObject("readset", this);
		InstanceValidationHelper.validationFiles(this.files, contextValidation);
		
	}
}
