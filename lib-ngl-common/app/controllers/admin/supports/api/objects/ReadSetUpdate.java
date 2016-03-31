package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;
import controllers.readsets.api.ReadSets;

public class ReadSetUpdate extends AbstractUpdate<ReadSet>{

	public ReadSetUpdate() {
		super(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class);		
	}
	
	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, "sampleOnContainer."));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));	
			
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		if(NGLObject.Action.delete.equals(NGLObject.Action.valueOf(input.action))){
			ReadSets.delete(input.code);
		}else{
			throw new RuntimeException(input.action+" not implemented");
		}	
	}
}
