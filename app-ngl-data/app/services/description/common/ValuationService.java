
package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public class ValuationService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(Valuation.class, Valuation.find);

		saveValuation(errors);	
	}


	/**
	 * 
	 * @param errors
	 * @throws DAOException
	 */
	public static void saveValuation(Map<String, List<ValidationError>> errors) throws DAOException {
		List<Valuation> l = new ArrayList<Valuation>();
		
		//NGL-BI 
		//TODO : change values (values are here just for test the service!!)
		l.add(DescriptionFactory.newValuation("Default", "criteria-default",  "specDefaultCriteriaRunCNGforSAVQualityControl_1" ));
		l.add(DescriptionFactory.newValuation("High", "criteria-high",  "specDefaultCriteriaRunCNGforSAVQualityControl_2" ));
		l.add(DescriptionFactory.newValuation("Low", "criteria-low",  "specDefaultCriteriaRunCNGforSAVQualityControl_3" ));
		
		DAOHelpers.saveModels(Valuation.class, l, errors);
	}

	

}
