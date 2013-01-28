package models.laboratory.common.description;

public class MeasureValue {

	public Long id;
	
	public String value;   
	
	public Boolean defaultValue = Boolean.FALSE;
  	
	public Long measureCateroryId;

	
	
	public MeasureValue() {
		super();
	}



	public MeasureValue(String value, Boolean defaultValue) {
		super();
		this.value = value;
		this.defaultValue = defaultValue;
	}

	
}
