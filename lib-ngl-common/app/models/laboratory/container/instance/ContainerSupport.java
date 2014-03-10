package models.laboratory.container.instance;

import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.SupportValidationHelper;
import fr.cea.ig.DBObject;

public class ContainerSupport extends DBObject implements IValidation{
	public String categoryCode;
	public State state;
	public String stockCode;
	public Valuation valuation;
	public TraceInformation traceInformation;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		SupportValidationHelper.validateCode(this, InstanceConstants.SUPPORT_COLL_NAME, contextValidation);
		SupportValidationHelper.validateSupportCategoryCode(categoryCode, contextValidation);
	}
}
