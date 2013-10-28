package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.*;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public class CommonInfoTypeService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		

		updateStates(errors);	
	}


	/**
	 * @author : dnoisett
	 * @param errors
	 * @throws DAOException
	 */
	public static void updateStates(Map<String, List<ValidationError>> errors) throws DAOException {
		List<CommonInfoType> l = new ArrayList<CommonInfoType>();
		//TODO : update the list (just one value for test here)
		l.add(DescriptionFactory.setStatesToCommonInfoType(CommonInfoType.find.findByCode("RHS2000").code, DescriptionFactory.getStates("F", "E","IW-V", "IP-V", "F-V", "IP-S", "IP-RG", "F-RG")));


		DAOHelpers.updateModels(CommonInfoType.class, l, errors);
	}
	

	
}
