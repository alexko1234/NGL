 package controllers.analyses.api;


import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import controllers.QueryFieldsForm;
import controllers.SubDocumentController;
import controllers.authorisation.Permission;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.File;
import models.utils.InstanceConstants;
import play.data.Form;
import play.mvc.Result;
import validation.ContextValidation;
import validation.run.instance.FileValidationHelper;

public class Files extends SubDocumentController<Analysis, File> {

	private final static List<String> authorizedUpdateFields = Arrays.asList("fullname");
	private final Form<QueryFieldsForm> updateForm; 
	
	@Inject
	public Files(NGLContext ctx) {
		super(ctx,InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, File.class);
		this.updateForm = getNGLContext().form(QueryFieldsForm.class);
	}
	
	@Override
	protected Object getSubObject(Analysis objectInDB, String fullname) {
		for (File file : objectInDB.files) {
			if (file.fullname.equals(fullname)) {
				return file;
			}
		}
		return null;
	}

	@Override
	protected Query getSubObjectQuery(String parentCode, String fullname) {
		return DBQuery.and(DBQuery.is("code", parentCode), DBQuery.is("files.fullname", fullname));
	}

	@Override
	protected Object getSubObjects(Analysis objectInDB) {
		return objectInDB.files;
	}
	
	@Permission(value={"writing"})	
	public Result save(String parentCode) {
		Analysis objectInDB = getObject(parentCode);
		if (objectInDB == null) {
			return notFound();
		}
		
		Form<File> filledForm = getSubFilledForm();
		File inputFile = filledForm.get();
				
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.putObject("analysis", objectInDB);
		ctxVal.putObject("objectClass", objectInDB.getClass());
		ctxVal.setCreationMode();
		inputFile.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			updateObject(DBQuery.is("code", parentCode), 
					DBUpdate.push("files", inputFile)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
			return get(parentCode, inputFile.fullname);
		} else {
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
	public Result update(String parentCode, String fullname) {
		Analysis objectInDB = getObject(getSubObjectQuery(parentCode, fullname));
		if (objectInDB == null) {
			return notFound();			
		}	
		
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		
		Form<File> filledForm = getSubFilledForm();
		File fileInput = filledForm.get();
		if (queryFieldsForm.fields == null) {
			if (fullname.equals(fileInput.fullname)) {			
//				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
				ctxVal.putObject("analysis", objectInDB);
				ctxVal.putObject("objectClass", objectInDB.getClass());
				ctxVal.setUpdateMode();
				fileInput.validate(ctxVal);
				
				if (!ctxVal.hasErrors()) {
					updateObject(getSubObjectQuery(parentCode, fullname), 
							DBUpdate.set("files.$", fileInput)
							.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
					return get(parentCode, fullname);
				} else {
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}
			} else {
				return badRequest("fullname are not the same");
			}
		} else { //update only some authorized properties
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
			ctxVal.putObject("analysis", objectInDB);
			ctxVal.putObject("objectClass", objectInDB.getClass());
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			
			if(!ctxVal.hasErrors() && queryFieldsForm.fields.contains("fullname")){
				ctxVal.setCreationMode();
				FileValidationHelper.validateFileFullName(fileInput.fullname, ctxVal);
			}
			if (!ctxVal.hasErrors()) {
				updateObject(getSubObjectQuery(parentCode, fullname), 
						getBuilder(fileInput, queryFieldsForm.fields, File.class,"files.$")
						.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
				if(queryFieldsForm.fields.contains("fullname") && null != fileInput.fullname){
					fullname = fileInput.fullname;
				}
				return get(parentCode, fullname);
			} else {
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}

	@Permission(value={"writing"})	
	public Result delete(String parentCode, String fullname) {
		Analysis objectInDB = getObject(getSubObjectQuery(parentCode, fullname));
		if (objectInDB == null) {
			return notFound();			
		}
		updateObject(getSubObjectQuery(parentCode, fullname), 
				DBUpdate.unset("files.$").set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
		updateObject(DBQuery.is("code", parentCode),DBUpdate.pull("files", null)); 
		return ok();
	}
	
	@Permission(value={"writing"})
	public Result deleteByParentCode(String parentCode) {
		Analysis objectInDB = getObject(parentCode);
		if (objectInDB == null)
			return notFound();
		
		updateObject(DBQuery.is("code", parentCode), 
				DBUpdate.unset("files").set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));		
		return ok();
	}
}
