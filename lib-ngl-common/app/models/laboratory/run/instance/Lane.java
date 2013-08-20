package models.laboratory.run.instance;

import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import net.vz.mongodb.jackson.DBQuery;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import validation.utils.ValidationConstants;
import models.utils.InstanceHelpers;
import validation.InstanceValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class Lane implements IValidation {
	
	public Integer number;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;
	public List<ReadSet> readsets;
	public Map<String, PropertyValue> properties = InstanceHelpers.getLazyMapPropertyValue();
	
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
		
		Run run = (Run) contextValidation.contextObjects.get("run");
		
		if(required(contextValidation.errors, this.number, getKey(contextValidation.rootKeyName,"number"))){
			//Validate unique lane.number if run already exist
			if(null != run._id ){
				Run runExist = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", run.code).is("lanes.number", this.number));
				//TODO : update case not managed
				if (runExist != null) {
					ConstraintsHelper.addErrors(contextValidation.errors,getKey(contextValidation.rootKeyName,"code"),ValidationConstants.ERROR_NOTUNIQUE,this.number);
				}
			}
		}
		
		String rootKeyNameProp = getKey(contextValidation.rootKeyName,"properties");
		ConstraintsHelper.validateProperties(contextValidation.errors, this.properties, RunPropertyDefinitionHelper.getLanePropertyDefinitions(), rootKeyNameProp);
		
		contextValidation.contextObjects.put("lane", this);
		InstanceValidationHelper.validationReadSets(this.readsets, contextValidation);

	}
}
