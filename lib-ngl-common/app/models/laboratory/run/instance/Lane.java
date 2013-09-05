package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import validation.InstanceValidationHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.utils.IValidation;
import models.utils.InstanceConstants;

public class Lane implements IValidation{
	
	public Integer number;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;
	public List<ReadSet> readsets;
	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	
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
		
		Run run = (Run) contextValidation.getObject("run");
		
		if(ValidationHelper.required(contextValidation, this.number, "number")) { 
			
			//Validate unique lane.number if run already exist
			if(null != run._id ){
				Run runExist = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", run.code).is("lanes.number", this.number));
				//TODO : update case not managed
				if (runExist != null) {
					contextValidation.addErrors("number",ValidationConstants.ERROR_NOTUNIQUE, this.number);
				}
			}
		}
		
		contextValidation.addKeyToRootKeyName("properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getLanePropertyDefinitions(), "");
		contextValidation.removeKeyFromRootKeyName("properties");
		
		contextValidation.putObject("lane", this);
		InstanceValidationHelper.validationReadSets(this.readsets, contextValidation);

	}
}
