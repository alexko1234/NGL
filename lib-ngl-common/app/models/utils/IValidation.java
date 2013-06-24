package models.utils;

import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

public interface IValidation {
	
	public void validate(Map<String,List<ValidationError>> errors);

}
