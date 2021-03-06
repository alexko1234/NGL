package models.laboratory.common.instance.property;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;
import static fr.cea.ig.lfw.utils.Iterables.first;

import java.io.IOException;
import java.util.Collection;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

import com.google.common.io.Files;


/**
 * Used to stock a File
 * 
 * 
 */
public class PropertyFileValue extends PropertyByteValue {
	
	public String fullname;
	public String extension;
	
	public PropertyFileValue() {
		super(PropertyValue.fileType);
	}
	
	public PropertyFileValue(java.io.File value) throws IOException {
		super(PropertyValue.fileType, Files.toByteArray(value));
		this.extension = Files.getFileExtension(value.getName());
		this.fullname = value.getName();
	}
	
	@Override
	public String toString() {
		return "PropertyFileValue [name=" + fullname + ", ext=" + extension + ", class=" + value.getClass().getName() + "]";
	}
		
	@Override
	public void validate(ContextValidation contextValidation) {
//		@SuppressWarnings("unchecked") // Uncheckable access to validation context object 
//		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		PropertyDefinition propertyDefinition = first(contextValidation.<Collection<PropertyDefinition>>getTypedObject("propertyDefinitions")).orElse(null);
		super.validate(contextValidation);
		ValidationHelper.required(contextValidation, this.fullname,  propertyDefinition.code + ".fullname");
		ValidationHelper.required(contextValidation, this.extension, propertyDefinition.code + ".extension");
	}

	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
//		result = prime * result + ((fullname == null) ? 0 : fullname.hashCode());
//		return result;
		return hash(super.hashCode(), extension, fullname);
	}

	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		PropertyFileValue other = (PropertyFileValue) obj;
//		if (extension == null) {
//			if (other.extension != null)
//				return false;
//		} else if (!extension.equals(other.extension))
//			return false;
//		if (fullname == null) {
//			if (other.fullname != null)
//				return false;
//		} else if (!fullname.equals(other.fullname))
//			return false;
//		return true;
		return typedEquals(PropertyFileValue.class, this, obj,
				           (x,y) -> super.equals(obj) 
				                    && objectEquals(x.extension, y.extension) 
				                    && objectEquals(x.fullname, y.fullname));
	}

}
