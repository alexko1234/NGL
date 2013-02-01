package models.laboratory.container.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.container.description.ContainerSupportCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContainerSupportCategoryDAO{

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("container_support_category").usingGeneratedKeyColumns("id");
	}

	public ContainerSupportCategory add(ContainerSupportCategory containerSupportCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", containerSupportCategory.name);
		parameters.put("code", containerSupportCategory.code);
		parameters.put("nbUsableContainer", containerSupportCategory.nbUsableContainer);
		parameters.put("nbLine", containerSupportCategory.nbLine);
		parameters.put("nbColumn", containerSupportCategory.nbColumn);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		containerSupportCategory.id = newId;
		return containerSupportCategory;
	}


	public void update(ContainerSupportCategory containerSupportCategory)
	{
		String sql = "UPDATE container_support_category SET name=?, nbUsableContainer=?, nbLine=?, nbColumn=? WHERE id=?";
		jdbcTemplate.update(sql, containerSupportCategory.name, containerSupportCategory.nbUsableContainer, containerSupportCategory.nbLine, containerSupportCategory.nbColumn, containerSupportCategory.id);
	}


	public ContainerSupportCategory findByCode(String code)
	{
		String sql = "SELECT id,name,code,nbUsableContainer,nbLine,nbColumn " +
				"FROM container_support_category "+
				"WHERE code=?";
		BeanPropertyRowMapper<ContainerSupportCategory> mapper = new BeanPropertyRowMapper<ContainerSupportCategory>(ContainerSupportCategory.class);
		ContainerSupportCategory category = this.jdbcTemplate.queryForObject(sql, mapper, code);
		return category;
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
