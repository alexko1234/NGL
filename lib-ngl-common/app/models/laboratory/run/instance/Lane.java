package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Validation;
import validation.ContextValidation;
import validation.IValidation;
import validation.run.instance.LaneValidationHelper;
import validation.run.instance.TreatmentValidationHelper;

public class Lane implements IValidation{

	public Integer number;
	public String stateCode;
	public List<String> resolutionCode;
	
	public Validation validation;
	
	@JsonIgnore
    public TBoolean valid = TBoolean.UNSET;
	@JsonIgnore
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
		LaneValidationHelper.validationLaneNumber(this.number,contextValidation);
		LaneValidationHelper.validationLaneReadSetCodes(this.number, this.readSetCodes, contextValidation);
		LaneValidationHelper.validateLaneStateCode(this.stateCode, contextValidation);
		contextValidation.putObject("lane", this);
		contextValidation.putObject("level", Level.CODE.Lane);
		TreatmentValidationHelper.validationTreatments(this.treatments, contextValidation);
		LaneValidationHelper.validationLaneProperties(this.properties, contextValidation);		
	}

	
}
