package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.instrument.description.Instrument;
import models.utils.ListObject;
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
		String sql = "SELECT id,name,code FROM instrument WHERE fk_instrument_used_type=?";
		BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentUsedType);
	}
	
	public List<ListObject> findAllForList(){
		String sql = "SELECT code , name,  FROM instrument";
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	public Instrument save(Instrument instrument, long idInstrumentUsedType)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrument.name);
        parameters.put("code", instrument.code);
        parameters.put("fk_instrument_used_type", idInstrumentUsedType);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrument.id = newId;
        return instrument;
	}
	
}
