package models.laboratory.run.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.utils.InstanceHelpers;

public class File {
	
	//concatenation de flotseqname + flotseqext	
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	
	public Map<String, PropertyValue> properties= InstanceHelpers.getLazyMapPropertyValue();
	
	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON	
	*/

}
