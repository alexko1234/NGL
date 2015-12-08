package controllers.experiments.api;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.Experiment;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import play.data.Form;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.SubDocumentController;
@Controller
public class ExperimentComments extends SubDocumentController<Experiment, Comment>{

	public ExperimentComments() {
		super(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, Comment.class);
	}
	
	protected Query getSubObjectQuery(String parentCode, String code){
		return DBQuery.and(DBQuery.is("code", parentCode), DBQuery.is("comments.code",code));
	}
	
	protected Collection<Comment> getSubObjects(Experiment object){
		return object.comments;
	}
	
	protected Comment getSubObject(Experiment object, String code){
		for(Comment c : object.comments){
			if(code.equals(c.code)){
				return c;
			}
		}
		return null;
	}
	
	public Result save(String parentCode){
		Experiment objectInDB = getObject(parentCode);
		if (objectInDB == null) {
			return notFound();
		}
		
		Form<Comment> filledForm = getSubFilledForm();
		Comment inputComment = filledForm.get();
		
		if (null == inputComment.code) {
			inputComment.createUser = getCurrentUser();
			inputComment.creationDate = new Date();
			inputComment.code = CodeHelper.getInstance().generateExperimentCommentCode(inputComment);									
		} else {
			return badRequest("use PUT method to update the comment");
		}
		
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ctxVal.setCreationMode();
		ctxVal.putObject("experiment", objectInDB);		
		inputComment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			updateObject(DBQuery.is("code", parentCode), 
					DBUpdate.push("comments", inputComment)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
			return get(parentCode, inputComment.code);
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}

	public Result update(String parentCode, String code){
		Experiment objectInDB = getObject(getSubObjectQuery(parentCode, code));
		if (objectInDB == null) {
			return notFound();			
		}	
		
		Form<Comment> filledForm = getSubFilledForm();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		
		Comment inputComment = filledForm.get();
		if(getCurrentUser().equals(inputComment.createUser)){
			if (code.equals(inputComment.code)) {
				ctxVal.setUpdateMode();
				ctxVal.putObject("experiment", objectInDB);
				inputComment.validate(ctxVal);
				if(!ctxVal.hasErrors()){
					updateObject(DBQuery.is("code", parentCode).is("comments.code", inputComment.code), 
							DBUpdate.set("comments.$", inputComment)
							.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
					return get(parentCode, code);
				} else {
					return badRequest(filledForm.errorsAsJson());			
				}
			}else{
				return badRequest("treatment code are not the same");
			}
		}else{
			return forbidden();
		}
	}
	
	public Result delete(String parentCode, String code){
		Experiment objectInDB = getObject(getSubObjectQuery(parentCode, code));
		if (objectInDB == null) {
			return notFound();			
		}	
		Comment deleteComment = getSubObject(objectInDB, code);
		if(getCurrentUser().equals(deleteComment.createUser)){
			updateObject(DBQuery.is("code", parentCode), 
					DBUpdate.pull("comments", deleteComment)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
			return ok();	
		}else{
			return forbidden();
		}
			
	}
	
}
