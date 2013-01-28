package models.laboratory.common.description;

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

	
	
	public PropertyDefinition() {
		super();
	}

	public PropertyDefinition(String code, String name, String description,
			Boolean required, Boolean active, Boolean choiceInList,
			String type, String displayFormat, Integer displayOrder,
			String level, boolean propagation, String inOut,
			List<Value> possibleValues, String defaultValue,
			MeasureCategory measureCategory, MeasureValue measureValue) {
		super();
		this.code = code;
		this.name = name;
		this.description = description;
		this.required = required;
		this.active = active;
		this.choiceInList = choiceInList;
		this.type = type;
		this.displayFormat = displayFormat;
		this.displayOrder = displayOrder;
		this.level = level;
		this.propagation = propagation;
		this.inOut = inOut;
		this.possibleValues = possibleValues;
		this.defaultValue = defaultValue;
		this.measureCategory = measureCategory;
		this.measureValue = measureValue;
	}

	public static Map<String,String> options() {
		return options;
	}

	public static Map<String,String> optionsLevel() {
		return optionsLevel;
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
