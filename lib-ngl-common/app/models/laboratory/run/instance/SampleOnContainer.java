package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;

/**
 * Class used to stock information from sample on the last container
 * @author galbini
 *
 */
public class SampleOnContainer {
	
	//last update date
	public Date lastUpdateDate;
	// Reference Sample code
	public String sampleCode;
	// Reference SampleType code
	public String sampleTypeCode;
	// Reference SampleCategory code
	public String sampleCategoryCode;
	// Reference to the container support
	public String containerSupportCode;
	// Reference to the container
	public String containerCode;
	// Properties of the content in the container
	public Map<String,PropertyValue> properties;
	//Percentage of content on the container
	public Double percentage;
	//Collaborator's reference
	public String referenceCollab;	




	@Override
	public String toString() {
		return "SampleOnContainer [lastUpdateDate=" + lastUpdateDate
				+ ", sampleCode=" + sampleCode + ", sampleTypeCode="
				+ sampleTypeCode + ", sampleCategoryCode=" + sampleCategoryCode
				+ ", containerSupportCode=" + containerSupportCode
				+ ", containerCode=" + containerCode + ", referenceCollab="+ referenceCollab + "]";
	}

}
