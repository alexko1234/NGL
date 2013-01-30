package controllers.admin.types;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.InstrumentUsedType;
import play.data.Form;
import play.mvc.Result;
import views.html.admin.types.experimentTypes;
import factory.ControllerTypeFactory;


/**
 * Specific controller for CRUD operation on experiment type
 * All type controller must inherit GenericTypes and implements IGenericCreateTypes
 * @author ejacoby
 *
 */
public class ExperimentTypes extends GenericTypes implements IGenericCreateTypes{

	public static final Form<ExperimentType> experimentTypeForm = form(ExperimentType.class);
	
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
	public static Result createOrUpdate(String id)
	{
		Form<ExperimentType> form = experimentTypeForm.bindFromRequest();
		ExperimentType bean = form.get();
		
		CommonInfoType beanCIT =GenericTypes.getBeanFromRequest(ControllerTypeFactory.typeExperiment);
		
		//TODO bug recupere aussi ceux qui ne sont pas selectionné 
		List<ExperimentType> nextExperiments = new ArrayList<ExperimentType>();
		for(ExperimentType expType : bean.nextExperimentTypes){
			System.out.println("expType "+expType.id);
			if(expType.id!=null)
				nextExperiments.add(expType);
		}
		bean.nextExperimentTypes = (List<ExperimentType>) GenericTypes.getListTypeFromDB(nextExperiments);
		
		//bean.nextExperimentTypes = (List<ExperimentType>) GenericTypes.getListTypeFromDB(bean.nextExperimentTypes);
		bean.instrumentTypes = (List<InstrumentUsedType>) GenericTypes.getListTypeFromDB(bean.instrumentTypes);
		//bean=beanCIT;
		
		if(id!=null && !id.equals("null")){
			bean.id=Long.valueOf(id);
		}
		
		if(bean.id==null)
			bean.add();
		else{
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
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(expType);
		Form<ExperimentType> filledFormET = experimentTypeForm.fill(expType);
		return ok(experimentTypes.render(filledFormCIT, filledFormET,false));
	}
	
	@Override
	public Result edit(long idCommonInfoType){
		ExperimentType expType = ExperimentType.findByCommonInfoType(idCommonInfoType);
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(expType);
		Form<ExperimentType> filledFormET = experimentTypeForm.fill(expType);
		return ok(experimentTypes.render(filledFormCIT, filledFormET,true));
	}
	
}
