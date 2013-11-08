package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class ResolutionDAO extends AbstractDAOMapping<Resolution>{

	protected ResolutionDAO() {
		super("resolution", Resolution.class, ResolutionMappingQuery.class,
				"SELECT t.id, t.name, t.code, t.fk_resolution_category FROM resolution as t ",
				true);
	}
	
	@Override
	public void remove(Resolution resolution) throws DAOException
	{
		//Remove list resolution for common_info_type
		String sqlState = "DELETE FROM common_info_type_resolution WHERE fk_resolution=?";
		jdbcTemplate.update(sqlState, resolution.id);
		//remove resolution
		super.remove(resolution);
	}

	@Override
	public long save(Resolution resolution) throws DAOException {
		//Check if category exist
		if(resolution.category == null || resolution.category.id == null){
			throw new IllegalArgumentException("ResolutionCategory is not present ");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", resolution.code);
		parameters.put("name", resolution.name);
		parameters.put("fk_resolution_category", resolution.category.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		resolution.id = newId;
		return resolution.id;
	}

	@Override
	public void update(Resolution resolution) throws DAOException {
		String sql = "UPDATE resolution SET code=?, name=? WHERE id=?";
		jdbcTemplate.update(sql, resolution.code, resolution.name);
		
	}
	
	public List<Resolution> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = "SELECT id,name,code "+
				"FROM resolution "+
				"JOIN common_info_type_resolution ON fk_resolution=id "+
				"WHERE fk_common_info_type=?";
		BeanPropertyRowMapper<Resolution> mapper = new BeanPropertyRowMapper<Resolution>(Resolution.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}

	public List<Resolution> findByCategoryCode(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		String sql = sqlCommon+" inner join resolution_category r on r.id = t.fk_resolution_category and r.code = ? ";
		return initializeMapping(sql, new SqlParameter("code", Types.VARCHAR)).execute(code);		
	}


}
