package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import models.laboratory.common.description.MeasureUnit;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

@Repository
public class MeasureUnitDAO extends AbstractDAOMapping<MeasureUnit> {

//	protected MeasureUnitDAO() {
//		super("measure_unit", MeasureUnit.class, MeasureUnitMappingQuery.class,
//				"SELECT t.id, code, value, default_unit, fk_measure_category "+
//						"FROM measure_unit as t ",true);
//	}
	protected MeasureUnitDAO() {
		super("measure_unit", MeasureUnit.class, MeasureUnitMappingQuery.factory,
				"SELECT t.id, code, value, default_unit, fk_measure_category "+
						"FROM measure_unit as t ",true);
	}

/*
	public List<MeasureUnit> findByMeasureCategory(long idMeasureCategory) {
		String sql = "SELECT id, code, value, default_unit " +
				"FROM measure_unit "+
				"WHERE fk_measure_category=?";
		BeanPropertyRowMapper<MeasureUnit> mapper = new BeanPropertyRowMapper<MeasureUnit>(MeasureUnit.class);
		return this.jdbcTemplate.query(sql, mapper, idMeasureCategory);
	}
*/
	public MeasureUnit findByValue(String value) {
		String sql = "SELECT id, code, value,default_unit " +
				"FROM measure_unit "+
				"WHERE value = ?";
		BeanPropertyRowMapper<MeasureUnit> mapper = new BeanPropertyRowMapper<>(MeasureUnit.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, value);
	}

	@Override
	public long save(MeasureUnit measureValue) throws DAOException {
		if (measureValue == null) 
			throw new IllegalArgumentException("measureValue is mandatory");
		//Check if category exist
		if (measureValue.category == null || measureValue.category.id == null)
			throw new IllegalArgumentException("MeasureCategory is not present !!");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("code", measureValue.code);
		parameters.put("value", measureValue.value);
		parameters.put("default_unit", measureValue.defaultUnit);
		parameters.put("fk_measure_category", measureValue.category.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		measureValue.id = newId;
		return measureValue.id;
	}


	@Override
	public void update(MeasureUnit measureValue) throws DAOException {
		String sql = "UPDATE measure_unit SET code=?, value=?, default_unit=? WHERE id=?";
		jdbcTemplate.update(sql, measureValue.code, measureValue.value, measureValue.defaultUnit, measureValue.id);
	}

}
