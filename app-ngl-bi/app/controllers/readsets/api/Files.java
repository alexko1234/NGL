 package controllers.readsets.api;

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
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
import controllers.authorisation.Permission;



public class Files extends CommonController {

	final static Form<File> fileForm = form(File.class);

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
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.putObject("readSet", readSet);
		ctxVal.setCreationMode();
		file.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readsetCode),
					DBUpdate.push("files", file)); 
			return ok(Json.toJson(file));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	//@Permission(value={"creation_update _files"})
	public static Result update(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		File file = filledForm.get();
		if (fullname.equals(file.fullname)) {
			ContextValidation ctxVal = new ContextValidation(filledForm.errors());
			ctxVal.putObject("readSet", readSet);
			ctxVal.setUpdateMode();
			file.validate(ctxVal);
			
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)),
						DBUpdate.set("files.$", file)); 
				
				return ok(Json.toJson(file));
			} else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("fullname are not the same");
		}
	}

	@Permission(value={"delete_files"})
	public static Result delete(String readsetCode, String fullname) {
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)));
		if (null == readSet) {
			return badRequest();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("code", readsetCode), DBQuery.is("files.fullname", fullname)), DBUpdate.unset("files.$"));
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readsetCode), DBUpdate.pull("files", null));
		return ok();
	}

	@Permission(value={"delete_files"})
	public static Result deleteByRunCode(String runCode) { 
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return notFound();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode", runCode)), DBUpdate.unset("files"));
		return ok();
	}
	
	public static Result workflow(String readsetCode, String fullname, String stateCode){
		return badRequest("Not implemented");
	}

}
