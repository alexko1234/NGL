package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class CommonInfoTypeMappingQuery extends MappingSqlQuery<CommonInfoType>{

	public CommonInfoTypeMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter){
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected CommonInfoType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		CommonInfoType commonInfoType = new CommonInfoType();
		commonInfoType.id = rs.getLong("cId");
		commonInfoType.name = rs.getString("name");
		commonInfoType.code = rs.getString("code");
		commonInfoType.collectionName = rs.getString("collection_name");
		//Get object Type
		ObjectType objectType = new ObjectType();
		objectType.id = rs.getLong("oId");
		objectType.type = rs.getString("type");
		objectType.generic =rs.getBoolean("generic");
		commonInfoType.objectType = objectType;

		//Get variables State
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		List<State> states = stateDAO.findByCommonInfoType(commonInfoType.id);
		commonInfoType.variableStates = states;

		//Get Resolutions
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		List<Resolution> resolutions = resolutionDAO.findByCommonInfoType(commonInfoType.id);
		commonInfoType.resolutions = resolutions;

		//Get properties
		PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		List<PropertyDefinition> properties = propertyDefinitionDAO.findByCommonInfoType(commonInfoType.id);
		commonInfoType.propertiesDefinitions=properties;
		return commonInfoType;
	}

}
