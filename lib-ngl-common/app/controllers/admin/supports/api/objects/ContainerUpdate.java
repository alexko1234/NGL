package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.Analysis;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;

public class ContainerUpdate extends AbstractUpdate<Container>{

	public ContainerUpdate() {
		super(InstanceConstants.CONTAINER_COLL_NAME, Container.class);		
	}

	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, ""));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		query = DBQuery.elemMatch("contents", query);
		
		if(CollectionUtils.isNotEmpty(form.codes)){
			query.and(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.codeRegex)){
			query.and(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
			
		
		
		return query;
	}

	@Override
	public void update(NGLObject input, ContextValidation cv) {
		Container container = getObject(input.code);
		if(NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))){
			updateContent(container, input);
			
		}else{
			throw new RuntimeException(input.action+" not implemented");
		}
		container.validate(cv);
		if(!cv.hasErrors()){
			updateObject(container);
		}
	}

	private void updateContent(Container container, NGLObject input) {
		
		container.contents.stream()
			.filter(c -> {
				if(input.projectCode.equals(c.projectCode) &&
					input.sampleCode.equals(c.sampleCode) &&
					input.currentValue.equals(c.properties.get(input.contentPropertyNameUpdated).value)){
						return true;
				}else{
					return false;
				}
			})
			.forEach(c -> {
				c.properties.get(input.contentPropertyNameUpdated).value = input.newValue;
			});
		
	}

	@Override
	public Long getNbOccurrence(NGLObject input) {
		Container container = getObject(input.code);
		return container.contents.stream()
		.filter(c -> {
			if(input.projectCode.equals(c.projectCode) &&
				input.sampleCode.equals(c.sampleCode) &&
				input.currentValue.equals(c.properties.get(input.contentPropertyNameUpdated).value)){
					return true;
			}else{
				return false;
			}
		})
		.count();
		
	}
	

}
