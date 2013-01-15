package models.description.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.ObjectType;
import models.description.common.PropertyDefinition;
import models.description.common.Resolution;
import models.description.common.State;

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
		commonInfoType.setId(rs.getLong("cId"));
		commonInfoType.setName(rs.getString("name"));
		commonInfoType.setCode(rs.getString("code"));
		commonInfoType.setCollectionName(rs.getString("collection_name"));
		//Get object Type
		ObjectType objectType = new ObjectType();
		objectType.setId(rs.getLong("oId"));
		objectType.setType(rs.getString("type"));
		objectType.setGeneric(rs.getBoolean("generic"));
		commonInfoType.setObjectType(objectType);

		//Get variables State
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		List<State> states = stateDAO.findByCommonInfoType(commonInfoType.getId());
		commonInfoType.setVariableStates(states);

		//Get Resolutions
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		List<Resolution> resolutions = resolutionDAO.findByCommonInfoType(commonInfoType.getId());
		commonInfoType.setResolutions(resolutions);

		//Get properties
		PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		List<PropertyDefinition> properties = propertyDefinitionDAO.findByCommonInfoType(commonInfoType.getId());
		commonInfoType.setPropertiesDefinition(properties);
		return commonInfoType;
	}

}
