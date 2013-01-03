package models.instance.common;


/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
public class PropertyValue {
	
	public PropertyValue() {
		super();
	}
	public PropertyValue(Object value) {
		super();
		this.value = value;
	}
	public PropertyValue(Object value, String unit) {
		super();
		this.value = value;
		this.unit = unit;
	}
	public Object value;
	public String unit;
	@Override
	public String toString() {
		return "PropertyValue [value=" + value + ", unit=" + unit + "]";
	}
	

}
