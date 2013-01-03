package controllers.admin.types;

import java.util.List;

import factory.ControllerTypeFactory;

import models.description.common.CommonInfoType;
import models.description.experiment.ExperimentType;
import models.description.experiment.InstrumentUsedType;
import play.data.Form;
import play.mvc.Result;
import views.html.admin.types.experimentTypes;

/**
 * Specific controller for CRUD operation on experiment type
 * All type controller must inherit GenericTypes and implements IGenericCreateTypes
 * @author ejacoby
 *
 */
public class ExperimentTypes extends GenericTypes implements IGenericCreateTypes{

	public final static Form<ExperimentType> experimentTypeForm = form(ExperimentType.class);
	
	public static Result home() {
		return ok(experimentTypes.render(commonInfoTypeForm, experimentTypeForm,true));
	}
	
	/**
	 * Service to create new type or update 
	 * 
	 * @param id : id of type retrieve from the view
	 * @param version : version of type retrieve from the view
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Result createOrUpdate(String id, String version)
	{
		Form<ExperimentType> form = experimentTypeForm.bindFromRequest();
		ExperimentType bean = form.get();
		if(id!=null && version!=null && !id.equals("null") && !version.equals("null")){
			bean.id=Long.valueOf(id);
			bean.version=Long.valueOf(version);
		}
		
		CommonInfoType beanCIT =GenericTypes.getBeanFromRequest(ControllerTypeFactory.typeExperiment);
		
		bean.nextExperimentTypes = (List<ExperimentType>) GenericTypes.getListTypeFromDB(bean.nextExperimentTypes);
		bean.instrumentTypes = (List<InstrumentUsedType>) GenericTypes.getListTypeFromDB(bean.instrumentTypes);
		bean.commonInfoType=beanCIT;
		
		if(bean.id==null)
			bean.save();
		else{
			//TODO ne marche pas
			bean.update();
		}
		
		return ok(experimentTypes.render(commonInfoTypeForm, experimentTypeForm,true));
	}

	@Override
	public Result add() {
		Form<CommonInfoType> defaultForm = commonInfoTypeForm.fill(new CommonInfoType());
		return ok(experimentTypes.render(defaultForm, experimentTypeForm,true));
	}
	
	@Override
	public Result show(long idCommonInfoType)
	{
		ExperimentType expType = ExperimentType.findByCommonInfoType(idCommonInfoType);
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(expType.commonInfoType);
		Form<ExperimentType> filledFormET = experimentTypeForm.fill(expType);
		return ok(experimentTypes.render(filledFormCIT, filledFormET,false));
	}
	
	@Override
	public Result edit(long idCommonInfoType){
		ExperimentType expType = ExperimentType.findByCommonInfoType(idCommonInfoType);
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(expType.commonInfoType);
		Form<ExperimentType> filledFormET = experimentTypeForm.fill(expType);
		return ok(experimentTypes.render(filledFormCIT, filledFormET,true));
	}
	
}
