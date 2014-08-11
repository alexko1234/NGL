package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateHierarchy;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class StateHierarchyMappingQuery extends MappingSqlQuery<StateHierarchy>{

	public StateHierarchyMappingQuery() {
		super();
	}
	
	public StateHierarchyMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected StateHierarchy mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		
		StateHierarchy stateHierarchy = new StateHierarchy();
		
		stateHierarchy.id=rs.getLong("id");
		stateHierarchy.code=rs.getString("code");
		
		long id = rs.getLong("fk_child_state");
		State state = null;
		try {
			state = State.find.findById(id);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		stateHierarchy.childStateCode=state.code;
		
		stateHierarchy.childStateName = state.name;
		
		id = rs.getLong("fk_parent_state");
		state = null;
		try {
			state = State.find.findById(id);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		stateHierarchy.parentStateCode=state.code;

		id = rs.getLong("fk_object_type");
		ObjectType ot = null;
		try {
			ot = ObjectType.find.findById(id);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		stateHierarchy.objectTypeCode=ot.code;
		
		
		return stateHierarchy;
	}

}
