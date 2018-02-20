package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import validation.utils.ValidationHelper;
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
		queryElts.add(getProjectCodeQuery(form, "sampleOnInputContainer."));
		queryElts.add(getSampleCodeQuery(form, "sampleOnInputContainer."));
		queryElts.addAll(getContentPropertiesQuery(form, "sampleOnInputContainer."));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		if(CollectionUtils.isNotEmpty(form.codes)){
			query.and(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.codeRegex)){
			query.and(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Process process = getObject(input.code);
		
		PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
		Object newValue = ValidationHelper.convertStringToType(pd.valueType, input.newValue);
		
		if (NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))) {
			// process.sampleOnInputContainer.properties.get(input.contentPropertyNameUpdated).value = newValue;	
			 process.sampleOnInputContainer.properties.get(input.contentPropertyNameUpdated).assignValue(newValue);	
		} else {
			throw new RuntimeException(input.action+" not implemented");
		}
		process.validate(cv);
		if(!cv.hasErrors()){
			updateObject(process);
		}
	}

	@Override
	public Long getNbOccurrence(NGLObject input) {
		return 1L;
	}

}
