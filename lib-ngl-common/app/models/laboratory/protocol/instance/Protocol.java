package models.laboratory.protocol.instance;

import java.util.ArrayList;
import java.util.List;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.protocol.instance.ProtocolValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;

public class Protocol extends DBObject implements IValidation{
	
	public String name;
	public String filePath;
	public String version;	
	public String categoryCode;
	public List<String> experimentTypeCodes;
	
	public Protocol() {		
		super();
		this.experimentTypeCodes = new ArrayList<String>();
	}
	
	public Protocol(String code, String name, String filePath, String version, String categoryCode) {
		this();
		this.code = code;
		this.name = name;
		this.filePath = filePath;
		this.version = version;
		this.categoryCode = categoryCode;
		
	}
	
	

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		ProtocolValidationHelper.validateId(this,contextValidation);
		ProtocolValidationHelper.validateCode(this,InstanceConstants.PROTOCOL_COLL_NAME, contextValidation);
		ProtocolValidationHelper.validateExperimentTypeCodes(experimentTypeCodes, contextValidation);
		ProtocolValidationHelper.validateProtocolCategoryCode(this.categoryCode, contextValidation);
		// filePath ( required ??), version (required ??)
		
	}
	
	
}
