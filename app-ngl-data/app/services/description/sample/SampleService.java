package services.description.sample;

import java.util.List;
import java.util.Map;

import models.utils.dao.DAOException;
import play.Logger;
import play.data.validation.ValidationError;

public class SampleService {

	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
		String institute=play.Play.application().configuration().getString("institute");
		if (institute.equals("CNS")) {
			(new SampleServiceCNS()).main(errors);
		} else if(institute.equals("CNG")) {
			(new SampleServiceCNG()).main(errors);
		} else if(institute.equals("TEST")) {
			(new SampleServiceTEST()).main(errors);
		} else {
			Logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
		}
	}
	
}
