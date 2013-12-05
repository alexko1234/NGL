
package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public class ValidationCriteriaService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(ValidationCriteria.class, ValidationCriteria.find);

		saveValidationCriteria(errors);	
	}


	/**
	 * 
	 * @param errors
	 * @throws DAOException
	 */
	public static void saveValidationCriteria(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ValidationCriteria> l = new ArrayList<ValidationCriteria>();
		
		//NGL-BI 
		//TODO : change values (values are here just for test the service!!)
		l.add(DescriptionFactory.newValidationCriteria("Default", "criteria-default",  "specDefaultCriteriaRunCNGforSAVQualityControl_1" ));
		l.add(DescriptionFactory.newValidationCriteria("High", "criteria-high",  "specDefaultCriteriaRunCNGforSAVQualityControl_2" ));
		l.add(DescriptionFactory.newValidationCriteria("Low", "criteria-low",  "specDefaultCriteriaRunCNGforSAVQualityControl_3" ));
		
		DAOHelpers.saveModels(ValidationCriteria.class, l, errors);
	}

	

}
