 package controllers.readsets.api;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.run.instance.FileValidationHelper;
import validation.run.instance.ReadSetValidationHelper;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;



public class Files extends ReadSetsController {

	final static Form<File> fileForm = form(File.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("fullname");
	
	//@Permission(value={"reading"})
	public static Result list(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (null == readSet) {
			return badRequest();
		}
		return ok(Json.toJson(readSet.files));
	}

	//@Permission(value={"reading"})
	public static Result get(String readsetCode, String fullname) {
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
	
	//@Permission(value={"reading"})
	public static Result head(String readsetCode, String fullname) {
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)))){			
			return ok();					
		}else{
			return notFound();
		}		
	}
	
	//@Permission(value={"creation_update_files"})
	public static Result save(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (null == readSet) {
			return badRequest();
		}
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File file = filledForm.get();
		
		if(null == file.state){
			file.state = new State();
		}
		file.state.code = "N";
		file.state.user = getCurrentUser();
		file.state.date = new Date();	
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.putObject("readSet", readSet);
		ctxVal.setCreationMode();
		file.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readsetCode),
					DBUpdate.push("files", file).set("traceInformation", getUpdateTraceInformation(readSet))); 
			return ok(Json.toJson(file));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	//@Permission(value={"creation_update_files"})
	public static Result update(String readSetCode, String fullname) {
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
				ContextValidation ctxVal = new ContextValidation(filledForm.errors());
				ctxVal.putObject("readSet", readSet);
				ctxVal.setUpdateMode();
				fileInput.validate(ctxVal);
				
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
							DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)),
							DBUpdate.set("files.$", fileInput).set("traceInformation", getUpdateTraceInformation(readSet))); 
					
					return get(readSetCode, fullname);
				} else {
					return badRequest(filledForm.errorsAsJson());
				}
			}else{
				return badRequest("fullname are not the same");
			}
		}else{ //update only some authorized properties
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
			ctxVal.putObject("readSet", readSet);
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			
			if(queryFieldsForm.fields.contains("fullname")){
				ctxVal.setCreationMode();
				FileValidationHelper.validateFileFullName(fileInput.fullname, ctxVal);
			}
			if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)),
						getBuilder(fileInput, queryFieldsForm.fields, File.class,"files.$").set("traceInformation", getUpdateTraceInformation(readSet))); 
				
				if(queryFieldsForm.fields.contains("fullname") && null != fileInput.fullname){
					fullname = fileInput.fullname;
				}
				return get(readSetCode, fullname);
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
		}
	}

	//@Permission(value={"delete_files"})
	public static Result delete(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)), DBUpdate.unset("files.$").set("traceInformation", getUpdateTraceInformation(readSet)));
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readsetCode), DBUpdate.pull("files", null));
		return ok();
	}

	//@Permission(value={"delete_files"})
	public static Result deleteByRunCode(String runCode) { 
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return notFound();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode", runCode)), DBUpdate.unset("files"));
		return ok();
	}
	

}
