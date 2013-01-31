package controllers.admin.types;




import java.util.ArrayList;
import java.util.List;

import models.laboratory.IDynamicType;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;

import org.codehaus.jackson.node.ObjectNode;

import play.api.templates.PlayMagicForJava;
import play.data.DynamicForm.Dynamic;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.types.genericTypes;
import controllers.utils.DataTableForm;
import factory.ControllerTypeFactory;
import views.html.admin.types.*;


/**
 * Generic controller for all new type controller
 * Defines common operations for all type controller
 * @author ejacoby
 *
 */
public class GenericTypes2 extends Controller {

	/**
	 * Defines a form wrapping the User class.
	 */ 
	final static Form<CommonInfoType> commonInfoTypeForm = form(CommonInfoType.class);
	private static final String Log = null;
	final static DynamicForm inputForm = Controller.form();
	public static Result home() {
		//return ok(genericTypes.render(datatableForm, commonInfoTypeForm));
		return ok(genericTypes2.render());
	}

	/**
	 * List all objectType in research form
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result list() {

		DynamicForm filledForm = inputForm.bindFromRequest(); 
		ObjectNode result = Json.newObject();
		//List<CommonInfoType> commonInfoTypes = CommonInfoType.findByNameAndType(filledForm.get().sSearch.get(1),(!filledForm.get().sSearch.get(2).isEmpty()) ? Long.valueOf(filledForm.get().sSearch.get(2)):null);
		
		//List<CommonInfoType> commonInfoTypes = CommonInfoType.findByNameAndType(filledForm.get("name"), Long.valueOf(filledForm.get("typeCode")));
		
		List<CommonInfoType> commonInfoTypes = CommonInfoType.findByNameAndType("", null);
		
		
		/*Page<CommonInfoType> p = CommonInfoType.page(0, 10, "name", "asc", filledForm.get().sSearch.get(1), 
				(!filledForm.get().sSearch.get(2).isEmpty()) ? Long.valueOf(filledForm.get().sSearch.get(2)):null);

		result.put("iTotalRecords", p.getTotalRowCount());
		result.put("iTotalDisplayRecords", p.getTotalRowCount());*/
		//result.put("iTotalRecords", commonInfoTypes.size());
		//result.put("iTotalDisplayRecords", commonInfoTypes.size());
		//result.put("sEcho", filledForm.get().sEcho);
		//result.put("aaData", Json.toJson(commonInfoTypes));
		return ok(Json.toJson(commonInfoTypes));
	}

	/**
	 * Generic service to create or update commonInfoType
	 * Used in table view
	 * @param format
	 * @return
	 */
	public static Result createOrUpdate(String format){
		Form<CommonInfoType> filledForm = commonInfoTypeForm.bindFromRequest();
		if(filledForm.hasErrors()) {
			if("json".equals(format)){
				return badRequest(filledForm.errorsAsJson());
			}else{
				//TODO
				//return badRequest(genericType.render(filledForm,true));
				return badRequest();
			}

		} else {
			CommonInfoType bean = filledForm.get();
			//TODO
			if(bean.id == null){
				bean.add();
			}else{
				bean.update();
			}						
			filledForm = filledForm.fill(bean);
			if("json".equals(format)){
				return ok(Json.toJson(bean));
			}else{
				//TODO
				//return ok(genericType.render(filledForm,true));
				return ok();
			}



		}		
	}

	public static Result addPropertyDefinition(Integer index){
		Form<CommonInfoType> defaultForm = commonInfoTypeForm.fill(new CommonInfoType()); //put default value
		return ok(addPropertyDefinition.render(PlayMagicForJava.javaFieldtoScalaField(defaultForm.field("propertiesDefinition["+index+"]")), Boolean.TRUE));	
	}

	@SuppressWarnings("unchecked")
	public static CommonInfoType getBeanFromRequest(String typeName)
	{
		Form<CommonInfoType> formCIT = commonInfoTypeForm.bindFromRequest();
		CommonInfoType beanCIT = formCIT.get();
		ObjectType objectType = ObjectType.findByType(typeName);
		beanCIT.objectType=objectType;
		//TODO a revoir
		beanCIT.variableStates = (List<State>) getListTypeFromDB(beanCIT.variableStates);
		return beanCIT;
	}

	/**
	 * Service to retrieve from database the list of entities from a view list
	 * @param listToTransform
	 * @return
	 */
	public static List<? extends IDynamicType> getListTypeFromDB(List<? extends IDynamicType> listToTransform)
	{
		List<IDynamicType> newList = new ArrayList<IDynamicType>();
		for(IDynamicType type : listToTransform){
			IDynamicType typeFromDB = type.findById(type.getIdType());
			newList.add(typeFromDB);
		}
		return newList;
	}

	/**
	 * Get specific action type controller to add view 
	 * @param id
	 * @return
	 */
	public static Result add(Long id){
		return ControllerTypeFactory.getInstance(id).add();
	}

	/**
	 * Get specific action type controller to show view 
	 * @param id
	 * @return
	 */
	public static Result show(Long id)
	{
		CommonInfoType cit = CommonInfoType.findCommonById(id);
		return ControllerTypeFactory.getInstance(cit.objectType.id).show(cit.id);
	}

	/**
	 * Get specific action type controller to edit view 
	 * @param id
	 * @return
	 */
	public static Result edit(Long id)
	{
		CommonInfoType cit = CommonInfoType.findCommonById(id);
		return ControllerTypeFactory.getInstance(cit.objectType.id).edit(cit.id);
	}
}