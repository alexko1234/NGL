package models.laboratory.parameter;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;

@JsonTypeInfo(use=Id.NAME, include=As.EXISTING_PROPERTY, property="typeCode", defaultImpl=models.laboratory.parameter.index.Index.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.parameter.index.IlluminaIndex.class, name = "index-illumina-sequencing"),
	@JsonSubTypes.Type(value =  models.laboratory.parameter.index.NanoporeIndex.class, name = "index-nanopore-sequencing"),
	@JsonSubTypes.Type(value =  models.laboratory.parameter.printer.BBP11.class, name = "BBP11"),
	@JsonSubTypes.Type(value =  models.laboratory.parameter.map.MapParameter.class, name = "map-parameter"),
	@JsonSubTypes.Type(value =  models.laboratory.parameter.context.ContextDescription.class, name = "context-description"),

})
public abstract class Parameter extends DBObject  implements IValidation{
	
	public String typeCode;
	public TraceInformation traceInformation;
	public String categoryCode;
	public String name;
	
	protected Parameter(String typeCode) {
		super();
		this.typeCode = typeCode;
	}
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		CommonValidationHelper.validateCode(this, InstanceConstants.PARAMETER_COLL_NAME, contextValidation);
		ValidationHelper.required(contextValidation, categoryCode, "categoryCode");
		ValidationHelper.required(contextValidation, name, "name");
		
	}
}
