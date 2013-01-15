package models.description.common;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import play.data.validation.Constraints.Required;

public class PropertyDefinition {

	private static LinkedHashMap<String,String> options;
	{
		options = new LinkedHashMap<String,String>();
		options.put(String.class.getName(), "String");
		options.put(Integer.class.getName(), "Integer");        
		options.put(Long.class.getName(), "Long");
		options.put(Float.class.getName(), "Float");
		options.put(Double.class.getName(), "Double");
		options.put(Date.class.getName(), "Date");	
		options.put(Boolean.class.getName(), "Boolean");	

	}

	private static LinkedHashMap<String,String> optionsLevel;
	{
		optionsLevel = new LinkedHashMap<String,String>();
		optionsLevel.put("current", "current");
		optionsLevel.put("content", "content");        
		optionsLevel.put("containing", "containing");   
	}

	public Long id;

	@Required
	public String code;

	@Required	
	public String name;

	public String description;

	public Boolean required = Boolean.FALSE;

	public Boolean active = Boolean.TRUE;
	public Boolean choiceInList = Boolean.FALSE;
	
	@Required	
	public String type;
	public String displayFormat;
	public Integer displayOrder;

	public String level;
	public boolean propagation;
	public String inOut;


	public List<Value> possibleValues;

	public String defaultValue;

	public MeasureCategory measureCategory;

	public MeasureValue measureValue;

	public static Map<String,String> options() {
		return options;
	}

	public static Map<String,String> optionsLevel() {
		return optionsLevel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<Value> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(List<Value> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public MeasureCategory getMeasureCategory() {
		return measureCategory;
	}

	public void setMeasureCategory(MeasureCategory measureCategory) {
		this.measureCategory = measureCategory;
	}

	public MeasureValue getMeasureValue() {
		return measureValue;
	}

	public void setMeasureValue(MeasureValue measureValue) {
		this.measureValue = measureValue;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean getPropagation() {
		return propagation;
	}

	public void setPropagation(boolean propagation) {
		this.propagation = propagation;
	}

	public String getInOut() {
		return inOut;
	}

	public void setInOut(String inOut) {
		this.inOut = inOut;
	}

	
	public Boolean getChoiceInList() {
		return choiceInList;
	}

	public void setChoiceInList(Boolean choiceInList) {
		this.choiceInList = choiceInList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyDefinition other = (PropertyDefinition) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}


}
