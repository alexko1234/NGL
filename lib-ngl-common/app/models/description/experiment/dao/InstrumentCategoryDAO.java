package models.description.experiment.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.description.content.ContainerSupportCategory;
import models.description.content.dao.ContainerSupportCategoryDAO;
import models.description.experiment.InstrumentCategory;

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
		instrumentCategory.setInContainerSupportCategories(containerSupportCategoryDAO.findInByInstrumentCategory(instrumentCategory.getId()));
		//Find outContainerSupportCategorie
		instrumentCategory.setOutContainerSupportCategories(containerSupportCategoryDAO.findOutByInstrumentCategory(instrumentCategory.getId()));
		return instrumentCategory;
	}
	
	
	public InstrumentCategory add(InstrumentCategory instrumentCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", instrumentCategory.getName());
        parameters.put("code", instrumentCategory.getCode());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        instrumentCategory.setId(newId);
        
        //Add in and out containerSupportCategories
        if(instrumentCategory.getInContainerSupportCategories()!=null && instrumentCategory.getInContainerSupportCategories().size()>0){
        	String sqlIn = "INSERT INTO instrumentCategory_inContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
        	for(ContainerSupportCategory containerSupportCategory : instrumentCategory.getInContainerSupportCategories()){
        		jdbcTemplate.update(sqlIn, instrumentCategory.getId(), containerSupportCategory.getId());
        	}
        }
        if(instrumentCategory.getOutContainerSupportCategories()!=null && instrumentCategory.getOutContainerSupportCategories().size()>0){
        	String sqlOut = "INSERT INTO instrumentCategory_outContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
        	for(ContainerSupportCategory containerSupportCategory : instrumentCategory.getOutContainerSupportCategories()){
        		jdbcTemplate.update(sqlOut, instrumentCategory.getId(), containerSupportCategory.getId());
        	}
        }
        
        return instrumentCategory;
	}
}
