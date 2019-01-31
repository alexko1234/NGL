package controllers.projects.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.DBObjectListForm;
import controllers.ListObject;
import models.laboratory.project.instance.Project;


public class ProjectsSearchForm extends DBObjectListForm<Project> {

	public List<String> projectCodes;
	public String projectCode;

	public List<String> stateCodes;
	public String stateCode;

	public List<String> typeCodes;
	
	public List<String> fgGroups;
	
	public Boolean isFgGroup;
	
	public List<String> unixGroups;
	
	
	public Set<String> existingFields, notExistingFields;


	@Override
	@JsonIgnore
	public Query getQuery() {
		List<Query> queries = new ArrayList<>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(this.projectCodes)){
			queries.add(DBQuery.in("code", this.projectCodes));
		} else if(StringUtils.isNotBlank(this.projectCode)){
			queries.add(DBQuery.is("code", this.projectCode));
		}
		
		if(CollectionUtils.isNotEmpty(this.fgGroups)){
			queries.add(DBQuery.in("biointhisaticParameters.fgGroup", this.fgGroups));
		}
		
		if (this.isFgGroup != null) {
			if(this.isFgGroup){
				queries.add(DBQuery.exists("biointhisaticParameters.fgGroup"));
			} else{
				queries.add(DBQuery.notExists("biointhisaticParameters.fgGroup"));
			}
		}
		
		if (StringUtils.isNotBlank(this.stateCode)) { //all
			queries.add(DBQuery.is("state.code", this.stateCode));
		} else if (CollectionUtils.isNotEmpty(this.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", this.stateCodes));
		}
		
		if (CollectionUtils.isNotEmpty(this.unixGroups)) {
			queries.add(DBQuery.in("properties.unixGroup.value", this.unixGroups));
		}
		
		if (CollectionUtils.isNotEmpty(this.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", this.typeCodes));
		}
		
		
		if (CollectionUtils.isNotEmpty(this.existingFields)) { //all
			for(String field : this.existingFields){
				queries.add(DBQuery.exists(field));
			}		
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}


	@Override
	@JsonIgnore
	public Function<Project, ListObject> conversion() {
		return o -> { return new ListObject(o.code, o.name); };
	}
	
	

}

