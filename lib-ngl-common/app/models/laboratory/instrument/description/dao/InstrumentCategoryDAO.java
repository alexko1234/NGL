package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.laboratory.instrument.description.InstrumentCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class InstrumentCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("instrument_category").usingGeneratedKeyColumns("id");
	}
	
	public InstrumentCategory findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM instrument_category "+
				"WHERE id=?";
		BeanPropertyRowMapper<InstrumentCategory> mapper = new BeanPropertyRowMapper<InstrumentCategory>(InstrumentCategory.class);
		InstrumentCategory instrumentCategory = this.jdbcTemplate.queryForObject(sql, mapper, id);
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		//Find inContainerSupportCategories
		instrumentCategory.inContainerSupportCategories = containerSupportCategoryDAO.findInByInstrumentCategory(instrumentCategory.id);
		//Find outContainerSupportCategorie
		instrumentCategory.outContainerSupportCategories = containerSupportCategoryDAO.findOutByInstrumentCategory(instrumentCategory.id);
		return instrumentCategory;
	}
	
	
	public InstrumentCategory add(InstrumentCategory instrumentCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrumentCategory.name);
        parameters.put("code", instrumentCategory.code);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrumentCategory.id = newId;
        
        //Add in and out containerSupportCategories
        if(instrumentCategory.inContainerSupportCategories!=null && instrumentCategory.inContainerSupportCategories.size()>0){
        	String sqlIn = "INSERT INTO instrumentCategory_inContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
        	for(ContainerSupportCategory containerSupportCategory : instrumentCategory.inContainerSupportCategories){
        		jdbcTemplate.update(sqlIn, instrumentCategory.id, containerSupportCategory.id);
        	}
        }
        if(instrumentCategory.outContainerSupportCategories!=null && instrumentCategory.outContainerSupportCategories.size()>0){
        	String sqlOut = "INSERT INTO instrumentCategory_outContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
        	for(ContainerSupportCategory containerSupportCategory : instrumentCategory.outContainerSupportCategories){
        		jdbcTemplate.update(sqlOut, instrumentCategory.id, containerSupportCategory.id);
        	}
        }
        
        return instrumentCategory;
	}
}
