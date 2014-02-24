package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.instrument.description.Instrument;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import controllers.instruments.api.InstrumentsSearchForm;

@Repository
public class InstrumentDAO extends AbstractDAODefault<Instrument>{

	protected InstrumentDAO() {
		super("instrument", Instrument.class, true, true);		
	}

	public Instrument save(Instrument instrument, long idInstrumentUsedType) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrument.name);
        parameters.put("code", instrument.code);
        parameters.put("fk_instrument_used_type", idInstrumentUsedType);
        parameters.put("active", instrument.active);
        parameters.put("path", instrument.path);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrument.id = newId;
        
        insertInstitutes(instrument.institutes, instrument.id, false);
        
        return instrument;
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
		String sql = getSqlCommon() + " WHERE t.fk_instrument_used_type=? and t.active=1";
		BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentUsedType);
	}
	
	public List<Instrument> findByInstrumentCategoryCodesAndInstrumentUsedTypeCodes(InstrumentsSearchForm instumentSearchForm,  boolean active) throws DAOException {
		Object[] parameters = new Object[]{active};
		
		String sql = getSqlCommon()  + " inner join instrument_used_type iut on iut.id = t.fk_instrument_used_type"
				+ " inner join instrument_category ic on ic.id = iut.fk_instrument_category"
				+" inner join common_info_type cit on cit.id = iut.fk_common_info_type"
				+" where t.active=?";
		
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
