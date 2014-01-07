package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

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
		return state;
	}

}
