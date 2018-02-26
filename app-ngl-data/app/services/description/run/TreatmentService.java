package services.description.run;

import static fr.cea.ig.play.IGGlobals.configuration;

import java.util.List;
import java.util.Map;

import models.utils.dao.DAOException;
// import play.Logger;
import play.data.validation.ValidationError;

public class TreatmentService {
	
//	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
//		String institute=play.Play.application().configuration().getString("institute");
//		if (institute.equals("CNS")){
//			(new TreatmentServiceCNS()).main(errors);
//		}else if(institute.equals("CNG")){
//			(new TreatmentServiceCNG()).main(errors);
//		}else{
//			Logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
//		}
//	}
	
	private static final play.Logger.ALogger logger = play.Logger.of(TreatmentService.class);
	
	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
		String institute = configuration().getString("institute");
		switch (institute) {
		case "CNS" : new TreatmentServiceCNS().main(errors); break;
		case "CNG" : new TreatmentServiceCNG().main(errors); break;
		default    : logger.error("You need to specify only one institute ! Now, it's {}", institute);
		}
	}
	
}

