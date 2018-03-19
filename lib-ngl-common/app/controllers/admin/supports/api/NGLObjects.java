package controllers.admin.supports.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import models.laboratory.common.description.Level;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

// import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

import com.mongodb.BasicDBObject;

import controllers.APICommonController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.admin.supports.api.objects.AbstractUpdate;
import controllers.admin.supports.api.objects.ContainerUpdate;
import controllers.admin.supports.api.objects.ExperimentUpdate;
import controllers.admin.supports.api.objects.ProcessUpdate;
import controllers.admin.supports.api.objects.ReadSetUpdate;
import controllers.authorisation.Permission;
import controllers.readsets.api.ReadSets;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;

public class NGLObjects extends APICommonController<NGLObject> {

	private static final play.Logger.ALogger logger = play.Logger.of(NGLObjects.class);
	
	// private final Form<NGLObjectsSearchForm> searchForm ; //= form(NGLObjectsSearchForm.class);
	private Map<String, AbstractUpdate<?>>   mappingCollectionUpdates;
	// private final NGLContext                 ctx;
	
	@Inject
	public NGLObjects(NGLContext ctx, ReadSets readSets) {
		super(ctx, NGLObject.class);
//		this.searchForm = ctx.form(NGLObjectsSearchForm.class);
		mappingCollectionUpdates = new HashMap<>(); // <String, AbstractUpdate>();
		mappingCollectionUpdates.put("ngl_sq.Container", new ContainerUpdate());
		mappingCollectionUpdates.put("ngl_sq.Process", new ProcessUpdate());
		mappingCollectionUpdates.put("ngl_sq.Experiment", new ExperimentUpdate());
		mappingCollectionUpdates.put("ngl_bi.ReadSetIllumina", new ReadSetUpdate(readSets));
	}

	@Permission(value={"admin"})
	public Result list() {
		NGLObjectsSearchForm form = filledFormQueryString(NGLObjectsSearchForm.class);
		
		//Form d = Form.form(); //just used to have errors
		Form<?> d = getNGLContext().form();
//		ContextValidation cv = new ContextValidation(getCurrentUser(), d.errors());
		ContextValidation cv = new ContextValidation(getCurrentUser(),d);
		form.validate(cv);
		if (cv.hasErrors()) {
			// return badRequest(Json.toJson(d.errors-AsJson()));
			return badRequest(errorsAsJson(cv.getErrors()));
		} else {
			List<NGLObject> results = form.collectionNames
				.stream()
				.map(collectionName -> getNGLObjects(form, collectionName))
				.flatMap(List::stream)
				.collect(Collectors.toList());			
			return ok(Json.toJson(results));
		}
	}
	
	@Permission(value={"admin"})
	public Result update(String code){
		Form<NGLObject> filledForm = getMainFilledForm();
		NGLObject input = filledForm.get();
		
		if (input.code.equals(code)) {
//			ContextValidation cv = new ContextValidation(getCurrentUser(), filledForm.errors());
			ContextValidation cv = new ContextValidation(getCurrentUser(), filledForm);
			input.validate(cv);
			if (cv.hasErrors()) {
				//return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(cv.getErrors()));
			} else {				
				mappingCollectionUpdates.get(input.collectionName).update(input, cv);
				if (cv.hasErrors()){
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(cv.getErrors()));
				} else {
					return ok();
				}
			}
		} else {
			return badRequest("NGLObject code are not the same");
		}		
	}

	private List<NGLObject> getNGLObjects(NGLObjectsSearchForm form, String collectionName) {
		Query q =  mappingCollectionUpdates.get(collectionName).getQuery(form);
		if (q != null) {
			List<NGLObject> r = MongoDBDAO.find(collectionName, NGLObject.class, q, getKeys()).toList();
			r.forEach(o -> {
				logger.debug("treat"+o.code);
				o.collectionName = collectionName;
				o.contentPropertyNameUpdated = form.contentPropertyNameUpdated;
				o.currentValue = form.contentProperties.get(form.contentPropertyNameUpdated).get(0);
				o.projectCode = form.projectCode;
				o.sampleCode = form.sampleCode;	
				o.nbOccurrences = mappingCollectionUpdates.get(collectionName).getNbOccurrence(o);
			});
			return r;
		} else {
			return Collections.emptyList();
		}
	}
	
	private BasicDBObject getKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("code",1);
		keys.put("typeCode",1);
		return keys;
	}
	
}
