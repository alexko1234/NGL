package controllers.migration;

import java.util.List;

import models.LimsCNSDAO;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class MigrationDataCNS extends CommonController{
	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	protected static ALogger logger=Logger.of("Migration");

	public static Result updateSequencingProgramType() throws DAOException {

		
		Logger.info(">>>>>>>>>>> Migration ContainerSupport starts");
		ContextValidation contextValidation=new ContextValidation();
		String sql="select name=case when (select count(*) from Lecturedepot l where lectco=2  and l.matmaco= d.matmaco )=0 then 'SR' else 'PE' end, code=re.lotrearef " +
				"from  Depotsolexa d, Prepaflowcell p, Relationmaterielmanip rm, Runhd r, Lotreactif re, Materielmanip m  where m.matmaco=d.matmaco and rm.matmacop=p.matmaco and rm.matmacof=d.matmaco and p.lotreaco=re.lotreaco and   d.matmaco=r.matmaco and r.runhInNGL!=null";

		//unset sequencingProgramType in all ContainerSupport
		MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.exists("code"), DBUpdate.unset("properties.sequencingProgramType"),true);

		List<ListObject> results= limsServices.getListObjectFromProcedureLims(sql);
		
		for(ListObject obj:results){
			
			if(MongoDBDAO.checkObjectExist(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,"code", obj.code)){
				MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", obj.code)
						,DBUpdate.set("properties.sequencingProgramType",new PropertySingleValue(obj.name)));
			}else {
				contextValidation.addErrors("containerSupport.code", "error.codeNotExist", obj.code);
			}

		}

		//unset sequencingProgramType in Container
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("code"), DBUpdate.unset("properties.sequencingProgramType"),true);
		
		contextValidation.displayErrors(logger);
		Logger.info(">>>>>>>>>>> Migration ContainerSupport end");

		if(contextValidation.hasErrors()){
			return badRequest("Errors");
		}else 
			return ok("Migration ok");

	}
}
