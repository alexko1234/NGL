package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.instrument.description.Instrument;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InstrumentDAO extends AbstractDAO<Instrument>{

	protected InstrumentDAO() {
		super("instrument", Instrument.class, true);
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
        parameters.put("name", instrument.name);
        parameters.put("code", instrument.code);
        parameters.put("instrument_used_type_id", idInstrumentUsedType);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrument.id = newId;
        return instrument;
	}
	
}
