package models.instance;

import java.util.HashMap;
import java.util.Map;

import models.instance.common.PropertyValue;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;

public class Utils {
	
	@SuppressWarnings("unchecked")
	public static Map<String, PropertyValue> getLazyMapPropertyValue() {
		return MapUtils.lazyMap(new HashMap<String, PropertyValue>(), new Transformer() {
		     public PropertyValue transform(Object mapKey) {
		    	 //todo comment je sais quel est le type on doit mettre
		    	 return new PropertyValue();
		     }
		 });
	}

}
