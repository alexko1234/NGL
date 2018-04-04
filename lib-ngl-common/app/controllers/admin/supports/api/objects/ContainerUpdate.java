package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

public class ContainerUpdate extends AbstractUpdate<Container> {

	public ContainerUpdate() {
		super(InstanceConstants.CONTAINER_COLL_NAME, Container.class);		
	}

	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, ""));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		query = DBQuery.elemMatch("contents", query);
		
		if (CollectionUtils.isNotEmpty(form.codes)) {
			query.and(DBQuery.in("code", form.codes));
		} else if(StringUtils.isNotBlank(form.codeRegex)) {
			query.and(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		return query;
	}

	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Container container = getObject(input.code);
		if (NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))) {
			updateContent(container, input);
			
		} else {
			throw new RuntimeException(input.action+" not implemented");
		}
		container.validate(cv);
		if (!cv.hasErrors()) {
			updateObject(container);
		}
	}

	private void updateContent(Container container, NGLObject input) {
		
		PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
		Object currentValue = ValidationHelper.convertStringToType(pd.valueType, input.currentValue);
		Object newValue = ValidationHelper.convertStringToType(pd.valueType, input.newValue);
		
		
		container.contents.stream()
			.filter(c -> {
				if(input.projectCode.equals(c.projectCode) &&
					input.sampleCode.equals(c.sampleCode) &&
					currentValue.equals(ValidationHelper.convertStringToType(pd.valueType, c.properties.get(input.contentPropertyNameUpdated).value.toString()))){
						return true;
				}else{
					return false;
				}
			})
			.forEach(c -> {
				// c.properties.get(input.contentPropertyNameUpdated).value = newValue;
				c.properties.get(input.contentPropertyNameUpdated).assignValue(newValue);
			});
		
	}

	@Override
	public Long getNbOccurrence(NGLObject input) {
		Container container = getObject(input.code);
		
		PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
		Object value = ValidationHelper.convertStringToType(pd.valueType, input.currentValue);
		return container.contents.stream()
		.filter(c -> {
			if(input.projectCode.equals(c.projectCode) &&
				input.sampleCode.equals(c.sampleCode) &&
				value.equals(ValidationHelper.convertStringToType(pd.valueType, c.properties.get(input.contentPropertyNameUpdated).value.toString()))){
					return true;
			}else{
				return false;
			}
		})
		.count();
		
	}
	

}
