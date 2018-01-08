package controllers.containers.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.APICommonController;
//import controllers.CommonController;
import controllers.authorisation.Permission;
import javax.inject.Inject;

import fr.cea.ig.play.NGLContext;
import controllers.APICommonController;

public class ContainerSupportCategories extends APICommonController<ContainerSupportCategoriesSearchForm> { //CommonController{

	private final /*static*/ Form<ContainerSupportCategoriesSearchForm> containerSupportCategoriesTypeForm;// = form(ContainerSupportCategoriesSearchForm.class);
	
	@Inject
	public ContainerSupportCategories(NGLContext ctx) {
		super(ctx, ContainerSupportCategoriesSearchForm.class);
		containerSupportCategoriesTypeForm = ctx.form(ContainerSupportCategoriesSearchForm.class);
	}
	
	@Permission(value={"reading"})
	public Result list() throws DAOException{
		Form<ContainerSupportCategoriesSearchForm>  containerCategoryFilledForm = filledFormQueryString(containerSupportCategoriesTypeForm,ContainerSupportCategoriesSearchForm.class);
		ContainerSupportCategoriesSearchForm containerSupportCategoriesSearch = containerCategoryFilledForm.get();

		List<ContainerSupportCategory> containerSupportCategories = new ArrayList<ContainerSupportCategory>();
		try{
			if(StringUtils.isNotBlank(containerSupportCategoriesSearch.instrumentUsedTypeCode)){
				containerSupportCategories = InstrumentUsedType.find.findByCode(containerSupportCategoriesSearch.instrumentUsedTypeCode).outContainerSupportCategories;
			}else if(StringUtils.isNotBlank(containerSupportCategoriesSearch.experimentTypeCode)){
				containerSupportCategories = ContainerSupportCategory.find.findInputByExperimentTypeCode(containerSupportCategoriesSearch.experimentTypeCode); 
			}else{
				containerSupportCategories = ContainerSupportCategory.find.findAll();
			}
			if(containerSupportCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ContainerSupportCategory>(containerSupportCategories, containerSupportCategories.size()))); 
			}else if(containerSupportCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ContainerSupportCategory et:containerSupportCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(containerSupportCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
