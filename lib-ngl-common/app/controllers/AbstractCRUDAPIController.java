package controllers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.ITracingAccess;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.ICRUDValidatable;
import validation.IValidation;
import views.components.datatable.DatatableForm;

/**
 * CRUD API controller base for DBObject subclasses.
 * 
 * @see Commentable
 * @see ICRUDValidatable
 * 
 * @author vrd
 *
 * @param <T> class to provide CRUD for
 */
// TODO: extend MongoController instead of DocumentController
public abstract class AbstractCRUDAPIController<T extends DBObject> extends DocumentController<T> {
	
	/**
	 * Construct a CRUD controller for the type that is stored in a named collection
	 * @param ctx            NGL context
	 * @param collectionName Mongo collection name
	 * @param type           type of objects to provide CRUD for
	 * @param defaultKeys    default list of field name
	 */
	public AbstractCRUDAPIController(NGLContext ctx, String collectionName, Class<T> type, List<String> defaultKeys) {
		super(ctx,collectionName, type, defaultKeys);	
	}

	// --------------------- CREATE -----------------------------------------
	// Overridable hooks for the creation, null return aborts early
	public T beforeCreationValidation(ContextValidation ctx, T t) {
		return t;
	}
	public T afterCreationValidation(ContextValidation ctx, T t) {
		return t;
	}
	/**
	 * Application level create operation.
	 * @param ctx validation context 
	 * @param t   instance to persist in the database 
	 */
	public T create(ContextValidation ctx, T t) {
		// Validate t as a DBObject
		if (t._id != null)
			ctx.addError("_id", "must not be present when creating an object, maybe you wanted to update the objet");
		// If t is ITraceable, stamp the creation
		if (t instanceof ITracingAccess)
			((ITracingAccess)t).setTraceCreationStamp(ctx, getCurrentUser());
		if (t instanceof ICommentable)
			((ICommentable) t).setComments(InstanceHelpers.updateComments(((ICommentable) t).getComments(), ctx));
		// Call before creation hook
		if ((t = beforeCreationValidation(ctx,t)) == null)
			return null;
		// If it's a IValidatable, set context mode to creation and 
		// call the validate.
		if (t instanceof ICRUDValidatable) {
			((ICRUDValidatable)t).validateInvariants(ctx);
			((ICRUDValidatable)t).validateCreation(ctx);
		} else if (t instanceof IValidation) {
			ctx.setCreationMode();
			((IValidation)t).validate(ctx);
		}
		if ((t = afterCreationValidation(ctx,t)) == null)
			return null;
		// Check for validation errors and abort creation if errors are present 
		if (ctx.hasErrors())
			return null;
		// save object
		return saveObject(t);
	}
	
	// Controller level access to the application level api
	public Result create() {
		// Fetch instance to create from request
		Form<T> filledForm = getMainFilledForm();
		T t = filledForm.get();
		// Create a validation context
		ContextValidation ctx = new ContextValidation(getCurrentUser(), filledForm.errors());
		t = create(ctx,t);
		return checkedResult(ctx,t);
	}

	

	// --------------------------------- READ -----------------------------------
	// -- expected method : return MongoDBDAO.findByCode(collectionName, type, code, keys);
	// see get(code) implementation in MongoCommonController
	public Result read(String code) {
		ContextValidation ctx = new ContextValidation(getCurrentUser());
		T t = null;
		if (request().queryString().size() > 0) {
			DatatableForm form = filledFormQueryString(DatatableForm.class);
			t =  getObject(code, getKeys(updateForm(form)));		
		} else { 
			t =  getObject(code);
		}
		if(t == null)
			ctx.addError("object","no object found for code %s",code);
		return checkedResult(ctx,t);
	}

