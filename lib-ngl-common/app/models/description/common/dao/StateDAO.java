package models.description.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.description.common.State;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StateDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
		
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("state").usingGeneratedKeyColumns("id");
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
	
	public State findById(long idState)
	{
		String sql = "SELECT id,name,code,active,priority "+
					"FROM state "+
					"WHERE id=?";
		BeanPropertyRowMapper<State> mapper = new BeanPropertyRowMapper<State>(State.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, idState);
	}
	
	public List<State> findAll()
	{
		String sql = "SELECT id,name,code,active,priority "+
					"FROM state";
		BeanPropertyRowMapper<State> mapper = new BeanPropertyRowMapper<State>(State.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	public State add(State state)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", state.getName());
        parameters.put("code", state.getCode());
        parameters.put("active", state.getActive());
        parameters.put("priority", state.getPriority());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        state.setId(newId);
        return state;
	}
	
	/**
	 * Code can not be changed
	 * @param state
	 * @return
	 */
	public void update(State state)
	{
		String sql = "UPDATE state SET name=?, active=?, priority=? WHERE id=?";
		jdbcTemplate.update(sql, state.getName(), state.getActive(), state.getPriority(), state.getId());
	}
}
