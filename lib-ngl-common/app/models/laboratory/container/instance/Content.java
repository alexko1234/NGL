package models.laboratory.container.instance;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContentValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO: comment

/**
 * {@link models.laboratory.container.instance.Container} content.
 * Container contains a number of embedded Content {@link models.laboratory.container.instance.Container#contents}.
 *  
 * @author vrd
 *
 */
public class Content implements IValidation {

	// Embedded Sample information
	
	/**
	 * Code the contained sample ({@link models.laboratory.sample.instance.Sample#code}).
	 */
	public String sampleCode;
	
	/**
	 * Type code of the contained sample ({@link models.laboratory.sample.instance.Sample#typeCode}).
	 */
	public String sampleTypeCode;
	
	/**
	 * Category code of the contained sample ({@link models.laboratory.sample.instance.Sample#categoryCode}).
	 */
	public String sampleCategoryCode;
	
	/**
	 * The content is defined for a given project.
	 */
	public String projectCode;
	
	/**
	 * Percentage of this content in the container.
	 */
	public Double percentage;
	
	/**
	 * Sample collaborator ({@link models.laboratory.sample.instance.Sample#referenceCollab}).
	 */
	public String referenceCollab;
	
	/**
	 * Taxonomy code of the contained sample ({@link models.laboratory.sample.instance.Sample#taxonCode}).
	 */
	public String taxonCode;
	
	/**
	 * Taxonomy scientific name ({@link models.laboratory.sample.instance.Sample#ncbiScientificName}).
	 */
	public String ncbiScientificName;

	// TODO: use PropertyValue<?>
	public Map<String,PropertyValue> properties;

	/* Put process properties to analyse container*/
	//public String processTypeCode;
	//public String processCode;
	public Map<String,PropertyValue> processProperties;
	
	public List<Comment> processComments;

	public Content() {
		properties = new HashMap<>(); // String, PropertyValue>();		
	}

	@JsonIgnore
	public Content(String sampleCode, String typeCode, String categoryCode) {
		this.sampleCode         = sampleCode;
		this.sampleTypeCode     = typeCode;
		this.sampleCategoryCode = categoryCode;
		this.properties         = new HashMap<>(); // <String, PropertyValue>();		
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		ContentValidationHelper.validateSampleCode(sampleCode, contextValidation);
		ContentValidationHelper.validateProjectCode(projectCode, contextValidation);
		ContentValidationHelper.validateSampleCodeWithProjectCode(projectCode, sampleCode, contextValidation);
		ContentValidationHelper.validateSampleCategoryCode(sampleCategoryCode,contextValidation);
		ContentValidationHelper.validateSampleTypeCode(sampleTypeCode,contextValidation);
		ContentValidationHelper.validatePercentageContent(percentage, contextValidation);
		ContentValidationHelper.validateProperties(sampleTypeCode, properties, contextValidation);
	}

	@Override
	public Content clone() {
		Content finalContent = new Content();

		finalContent.projectCode        = projectCode;
		finalContent.sampleCode         = sampleCode;
		finalContent.sampleCategoryCode = sampleCategoryCode;
		finalContent.sampleTypeCode     = sampleTypeCode;
		finalContent.referenceCollab    = referenceCollab;
		finalContent.percentage         = percentage;
		if (properties != null)
			finalContent.properties = new HashMap<>(properties); // new HashMap<String,PropertyValue>(properties);
		finalContent.taxonCode          = taxonCode;
		finalContent.ncbiScientificName = ncbiScientificName;
		if(processProperties != null)
			finalContent.processProperties = new HashMap<>(processProperties); //new HashMap<String,PropertyValue>(processProperties);
		finalContent.processComments    = processComments;
		return finalContent;
	}
	
}
