package models.instance.run;

import java.util.Map;

import play.data.validation.Constraints.Required;

import models.instance.Utils;
import models.instance.common.PropertyValue;

public class File {
	
	//concaténation de flotseqname + flotseqext	
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	
	public Map<String, PropertyValue> properties= Utils.getLazyMapPropertyValue();
	
	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON	
	*/

}