	// -------------------------------- UPDATE ----------------------------------
	// Update hooks
	public T beforeUpdateValidation(ContextValidation ctx, T t) {
		return t;
	}
	public T afterUpdateValidation(ContextValidation ctx, T t) {
		return t;
	}
	
	
	// Update the instance as provided. Still some trickery with the comments
	// but this is roughly ok.
	public T update(ContextValidation ctx, T t) {
		if (t._id == null)
			ctx.addError("_id", "must be present when updating an object, maybe you wanted to create the objet");
		// If t is ITraceable, stamp the creation
		if (t instanceof ITracingAccess)
			((ITracingAccess)t).setTraceUpdateStamp(ctx, getCurrentUser());
		if (t instanceof ICommentable)
			((ICommentable) t).setComments(InstanceHelpers.updateComments(((ICommentable) t).getComments(), ctx));
		// Call before update hook
		if ((t = beforeUpdateValidation(ctx,t)) == null)
			return null;
		// If it's a IValidatable, set context mode to creation and 
		// call the validate.
		if (t instanceof IValidation) {
			ctx.setUpdateMode();
			((IValidation)t).validate(ctx);
		} else if (t instanceof ICRUDValidatable) {
			((ICRUDValidatable)t).validateInvariants(ctx);
			((ICRUDValidatable)t).validateUpdate(ctx);
		}
		if ((t = afterUpdateValidation(ctx,t)) == null)
			return null;
		// Check for validation errors and abort creation if errors are present 
		if (ctx.hasErrors())
			return null;
		// save object
		MongoDBDAO.update(getCollectionName(), t);
		return t;
	}
	
