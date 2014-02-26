package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.instrument.description.Instrument;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import controllers.instruments.api.InstrumentsSearchForm;

@Repository
public class InstrumentDAO extends AbstractDAOMapping<Instrument>{

	protected InstrumentDAO() {
		super("instrument", Instrument.class, InstrumentMappingQuery.class,
				"SELECT distinct t.id, t.name, t.code, t.active, t.path, t.fk_instrument_used_type FROM instrument as t "+DAOHelpers.getInstrumentSQLForInstitute("t"),
				true);				
	}
	
	@Override
	public long save(Instrument instrument) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrument.name);
        parameters.put("code", instrument.code);
        parameters.put("fk_instrument_used_type", instrument.instrumentUsedType.id);
        parameters.put("active", instrument.active);
        parameters.put("path", instrument.path);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrument.id = newId;
        
        insertInstitutes(instrument.institutes, instrument.id, false);
        
        return instrument.id;
	}
	
	@Override
	public void update(Instrument instrument) throws DAOException {
		String sql = "UPDATE instrument SET code=?, name=?, fk_instrument_used_type =?, active=?, path=? WHERE id=?";
		jdbcTemplate.update(sql, instrument.code, instrument.name, instrument.instrumentUsedType.id, instrument.active, instrument.path, instrument.id);
	}
	
	private void insertInstitutes(List<Institute> institutes, Long instrumentId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeInstitutes(instrumentId);
		}
		//Add institutes list		
		if(institutes!=null && institutes.size()>0){
			String sql = "INSERT INTO instrument_institute (fk_instrument, fk_institute) VALUES (?,?)";
			for(Institute institute : institutes){
				if(institute == null || institute.id == null ){
					throw new DAOException("institute is mandatory");
				}
				jdbcTemplate.update(sql, instrumentId, institute.id);
			}
		}
	}
	
	private void removeInstitutes( long instrumentId) {
		String sql = "DELETE FROM instrument_institute WHERE fk_instrument=?";
		jdbcTemplate.update(sql, instrumentId);
	}
	
	
	public void remove(Instrument instrument) throws DAOException {
		if(null == instrument){
			throw new IllegalArgumentException("instrument is null");
		}
		
		//remove institute
		removeInstitutes(instrument.id);

		super.remove(instrument);
	}
	
	public List<Instrument> findByInstrumentUsedType(long idInstrumentUsedType) throws DAOException {
		String sql = sqlCommon + " WHERE t.fk_instrument_used_type=? and t.active=1";
		BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentUsedType);
	}
	
	public List<Instrument> findByInstrumentCategoryCodesAndInstrumentUsedTypeCodes(InstrumentsSearchForm instumentSearchForm,  Boolean active) throws DAOException {
		Object[] parameters = new Object[0];
		
		
		String sql = sqlCommon  + " inner join instrument_used_type iut on iut.id = t.fk_instrument_used_type"
				+ " inner join instrument_category ic on ic.id = iut.fk_instrument_category"
				+" inner join common_info_type cit on cit.id = iut.fk_common_info_type"
				+" where 1=1 ";
		
		if(null != active){
			ArrayUtils.add(parameters, active);
			sql += " and t.active=?";
		}
		
		
		if(instumentSearchForm.instrumentUsedTypeCodes != null){
			parameters = ArrayUtils.addAll(parameters, instumentSearchForm.instrumentUsedTypeCodes.toArray());
			sql += " and  cit.code= ("+listToParameters(instumentSearchForm.instrumentUsedTypeCodes)+") ";
		}
			
		
		if(instumentSearchForm.instrumentCategoryCodes != null){
			parameters = ArrayUtils.addAll(parameters, instumentSearchForm.instrumentCategoryCodes.toArray());
			sql += " and ic.code= ("+listToParameters(instumentSearchForm.instrumentCategoryCodes)+") ";
		}
		
		if(instumentSearchForm.instrumentCategoryCode != null){
			Object[] args = new Object[]{instumentSearchForm.instrumentCategoryCode};
			parameters = ArrayUtils.addAll(parameters,args);
			sql += " and ic.code=? ";
			
		}
		
		if(instumentSearchForm.instrumentUsedTypeCode != null){
			Object[] args = new Object[]{instumentSearchForm.instrumentUsedTypeCode};
			parameters = ArrayUtils.addAll(parameters,args);
			sql += " and  cit.code=? ";
		}
		
		BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
		
		return this.jdbcTemplate.query(sql, mapper, parameters);
	}
	
}
