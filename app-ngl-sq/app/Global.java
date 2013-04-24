
import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Request;

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

		/*if(ExperimentType.findByCode("TEST")==null) {
			ExperimentType et = new ExperimentType();
		
			et.code = "TEST";
			et.name = "TEST";
			et.collectionName = "testExp";
			et.propertiesDefinition = new ArrayList<PropertyDefinition>();
			et.objectType = ObjectType.findById(Long.valueOf("1")); 
			PropertyDefinition pd = new PropertyDefinition();
			pd.active =true;
			pd.code="P1";
			pd.name="P1";
			pd.required = false;
			pd.type = String.class.getName();

			et.propertiesDefinition.add(pd);
			//et.save();
		}*/

	}
}