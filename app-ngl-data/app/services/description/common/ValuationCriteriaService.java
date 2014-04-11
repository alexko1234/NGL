
package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public class ValuationCriteriaService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(ValuationCriteria.class, ValuationCriteria.find);

		//saveValuationCriteria(errors);	
	}


	/**
	 * 
	 * @param errors
	 * @throws DAOException
	 */
	public static void saveValuationCriteria(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ValuationCriteria> l = new ArrayList<ValuationCriteria>();
		
		//NGL-BI 
		//TODO : change values (values are here just for test the service!!)
		l.add(DescriptionFactory.newValuationCriteria("Default", "criteria-default",  "specDefaultCriteriaRunCNGforSAVQualityControl_1",DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newValuationCriteria("High", "criteria-high",  "specDefaultCriteriaRunCNGforSAVQualityControl_2",DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newValuationCriteria("Low", "criteria-low",  "specDefaultCriteriaRunCNGforSAVQualityControl_3",DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		
		DAOHelpers.saveModels(ValuationCriteria.class, l, errors);
	}

	

}
