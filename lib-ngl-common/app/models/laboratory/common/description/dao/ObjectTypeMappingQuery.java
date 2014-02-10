package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class ObjectTypeMappingQuery extends MappingSqlQuery<ObjectType>{

	public ObjectTypeMappingQuery(){
		super();
	}
	
	public ObjectTypeMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter){
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected ObjectType mapRow(ResultSet rs, int rowNumber) throws SQLException {
		
			ObjectType objectType = new ObjectType();
			objectType.id = rs.getLong("oId");
			objectType.code = rs.getString("codeObject");
			objectType.generic =rs.getBoolean("generic");
			
			//Get variables State
			StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
			List<State> states = null;
			try {
				states = stateDAO.findByObjectTypeId(objectType.id);
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			objectType.states = states;

			return objectType;
	}

}