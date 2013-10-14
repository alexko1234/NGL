package services.description.common;

import static services.description.DescriptionFactory.newObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
public class ObjectTypeService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		saveObjectTypes(errors);		
	}
	public static void saveObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		for (ObjectType.CODE code : ObjectType.CODE.values()) {
			l.add(newObjectType(code.name(), code.name()));
		}
		DAOHelpers.saveModels(ObjectType.class, l, errors);
	}
}
