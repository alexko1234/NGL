package services.description.sample;

import java.util.List;
import java.util.Map;

import models.utils.dao.DAOException;
// import play.Logger;
import play.data.validation.ValidationError;
import static fr.cea.ig.play.IGGlobals.configuration;

public class ImportService {
	
	private static final play.Logger.ALogger logger = play.Logger.of(ImportService.class);
	
	public static void main(Map<String,List<ValidationError>> errors) throws DAOException {
		// String institute=play.Play.application().configuration().getString("institute");
		String institute = configuration().getString("institute");
		if (institute.equals("CNS")) {
			new ImportServiceCNS().main(errors);
		} else if(institute.equals("CNG")) {
			new ImportServiceCNG().main(errors);
		} else {
			logger.error("You need to specify only one institute ! Now, it's {}", institute); // + play.Play.application().configuration().getString("institute"));
		}

	}

}
