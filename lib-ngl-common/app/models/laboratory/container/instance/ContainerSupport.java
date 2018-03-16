package models.laboratory.container.instance;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerSupportValidationHelper;
import validation.utils.ValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;

// This link : {@link models.laboratory.container.instance.ContainerSupport}

/**
 * Name of the collection is {@link models.utils.InstanceConstants#CONTAINER_SUPPORT_COLL_NAME}.
 * 
 * @author vrd
 *
 */
public class ContainerSupport extends DBObject implements IValidation {
	
	/**
	 * Category code (type of container support) ({@link models.laboratory.container.description ContainerSupportCategory}).
	 */
	public String categoryCode;
	
	public State state;
	
	public String storageCode;
	public Valuation valuation; //TODO GA Must be disappear ???
	
	/**
	 * Access trace.
	 */
	public TraceInformation traceInformation;
	
	public Set<String> projectCodes;
	public Set<String> sampleCodes;
	public Set<String> fromTransformationTypeCodes; //TODO GA useful ???
	public Map<String, PropertyValue> properties;
	public Integer nbContainers;
	public Integer nbContents;
	
	/**
	 * Comments.
	 */
	public List<Comment> comments;
	
	public List<StorageHistory> storages;
	
	public ContainerSupport() {
		projectCodes= new HashSet<>();
		sampleCodes= new HashSet<>();
		fromTransformationTypeCodes= new HashSet<>();
		valuation = new Valuation();
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		if (contextValidation.getObject(FIELD_STATE_CODE) == null) {
			contextValidation.putObject(FIELD_STATE_CODE , state.code);			
		}
		ContainerSupportValidationHelper.validateId(this, contextValidation);
		ContainerSupportValidationHelper.validateCode(this, InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, contextValidation);
		CommonValidationHelper.validateState(ObjectType.CODE.Container, state, contextValidation);
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(categoryCode, contextValidation);
		ContainerSupportValidationHelper.validateProjectCodes(projectCodes, contextValidation);
		ContainerSupportValidationHelper.validateSampleCodes(sampleCodes, contextValidation);
		ContainerSupportValidationHelper.validateExperimentTypeCodes(fromTransformationTypeCodes, contextValidation);
		
		ValidationHelper.required(contextValidation, nbContainers, "nbContainers");
		ValidationHelper.required(contextValidation, nbContents, "nbContents");
		
		//TODO Validate properties
	}
}
