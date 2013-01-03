package controllers.admin.types;

import models.description.common.CommonInfoType;
import models.description.experiment.InstrumentUsedType;
import play.data.Form;
import play.mvc.Result;
import views.html.admin.types.instrumentUsedTypes;

/**
 * Specific controller for CRUD operation on instrument used type
 * All type controller must inherit GenericTypes and implements IGenericCreateTypes
 * @author ejacoby
 *
 */
public class InstrumentUsedTypes extends GenericTypes implements IGenericCreateTypes{

	public static Form<InstrumentUsedType> instrumentUsedTypeForm = form(InstrumentUsedType.class);
	
	public static Result home() {
		return ok(instrumentUsedTypes.render(commonInfoTypeForm, instrumentUsedTypeForm,true));
	}
	
	@SuppressWarnings("unchecked")
	public static Result createOrUpdate()
	{
		Form<InstrumentUsedType> form = form(InstrumentUsedType.class).bindFromRequest();
		InstrumentUsedType bean = form.get();
		
		System.out.println("Info to save");
		
		//TODO
		return ok("OK");
	}

	@Override
	public Result add() {
		return ok(instrumentUsedTypes.render(commonInfoTypeForm, instrumentUsedTypeForm,true));
	}

	@Override
	public Result show(long idCommonInfoType) {
		InstrumentUsedType instUsedType = InstrumentUsedType.findByCommonInfoType(idCommonInfoType);
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(instUsedType.commonInfoType);
		Form<InstrumentUsedType> filledFormIUT = instrumentUsedTypeForm.fill(instUsedType);
		return ok(instrumentUsedTypes.render(filledFormCIT, filledFormIUT,false));
	}

	@Override
	public Result edit(long idCommonInfoType) {
		InstrumentUsedType instUsedType = InstrumentUsedType.findByCommonInfoType(idCommonInfoType);
		Form<CommonInfoType> filledFormCIT = commonInfoTypeForm.fill(instUsedType.commonInfoType);
		Form<InstrumentUsedType> filledFormIUT = instrumentUsedTypeForm.fill(instUsedType);
		return ok(instrumentUsedTypes.render(filledFormCIT, filledFormIUT,true));
	}
	
	
}
