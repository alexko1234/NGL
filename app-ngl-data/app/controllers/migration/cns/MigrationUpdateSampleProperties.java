package controllers.migration.cns;

import models.LimsCNSDAO;
import models.utils.InstanceConstants;
import play.Logger;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import services.instance.sample.UpdateSamplePropertiesCNS;
import validation.ContextValidation;
import controllers.CommonController;

public class MigrationUpdateSampleProperties  extends CommonController{

	protected static ALogger logger=Logger.of("MigrationUpdateSampleProperties");

	public static Result migration() {
		ContextValidation contextError=new ContextValidation("ngl-sq");
		UpdateSamplePropertiesCNS.updateSampleModifySince(-20, contextError);
		return ok("Migration update sample Finish");
	}
}
