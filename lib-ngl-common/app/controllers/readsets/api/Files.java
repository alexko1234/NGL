package controllers.readsets.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.With;
import validation.ContextValidation;
import validation.run.instance.FileValidationHelper;
import validation.run.instance.ReadSetValidationHelper;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import controllers.history.UserHistory;
import fr.cea.ig.play.NGLContext;

public class Files extends ReadSetsController {

	private final /*static*/ Form<File> fileForm; // = form(File.class);
	private final /*static*/ Form<QueryFieldsForm> updateForm; // = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("fullname");
	
	@Inject
	public Files(NGLContext ctx) {
		fileForm = ctx.form(File.class);
		updateForm = ctx.form(QueryFieldsForm.class);
	}
	
	@Permission(value={"reading"})
	public /*static*/ Result list(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (null == readSet) {
			return badRequest();
		}
		return ok(Json.toJson(readSet.files));
	}

	@Permission(value={"reading"})
	public /*static*/ Result get(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		for (File file : readSet.files) {
			if (file.fullname.equals(fullname)) {
				return ok(Json.toJson(file));
			}
		}
		return notFound();
	}
	
	@Permission(value={"reading"})
	public /*static*/ Result head(String readsetCode, String fullname) {
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)))){			
			return ok();					
		}else{
			return notFound();
		}		
	}
	
	@Permission(value={"writing"})
	public /*static*/ Result save(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (null == readSet) {
			return badRequest();
		}
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File file = filledForm.get();
				
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.putObject("readSet", readSet);
		ctxVal.putObject("objectClass", readSet.getClass());
		ctxVal.setCreationMode();
		file.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readsetCode),
					DBUpdate.push("files", file).set("traceInformation", getUpdateTraceInformation(readSet))); 
			return ok(Json.toJson(file));
		} else {
			//return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
	public /*static*/ Result update(String readSetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File fileInput = filledForm.get();
		if(queryFieldsForm.fields == null){
			if (fullname.equals(fileInput.fullname)) {			
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				ctxVal.putObject("readSet", readSet);
				ctxVal.putObject("objectClass", readSet.getClass());
				ctxVal.setUpdateMode();
				fileInput.validate(ctxVal);
				
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
							DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)),
							DBUpdate.set("files.$", fileInput).set("traceInformation", getUpdateTraceInformation(readSet))); 
					
					return get(readSetCode, fullname);
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
				}
			} else {
				return badRequest("fullname are not the same");
			}
		}else{ //update only some authorized properties
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ctxVal.putObject("readSet", readSet);
			ctxVal.putObject("objectClass", readSet.getClass());
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			
			if(queryFieldsForm.fields.contains("fullname")){
				ctxVal.setCreationMode();
				FileValidationHelper.validateFileFullName(fileInput.fullname, ctxVal);
			}
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)),
						getBuilder(fileInput, queryFieldsForm.fields, File.class,"files.$").set("traceInformation", getUpdateTraceInformation(readSet))); 
				
				if(queryFieldsForm.fields.contains("fullname") && null != fileInput.fullname){
					fullname = fileInput.fullname;
				}
				return get(readSetCode, fullname);
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}

	@Permission(value={"writing"})
	public /*static*/ Result delete(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		
		//TODO Doit marcher {$pull : {"files" : {"fullname" : {$regex : "trim"}}}}
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)), DBUpdate.unset("files.$").set("traceInformation", getUpdateTraceInformation(readSet)));
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readsetCode), DBUpdate.pull("files", null));
		return ok();
	}

	@Permission(value={"writing"})
	public /*static*/ Result deleteByReadSetCode(String readsetCode) { 
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (null == readSet) {
			return badRequest();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readsetCode), DBUpdate.unset("files"));
		return ok();
	}
	
	@Permission(value={"writing"})
	public /*static*/ Result deleteByRunCode(String runCode) { 
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return notFound();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode", runCode)), DBUpdate.unset("files"));
		return ok();
	}
	

}