	public Result update(String code) {
		// Test query string, could just test if it exists in the first place
		Form<QueryFieldsForm> filledQueryFieldsForm = getQueryStringForm(QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<T> filledForm = getMainFilledForm();
		T t = filledForm.get();
		ContextValidation ctx = new ContextValidation(getCurrentUser(), filledForm.errors());
		// We could use a possibly faster test
		T dbo = getObject(code);
		if (dbo == null) {
			ctx.addError("object", "object with code %s not found", code);
		} else if (queryFieldsForm.fields == null) {
			// Partial update is not implemented at this moment
			ctx.addError("internal", "partial update is not supported");
		} else {
			t = update(ctx,t);
		}
		return checkedResult(ctx,t);
	}
	
	// --------------------------------- DELETE ------------------------------------------
	
	public T beforeDeletionValidation(ContextValidation ctx, T t) { return t; }
	public T afterDeletionValidation(ContextValidation ctx, T t) { return t; }
	
	public T delete(ContextValidation ctx, T t) {
		if (t._id == null)
			ctx.addError("_id", "must be present when deleting an object, maybe you wanted to create the objet");
		if ((t = beforeDeletionValidation(ctx,t)) == null)
			return null;
		// If it's a IValidatable, set context mode to creation and 
		// call the validate.
		if (t instanceof IValidation) {
			ctx.setDeleteMode();
			((IValidation)t).validate(ctx);
		} else if (t instanceof ICRUDValidatable) {
			((ICRUDValidatable)t).validateInvariants(ctx);
			((ICRUDValidatable)t).validateDelete(ctx);
		}
		if ((t = afterDeletionValidation(ctx,t)) == null)
			return null;
		// Check for validation errors and abort creation if errors are present 
		if (ctx.hasErrors())
			return null;
		MongoDBDAO.deleteByCode(getCollectionName(),  type, t.getCode());
		return t;
	}
	
	@Override
	public Result delete(String code) {
		ContextValidation ctx = new ContextValidation(getCurrentUser());
		T t = getObject(code);
		if (t == null) 
			ctx.addError("object", "object with code %s not found", code);
		else
			delete(ctx,t);
		return checkedResult(ctx,t);		
	}

	
	
	
	// Missing null 'a' test
	public <A> Result checkedResult(ContextValidation ctx, A a) {
		if (ctx.hasErrors())
			return badRequest(errorsAsJson(ctx.getErrors()));
		return ok(Json.toJson(a));
	}

	// Uses partial update mode
	
	/*Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);

	QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
	// Form<Sample> filledForm = getFilledForm(sampleForm, Sample.class);
	Form<T> filledForm = getMainFilledForm();
	T sampleInForm = filledForm.get();

	
	 
		/*
		public Meta<T> meta(String name, BiConsumer<ContextValidation,T> validator, BiConsumer<T,T> updater) {
			return new Meta<T>(name,validator,updater);
		}
		// Give a list of fields to update. validate against defined lists.
		// for each possible field, we define the validation function
		// as some external validate method. 
		public void partialUpdate(ContextValidation ctx, T t, List<String> fields) {
			// A partial update maps a field name to some validation function.
			// The name must belong to the authorized update fields.
			// The partial update triggers weak validation that is epxressed
			// in some provided mapping.
			Map<String,Meta<T>> meta = meta();
			for (String fn : fields) 
				// If the validator is defined, the the update is authorized.
				// Could use function and null return as fail early
				if (meta.containsKey(fn)) 
					meta.get(fn).validator.accept(ctx, t);
			// Build the query for the partial update
			
		}
		
		// Given a dboject and a form object, copy the provided fields by names.
		// Then trigger the full update. Seems simple enough.
		public T partialAsFullUpdate(ContextValidation ctx, T dbo, T ino, List<String> fields) {
			for (String fn : fields)
				// If the validator is defined, the the update is authorized.
				// Could use function and null return as fail early
				if (meta.containsKey(fn)) 
					meta.get(fn).updater.accept(ino,dbo);
			return fullUpdate(ctx,dbo);
		}
		
			// The update is either a full or a partial update. This makes no sense to have a compound
	// application api.
	public T update(ContextValidation ctx, T t) {
		T u = getObject(t.getCode());
		// Abort early if the object to update is not in the database.
		if (u == null) {
			// TODO:check if this is the proper call
			ctx.addError("MongoDB", "object %s does not exist in database", t.getCode());
			return null;
		}
		
		return null;
	}
	
	public void partialUpdateValidation() {
		
	}
	
	private void applyPartialUpdate() {
	}
	
	// Information about partial updates.
	public static class Meta<T> {
		String name; // field name in class
		BiConsumer<ContextValidation,T> validator;
		BiConsumer<T,T> updater;
		public Meta(String name, BiConsumer<ContextValidation,T> validator, BiConsumer<T,T> updater) {
			this.name = name;
			this.validator = validator;
			this.updater = updater;
		}
	}
	// Reflection copy to lighten the definition.

*/		
	// We index the meta info by class so the build meta results
	// are cached.
	/*private static Map<Class<?>,Map<String,Meta<?>>> metaCatalog;
	protected Map<String,Meta<T>> meta() {
		Map<String,Meta<T>> meta = metaCatalog.get(type);
		if (meta == null) {
			meta = new HashMap<String,Meta<T>>();
			for (Meta<T> m : buildMeta()) {
				meta.put(m.name,m);
			}
			metaCatalog.put(type, meta);
		}
		return meta;
	}*/
	// Can we control a bit more ?
	/*
	private Map<String,Meta<T>> meta = null;
	protected abstract Collection<Meta<T>> buildMeta();
	protected Map<String,Meta<T>> meta() {
		if (meta == null) {
			meta = new HashMap<String,Meta<T>>();
			for (Meta<T> m : buildMeta()) {
				meta.put(m.name,m);
			}
		}
		return meta;
	}
	public BiConsumer<ContextValidation,T> pass = (ctx,val) -> {};
	*/
	// This would require the validation context to return proper errors.
	// Could be passed using a 3rd arg.
	/*public BiConsumer<T,T> reflectCopy(String n) {
		Field f = type.getField(n);
		return (from,to) -> {
			try {
				f.set(to,f.get(from));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		};	
	}
	public Meta<T> metareflect(String n) {
		return new Meta<T>(n,pass,reflectCopy(n));
	}*/

}
