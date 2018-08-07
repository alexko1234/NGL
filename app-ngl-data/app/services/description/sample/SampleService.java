package services.description.sample;

import static fr.cea.ig.play.IGGlobals.configuration;

import java.util.List;
import java.util.Map;

import models.utils.dao.DAOException;
// import play.Logger;
import play.data.validation.ValidationError;

public class SampleService {

	private static final play.Logger.ALogger logger = play.Logger.of(SampleService.class);
	
//<<<<<<< HEAD
//	public static void main(Map<String,List<ValidationError>> errors) throws DAOException{
//		(new SampleServiceGET()).main(errors);
//=======
	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
		String institute = configuration().getString("institute");
		switch (institute) {
		case "CNS"  : new SampleServiceCNS().main(errors);  break;
		case "CNG"  : new SampleServiceCNG().main(errors);  break;
		case "GET"  : new SampleServiceGET().main(errors);  break;
		case "TEST" : new SampleServiceTEST().main(errors); break;
		default     : logger.error("You need to specify only one institute ! Now, it's {}", institute);
		}
//>>>>>>> V2.0.2
	}
	
//	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
//		String institute=configuration().getString("institute");
//		if (institute.equals("CNS")) {
//			(new SampleServiceCNS()).main(errors);
//		} else if(institute.equals("CNG")) {
//			(new SampleServiceCNG()).main(errors);
//		} else if(institute.equals("TEST")) {
//			(new SampleServiceTEST()).main(errors);
//		} else {
//			Logger.error("You need to specify only one institute ! Now, it's "+ configuration().getString("institute"));
//		}
//	}
	
}
