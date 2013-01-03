
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import models.description.common.CommonInfoType;
import models.description.common.ObjectType;
import models.description.common.PropertyDefinition;
import models.description.experiment.ExperimentType;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.With;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");
		insertDefaultDataIfNecessary();

  }
  @Override
  public Action onRequest(Request request, Method actionMethod) {
	  
	  //call CAS module
	  return new play.modules.cas.CasAuthentication();
  }
  
  @Override
  public  play.api.mvc.Handler	onRouteRequest(Http.RequestHeader request) {
		return super.onRouteRequest(request);
  }
  
  @Override
  public void onStop(Application app) {
    Logger.info("NGL shutdown...");
  }

	private void insertDefaultDataIfNecessary() {

		if(ExperimentType.find.where().eq("commonInfoType.code", "TEST").findIds().isEmpty()) {
			ExperimentType et = new ExperimentType();
			et.commonInfoType = new CommonInfoType();
			et.commonInfoType.code = "TEST";
			et.commonInfoType.name = "TEST";
			et.commonInfoType.collectionName = "testExp";
			et.commonInfoType.propertiesDefinition = new ArrayList<PropertyDefinition>();
			et.commonInfoType.objectType = ObjectType.find.byId(Long.valueOf("1")); 
			PropertyDefinition pd = new PropertyDefinition();
			pd.active =true;
			pd.choiceInList = false;
			pd.code="P1";
			pd.name="P1";
			pd.required = false;
			pd.type = String.class.getName();

			et.commonInfoType.propertiesDefinition.add(pd);
			//et.save();
		}

	}
}