package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class CommonInfoTypeMappingQuery extends MappingSqlQuery<CommonInfoType>{

	public CommonInfoTypeMappingQuery(){
		super();
	}
	
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
			commonInfoType.code = rs.getString("codeSearch");
			commonInfoType.displayOrder = rs.getInt("displayOrder");
			//Get object Type
			ObjectType objectType = new ObjectType();
			objectType.id = rs.getLong("oId");
			objectType.code = rs.getString("codeObject");
			objectType.generic =rs.getBoolean("generic");
			commonInfoType.objectType = objectType;

			//Get variables State
			/*
			StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
			List<State> states = null;
			try {
				states = stateDAO.findByCommonInfoType(commonInfoType.id);
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				Logger.error("DAO error: "+e.getMessage(),e);;
			}
			commonInfoType.states = states;
			*/
			
			
			//Get properties
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			List<PropertyDefinition> properties = propertyDefinitionDAO.findByCommonInfoType(commonInfoType.id);
			commonInfoType.propertiesDefinitions=properties;
			
			
			//Get Institutes
			/*
			InstituteDAO instituteDAO = Spring.getBeanOfType(InstituteDAO.class);
			List<Institute> institutes = instituteDAO.findByCommonInfoType(commonInfoType.id);
			commonInfoType.institutes = institutes;
			*/
			//Get Valuation
			/*
			ValuationCriteriaDAO valuationCriteriaDAO = Spring.getBeanOfType(ValuationCriteriaDAO.class);
			List<ValuationCriteria> valuationCriterias = valuationCriteriaDAO.findByCommonInfoType(commonInfoType.id);
			commonInfoType.criterias = valuationCriterias;
			 */
			return commonInfoType;
	}

}
