package models.laboratory.common.description;

import java.util.List;

public class MeasureCategory{

	public Long id;
	
	public String name;
	
	public String code;
	
	public List<MeasureValue> measurePossibleValues;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<MeasureValue> getMeasurePossibleValues() {
		return measurePossibleValues;
	}

	public void setMeasurePossibleValues(List<MeasureValue> measurePossibleValues) {
		this.measurePossibleValues = measurePossibleValues;
	}
	
	
}
