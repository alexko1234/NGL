package controllers.instruments.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

//import controllers.CommonController;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import controllers.APICommonController;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class InstrumentCategories extends APICommonController<InstrumentCategoriesSearchForm> { //CommonController{
	
	private static final play.Logger.ALogger logger = play.Logger.of(InstrumentCategories.class);
	
	private final /*static*/ Form<InstrumentCategoriesSearchForm> instrumentCategoriesForm;// = form(InstrumentCategoriesSearchForm.class);

	@Inject
	public InstrumentCategories(NGLContext ctx) {
		super(ctx, InstrumentCategoriesSearchForm.class);
		instrumentCategoriesForm = ctx.form(InstrumentCategoriesSearchForm.class);
	}
	
	public Result list() throws DAOException{
		Form<InstrumentCategoriesSearchForm> instrumentCategoriesTypeFilledForm = filledFormQueryString(instrumentCategoriesForm,InstrumentCategoriesSearchForm.class);
		InstrumentCategoriesSearchForm instrumentCategoriesQueryParams = instrumentCategoriesTypeFilledForm.get();

		List<InstrumentCategory> instrumentCategories;

		try {		
			if(StringUtils.isNotBlank(instrumentCategoriesQueryParams.instrumentTypeCode)){
				instrumentCategories = InstrumentCategory.find.findByInstrumentUsedTypeCode(instrumentCategoriesQueryParams.instrumentTypeCode);
			}else{
				instrumentCategories = InstrumentCategory.find.findAll();
			}
			if(instrumentCategoriesQueryParams.datatable){
				return ok(Json.toJson(new DatatableResponse<>(instrumentCategories, instrumentCategories.size()))); 
			}else if(instrumentCategoriesQueryParams.list){
				List<ListObject> lop = new ArrayList<>();
				for(InstrumentCategory et:instrumentCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(instrumentCategories));
			}
		} catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
