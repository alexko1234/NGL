package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.instance.AbstractContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import validation.utils.ValidationHelper;
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
		
		if(CollectionUtils.isNotEmpty(form.codes)){
			query.and(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.codeRegex)){
			query.and(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Experiment exp = getObject(input.code);
		PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
		Object currentValue = ValidationHelper.convertStringToType(pd.valueType, input.currentValue);
		Object newValue = ValidationHelper.convertStringToType(pd.valueType, input.newValue);
		
		//1 update input containers
		if(NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))){
			updateInputContainers(exp, input, pd, currentValue, newValue);
			updateOutputContainers(exp, input, pd, currentValue, newValue);
			updateOutputExperimentProperties(exp, input, pd, currentValue, newValue);			
		}else{
			throw new RuntimeException(input.action+" not implemented");
		}
		
		exp.validate(cv);
		if(!cv.hasErrors()){
			updateObject(exp);
		}		
	}

	private void updateInputContainers(Experiment exp,
			NGLObject input, PropertyDefinition pd, Object currentValue, Object newValue) {
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
						currentValue.equals(ValidationHelper.convertStringToType(pd.valueType, content.properties.get(input.contentPropertyNameUpdated).value.toString()))){
							return true;
					}else{
						return false;
					}
			})
			.forEach(content ->{
				content.properties.get(input.contentPropertyNameUpdated).value = newValue;
			});
		
	}
	
	private void updateOutputContainers(Experiment exp,
			NGLObject input, PropertyDefinition pd, Object currentValue, Object newValue) {
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
						currentValue.equals(ValidationHelper.convertStringToType(pd.valueType, content.properties.get(input.contentPropertyNameUpdated).value.toString()))){
							return true;
					}else{
						return false;
					}
			})
			.forEach(content ->{
				content.properties.get(input.contentPropertyNameUpdated).value = newValue;
			});
		
	}
	
	private void updateOutputExperimentProperties(Experiment exp,
			NGLObject input, PropertyDefinition pd, Object currentValue, Object newValue) {
		exp.atomicTransfertMethods
			.stream()
			.filter(atm -> atm.outputContainerUseds != null)			
			.map(atm -> atm.outputContainerUseds)
			.flatMap(List::stream)
			.filter(ocu -> ocu.experimentProperties != null)
			.map(ocu -> ocu.experimentProperties.entrySet())
			.flatMap(Set::stream)
			.filter(entry -> (entry.getKey().equals(input.contentPropertyNameUpdated) && ValidationHelper.convertStringToType(pd.valueType, entry.getValue().value.toString()).equals(currentValue)))
			.forEach(entry ->{
				entry.getValue().value = newValue;
			});					
	}

	@Override
	public Long getNbOccurrence(NGLObject input) {
		Experiment exp = getObject(input.code);
		PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
		Object value = ValidationHelper.convertStringToType(pd.valueType, input.currentValue);
		
		Long count = exp.atomicTransfertMethods
			.stream()
			.map(atm -> atm.inputContainerUseds)
			.flatMap(List::stream)
			.map(icu -> icu.contents)
			.flatMap(List::stream)
			.filter(content -> {
				if(input.projectCode.equals(content.projectCode) &&
						input.sampleCode.equals(content.sampleCode) &&
						content.properties.containsKey(input.contentPropertyNameUpdated) && 
						value.equals(ValidationHelper.convertStringToType(pd.valueType, content.properties.get(input.contentPropertyNameUpdated).value.toString()))){
							return true;
					}else{
						return false;
					}
			})
			.count();
		
		count = count + exp.atomicTransfertMethods
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
							value.equals(ValidationHelper.convertStringToType(pd.valueType, content.properties.get(input.contentPropertyNameUpdated).value.toString()))){
								return true;
						}else{
							return false;
						}
				})
				.count();
		
		count = count + exp.atomicTransfertMethods
				.stream()
				.filter(atm -> atm.outputContainerUseds != null)			
				.map(atm -> atm.outputContainerUseds)
				.flatMap(List::stream)
				.filter(ocu -> ocu.experimentProperties != null)
				.map(ocu -> ocu.experimentProperties.entrySet())
				.flatMap(Set::stream)
				.filter(entry -> (entry.getKey().equals(input.contentPropertyNameUpdated) && ValidationHelper.convertStringToType(pd.valueType, entry.getValue().value.toString()).equals(value)))
				.count();	
		
		return count;
	}
}
