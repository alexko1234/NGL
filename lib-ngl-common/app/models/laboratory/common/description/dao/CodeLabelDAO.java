package models.laboratory.common.description.dao;

import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CodeLabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class CodeLabelDAO {

	protected DataSource dataSource;
	protected SimpleJdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("ngl")
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);   		
	}
	
	public List<CodeLabel> findAll(){
		String sql = "select 'state' as table_name, code, name as label from state "+
						"union all "+
						"select 'resolution' as table_name, code, name as label from resolution "+
						"union all "+
						"select 'type' as table_name, code, name as label from common_info_type "+
						"union all "+
						"select 'instrument' as table_name, code, name as label from instrument "+
						"union all "+
						"select 'instrument_cat' as table_name, code, name as label from instrument_category "+
						"union all "+
						"select 'valuation' as table_name, code, name as label from valuation";
		BeanPropertyRowMapper<CodeLabel> mapper = new BeanPropertyRowMapper<CodeLabel>(CodeLabel.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
}
