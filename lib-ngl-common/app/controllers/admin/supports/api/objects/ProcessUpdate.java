package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;

public class ProcessUpdate extends AbstractUpdate<Process>{

	public ProcessUpdate() {
		super(InstanceConstants.PROCESS_COLL_NAME, Process.class);		
	}
	
	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, "sampleOnInputContainer."));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Process process = getObject(input.code);
		if(NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))){
			process.sampleOnInputContainer.properties.get(input.contentPropertyNameUpdated).value = input.newValue;
			
		}else{
			throw new RuntimeException(input.action+" not implemented");
		}
		process.validate(cv);
		if(!cv.hasErrors()){
			updateObject(process);
		}
	}

}
