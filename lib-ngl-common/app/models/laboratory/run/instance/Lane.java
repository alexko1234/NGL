package models.laboratory.run.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import validation.DescriptionValidationHelper;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.utils.InstanceConstants;

public class Lane implements IValidation{

	public Integer number;
	public String stateCode;
	public List<String> resolutionCode;
    public TBoolean valid = TBoolean.UNSET;
    public Date validDate;
    

	//public List<ReadSet> readsets;
	// dnoisett, the lane doesn't contain the entire readset anymore, just a code to refer it;
	public List<String> readSetCodes;

	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
	
	/*
	nbCycleRead1
	nbCycleReadIndex1
	nbCycleRead2
	nbCycleReadIndex2
	nbCluster
	nbClusterInternalFilter 		nombre de clusters passant les filtres
	percentClusterInternalFilter 	pourcentage de clusters passant les filtres
	nbClusterIlluminaFilter 		nombre de clusters passant le filtre illumina
	percentClusterIlluminaFilter 	pourcentage de clusters passant le filtre illumina
	nbClusterTotal 					nombre de clusters
	nbBaseInternalFilter			nombre de bases total des sequences passant les filtres
	nbTiles 						nombre de tiles
	phasing
	prephasing
	 */

	@Override
	public void validate(ContextValidation contextValidation) {

		if (ValidationHelper.required(contextValidation, this.number, "number")) {
			//Validate unique lane.number if run already exist
			if (contextValidation.isCreationMode() && isLaneExist(contextValidation)) {
					contextValidation.addErrors("number",ValidationConstants.ERROR_NOTUNIQUE_MSG, this.number);
			}else if(contextValidation.isUpdateMode() && !isLaneExist(contextValidation)){
				contextValidation.addErrors("number",ValidationConstants.ERROR_NOTEXISTS_MSG, this.number);				
			}
			
			
		}

		if(this.readSetCodes != null && this.readSetCodes.size() > 0){
			List<String> readSetCodesTreat = new ArrayList<String>();
			for(int i=0; i< this.readSetCodes.size(); i++){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, this.readSetCodes.get(i));
				if(null == readSet || !number.equals(readSet.laneNumber)){
					contextValidation.addErrors("readSetCodes["+i+"]",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG,  this.readSetCodes.get(i), "ReadSet");
				}
				
				if(readSetCodesTreat.contains(this.readSetCodes.get(i))){
					contextValidation.addErrors("readSetCodes["+i+"]",ValidationConstants.ERROR_CODE_DOUBLE_MSG,  this.readSetCodes.get(i));
				}
				readSetCodesTreat.add(this.readSetCodes.get(i));
			}
		}
		
		
		if(ValidationHelper.required(contextValidation, this.stateCode, "stateCode")){
			if(!RunPropertyDefinitionHelper.getRunStateCodes().contains(this.stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.stateCode);
			}
		}
		
		contextValidation.putObject("lane", this);
		contextValidation.putObject("level", Level.CODE.Lane);
		InstanceValidationHelper.validationTreatments(this.treatments, contextValidation);
		
		
		contextValidation.addKeyToRootKeyName("properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getLanePropertyDefinitions());
		contextValidation.removeKeyFromRootKeyName("properties");



	}

	private boolean isLaneExist(ContextValidation contextValidation) {
		Run run = (Run) contextValidation.getObject("run");
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", run.code), DBQuery.is("lanes.number", this.number)));
		
	}
}
