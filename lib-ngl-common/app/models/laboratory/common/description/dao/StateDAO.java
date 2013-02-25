package models.laboratory.common.description.dao;

import java.util.List;

import models.laboratory.common.description.State;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class StateDAO extends AbstractDAO<State>{

	protected StateDAO() {
		super("state", State.class,true);
	}

	public List<State> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = "SELECT id,name,code,active,priority " +
				"FROM state "+
				"JOIN common_info_type_state ON fk_state=id "+
				"WHERE fk_common_info_type=?";
		BeanPropertyRowMapper<State> mapper = new BeanPropertyRowMapper<State>(State.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}

	@Override
	public void remove(State state)
	{
		//Remove list state for common_info_type
		String sqlState = "DELETE FROM common_info_type_state WHERE fk_state=?";
		jdbcTemplate.update(sqlState, state.id);
		//remove resolution
		super.remove(state);

	}

}
