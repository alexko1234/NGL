package models.laboratory.run.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import validation.ContextValidation;
import validation.IValidation;
import validation.run.instance.AnalysisValidationHelper;
import validation.run.instance.FileValidationHelper;
import validation.run.instance.ReadSetValidationHelper;
import validation.run.instance.TreatmentValidationHelper;
import validation.utils.ValidationHelper;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;

public class Analysis extends DBObject implements IValidation{

    public String typeCode;
	public State state;
	public Valuation valuation = new Valuation();
	public TraceInformation traceInformation;
	
	public List<String> masterReadSetCodes;
	public List<String> readSetCodes;
	public List<String> projectCodes = new ArrayList<String>();
    public List<String> sampleCodes = new ArrayList<String>();
	
	public String path;
	public List<File> files;
	public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	
	@Override
	public void validate(ContextValidation contextValidation) {
		AnalysisValidationHelper.validateId(this, contextValidation);
		AnalysisValidationHelper.validateCode(this, InstanceConstants.ANALYSIS_COLL_NAME, contextValidation);
		AnalysisValidationHelper.validateAnalysisType(this.typeCode, this.properties, contextValidation);
		AnalysisValidationHelper.validateState(this.typeCode, this.state, contextValidation);
		AnalysisValidationHelper.validateValuation(this.typeCode, this.valuation, contextValidation);
		AnalysisValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		
		AnalysisValidationHelper.validateReadSetCodes(this, contextValidation);
		AnalysisValidationHelper.validateProjectCodes(this.projectCodes, contextValidation);
		AnalysisValidationHelper.validateSampleCodes(this.sampleCodes,contextValidation);
		
		ValidationHelper.required(contextValidation, this.path, "path");
		contextValidation.putObject("analysis", this);
		contextValidation.putObject("objectClass", this.getClass());
		contextValidation.putObject("level", Level.CODE.Analysis);
		TreatmentValidationHelper.validationTreatments(this.treatments, contextValidation);
		FileValidationHelper.validationFiles(this.files, contextValidation);
		
	}
}
