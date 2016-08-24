package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.experiment.instance.AbstractContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;

public class ExperimentUpdate extends AbstractUpdate<Experiment>{

	public ExperimentUpdate() {
		super(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class);		
	}
	
	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, ""));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		query = DBQuery.or(DBQuery.elemMatch("atomicTransfertMethods.outputContainerUseds.contents", query),
				DBQuery.elemMatch("atomicTransfertMethods.inputContainerUseds.contents", query));	
		
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Experiment exp = getObject(input.code);
		
		//1 update input containers
		if(NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))){
			updateInputContainers(exp, input);
			updateOutputContainers(exp, input);
			updateOutputExperimentProperties(exp, input);			
		}else{
			throw new RuntimeException(input.action+" not implemented");
		}
		
		exp.validate(cv);
		if(!cv.hasErrors()){
			updateObject(exp);
		}		
	}

	private void updateInputContainers(Experiment exp,
			NGLObject input) {
		exp.atomicTransfertMethods
			.stream()
			.map(atm -> atm.inputContainerUseds)
			.flatMap(List::stream)
			.map(icu -> icu.contents)
			.flatMap(List::stream)
			.filter(content -> {
				if(input.projectCode.equals(content.projectCode) &&
						input.sampleCode.equals(content.sampleCode) &&
						content.properties.containsKey(input.contentPropertyNameUpdated) && 
						input.currentValue.equals(content.properties.get(input.contentPropertyNameUpdated).value)){
							return true;
					}else{
						return false;
					}
			})
			.forEach(content ->{
				content.properties.get(input.contentPropertyNameUpdated).value = input.newValue;
			});;
		
	}
	
	private void updateOutputContainers(Experiment exp,
			NGLObject input) {
		exp.atomicTransfertMethods
			.stream()
			.filter(atm -> atm.outputContainerUseds != null)
			.map(atm -> atm.outputContainerUseds)
			.flatMap(List::stream)
			.map(ocu -> ocu.contents)
			.flatMap(List::stream)
			.filter(content -> {
				if(input.projectCode.equals(content.projectCode) &&
						input.sampleCode.equals(content.sampleCode) &&
						content.properties.containsKey(input.contentPropertyNameUpdated) && 
						input.currentValue.equals(content.properties.get(input.contentPropertyNameUpdated).value)){
							return true;
					}else{
						return false;
					}
			})
			.forEach(content ->{
				content.properties.get(input.contentPropertyNameUpdated).value = input.newValue;
			});
		
	}
	
	private void updateOutputExperimentProperties(Experiment exp,
			NGLObject input) {
		exp.atomicTransfertMethods
			.stream()
			.filter(atm -> atm.outputContainerUseds != null)			
			.map(atm -> atm.outputContainerUseds)
			.flatMap(List::stream)
			.filter(ocu -> ocu.experimentProperties != null)
			.map(ocu -> ocu.experimentProperties.entrySet())
			.flatMap(Set::stream)
			.filter(entry -> (entry.getKey().equals(input.contentPropertyNameUpdated) && entry.getValue().value.equals(input.currentValue)))
			.forEach(entry ->{
				entry.getValue().value = input.newValue;
			});
			
		
	}
}
