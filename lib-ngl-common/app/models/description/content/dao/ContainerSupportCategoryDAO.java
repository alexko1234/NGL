package models.description.content.dao;

import java.util.List;

import javax.sql.DataSource;

import models.description.content.ContainerSupportCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContainerSupportCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	public List<ContainerSupportCategory> findInByInstrumentCategory(long idInstrumentCategory)
	{
		String sql = "SELECT id,name,code,nbUsableContainer,nbLine,nbColumn " +
				"FROM container_support_category "+
				"JOIN instrumentCategory_inContainerSupportCategory ON fk_container_support_category=id "+
				"WHERE fk_instrument_category=?";
		BeanPropertyRowMapper<ContainerSupportCategory> mapper = new BeanPropertyRowMapper<ContainerSupportCategory>(ContainerSupportCategory.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentCategory);
	}
	
	public List<ContainerSupportCategory> findOutByInstrumentCategory(long idInstrumentCategory)
	{
		String sql = "SELECT id, name, code, nbUsableContainer,nbLine,nbColumn "+
					"FROM container_support_category "+
					"JOIN instrumentCategory_outContainerSupportCategory ON fk_container_support_category=id "+
					"WHERE fk_instrument_category=?";
		BeanPropertyRowMapper<ContainerSupportCategory> mapper = new BeanPropertyRowMapper<ContainerSupportCategory>(ContainerSupportCategory.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentCategory);
	}
}
