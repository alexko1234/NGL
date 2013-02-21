package models.utils.dao;

import java.util.ArrayList;
import java.util.List;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic operations for SimpleDAO
 * @author ejacoby
 *
 * @param <T>
 */
public abstract class AbstractDAO<T> extends AbstractCommonDAO<T>{

	protected AbstractDAO(String tableName, Class<T> entityClass,boolean useGeneratedKey) {
		super(tableName, entityClass,useGeneratedKey);
	}

	@SuppressWarnings("unchecked")
	private List<String> getColumns() throws MetaDataAccessException
	{
		return (List<String>)JdbcUtils.extractDatabaseMetaData(dataSource, new ColumnMetaDataCallback(tableName));
	}

	private String getSQLSelect() throws DAOException 
	{
		try {
			String sql = "SELECT ";
			for(String column : getColumns()){
				sql+=column+", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql+=" FROM "+tableName;
			return sql;
		} catch (MetaDataAccessException e) {
			throw new DAOException(e);
		}
	}
	
	private String getSQLUpdate() throws DAOException
	{
		try {
			String sql = "UPDATE "+tableName+" SET ";
			for(String column : getColumns()){
				if(!column.equals("id"))
					sql+=column+"=:"+column+", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += " WHERE id=:id";
			return sql;
		} catch (MetaDataAccessException e) {
			throw new DAOException(e);
		}
	}
	
	public List<T> findAll() throws DAOException
	{
		try {
			String sql = getSQLSelect()+" ORDER by code";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.query(sql, mapper);
		} catch (DataAccessException e) {
			return new ArrayList<T>();
		}
	}

	public T findById(long id) throws DAOException
	{
		try {
			String sql = getSQLSelect()+" WHERE id=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.queryForObject(sql, mapper, id);
		} catch (DataAccessException e) {
			return null;
		}
	}

	public T findByCode(String code) throws DAOException
	{
		try {
			String sql = getSQLSelect()+" WHERE code=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.queryForObject(sql, mapper, code);
		} catch (DataAccessException e) {
			return null;
		}
	}

	@Transactional	
	public long save(T value) throws DAOException
	{
        SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
        
        long id  = (Long) jdbcInsert.executeAndReturnKey(ps);
        //TODO set id to generic model
        //value.setId(id);
        return id;
	}

	
	@Transactional	
	public void update(T value) throws DAOException
	{
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(getSQLUpdate(), ps);
	}
	
	



}
