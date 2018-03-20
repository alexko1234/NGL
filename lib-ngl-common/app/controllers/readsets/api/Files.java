package controllers.readsets.api;


import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.NGLContext;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
// import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.run.instance.FileValidationHelper;

public class Files extends ReadSetsController {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Files.class);
	
	private static final List<String> authorizedUpdateFields = Arrays.asList("fullname");
	
	private final Form<File>            fileForm; 
	private final Form<QueryFieldsForm> updateForm;
	
	@Inject
	public Files(NGLContext ctx) {
		fileForm   = ctx.form(File.class);
		updateForm = ctx.form(QueryFieldsForm.class);
	}
	
	@Permission(value={"reading"})
	public Result list(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (readSet == null) 
			return badRequest(); // TODO : return notFound()
		return ok(Json.toJson(readSet.files));
	}

	@Permission(value={"reading"})
	public Result get(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (readSet == null) 
			return badRequest(); // TODO : return a value coherent with head() 
		for (File file : readSet.files) 
			if (file.fullname.equals(fullname)) 
				return ok(Json.toJson(file));
		return notFound();
	}
	
	@Permission(value={"reading"})
	public Result head(String readsetCode, String fullname) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)))){			
			return ok();					
		} else {
			return notFound();
		}		
	}
	
	@Permission(value={"writing"})
	public Result save(String readsetCode) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (readSet == null)
			return badRequest();
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File file = filledForm.get();
		logger.debug("file " + file);
		
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.putObject("readSet",     readSet);
		ctxVal.putObject("objectClass", readSet.getClass());
		ctxVal.setCreationMode();
		file.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readsetCode),
					DBUpdate.push("files", file).set("traceInformation", getUpdateTraceInformation(readSet))); 
			return ok(Json.toJson(file));
		} else {
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
	public Result update(String readSetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File fileInput = filledForm.get();
		if (queryFieldsForm.fields == null) {
			if (fullname.equals(fileInput.fullname)) {			
//				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
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
					return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
				}
			} else {
				return badRequest("fullname are not the same");
			}
		} else { //update only some authorized properties
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
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
				
				if (queryFieldsForm.fields.contains("fullname") && fileInput.fullname != null) {
					fullname = fileInput.fullname;
				}
				return get(readSetCode, fullname);
			} else {
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}

	@Permission(value={"writing"})
	public Result delete(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (readSet == null) 
			return badRequest();
		//TODO Doit marcher {$pull : {"files" : {"fullname" : {$regex : "trim"}}}}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)), DBUpdate.unset("files.$").set("traceInformation", getUpdateTraceInformation(readSet)));
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readsetCode), DBUpdate.pull("files", null));
		return ok();
	}

	@Permission(value={"writing"})
	public Result deleteByReadSetCode(String readsetCode) { 
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readsetCode));
		if (readSet == null)
			return badRequest();
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readsetCode), DBUpdate.unset("files"));
		return ok();
	}
	
	@Permission(value={"writing"})
	public Result deleteByRunCode(String runCode) { 
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run == null)
			return notFound();
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode", runCode)), DBUpdate.unset("files"));
		return ok();
	}
	
}
