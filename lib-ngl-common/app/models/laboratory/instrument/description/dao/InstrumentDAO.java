package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.instrument.description.Instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InstrumentDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);   
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("instrument").usingGeneratedKeyColumns("id");
	}
	
	public List<Instrument> findByInstrumentUsedType(long idInstrumentUsedType)
	{
		String sql = "SELECT id,name,code FROM instrument WHERE instrument_used_type_id=?";
		BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentUsedType);
	}
	
	public Instrument add(Instrument instrument, long idInstrumentUsedType)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrument.getName());
        parameters.put("code", instrument.getCode());
        parameters.put("instrument_used_type_id", idInstrumentUsedType);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrument.setId(newId);
        return instrument;
	}
	
	public void update(Instrument instrument)
	{
		String sql = "UPDATE instrument SET name=? WHERE id=?";
		jdbcTemplate.update(sql, instrument.getName(), instrument.getId());
	}
}
