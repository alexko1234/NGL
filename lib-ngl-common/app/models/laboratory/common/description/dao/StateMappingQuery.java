package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import models.laboratory.common.description.ResolutionCategory;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class StateMappingQuery extends MappingSqlQuery<State>{

	public StateMappingQuery() {
		super();
	}
	
	public StateMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected State mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		State state = new State();
		state.id=rs.getLong("id");
		state.code=rs.getString("code");
		state.name=rs.getString("name");
		state.active=rs.getBoolean("active");
		state.position=rs.getInt("position");
		
		long idCategory = rs.getLong("fk_state_category");
		StateCategory category = null;
		try {
			category = StateCategory.find.findById(idCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		state.category=category;
		return state;
	}

}
