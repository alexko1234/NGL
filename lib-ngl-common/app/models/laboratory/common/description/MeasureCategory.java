package models.laboratory.common.description;

import java.util.List;

public class MeasureCategory{

	public Long id;
	
	public String name;
	
	public String code;
	
	public List<MeasureValue> measurePossibleValues;

	public MeasureCategory() {
		super();
	}

	public MeasureCategory(String name, String code,
			List<MeasureValue> measurePossibleValues) {
		super();
		this.name = name;
		this.code = code;
		this.measurePossibleValues = measurePossibleValues;
	}

	
}
