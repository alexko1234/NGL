package models.instance.container;

import java.util.Map;

import models.instance.common.PropertyValue;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult {
	
	public String QualityControleCode;
	public Map<String,PropertyValue> properties;
	

}
