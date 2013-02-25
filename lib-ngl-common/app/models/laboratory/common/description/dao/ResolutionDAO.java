package models.laboratory.common.description.dao;

import java.util.List;

import models.laboratory.common.description.Resolution;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ResolutionDAO extends AbstractDAO<Resolution>{

	protected ResolutionDAO() {
		super("resolution", Resolution.class,true);
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

	@Override
	public void remove(Resolution resolution)
	{
		//Remove list resolution for common_info_type
		String sqlState = "DELETE FROM common_info_type_resolution WHERE fk_resolution=?";
		jdbcTemplate.update(sqlState, resolution.id);
		//remove resolution
		super.remove(resolution);


	}


}
