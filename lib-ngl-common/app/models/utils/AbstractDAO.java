package models.utils;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDAO<P, T> {
	
	protected String tableName;
	protected SimpleJdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	
	protected AbstractDAO(String tableName) {
		this.tableName = tableName;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName).usingGeneratedKeyColumns("id");
	}
	public abstract List<T> findAll();
	
	public abstract T findById(P id);
	
	public abstract T findByCode(String code);
	
	@Transactional	
	public abstract T update(T value);
	@Transactional	
	public abstract T add(T value);
	
	@Transactional	
	public abstract void remove(T value);
	
	

}
