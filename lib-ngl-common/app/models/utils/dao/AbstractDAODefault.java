package models.utils.dao;

import java.util.ArrayList;
import java.util.List;

import models.utils.ListObject;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import play.Logger;


/**
 * Generic operations for SimpleDAO
 * @author ejacoby
 *
 * @param <T>
 */
public abstract class AbstractDAODefault<T> extends AbstractDAO<T>{

	private String sqlCommon;
	private boolean usedInstitute = false;
	
	protected AbstractDAODefault(String tableName, Class<T> entityClass, boolean useGeneratedKey) {
		super(tableName, entityClass,useGeneratedKey);		
	}
	
	protected AbstractDAODefault(String tableName, Class<T> entityClass, boolean useGeneratedKey, boolean usedInstitute) {
		super(tableName, entityClass,useGeneratedKey);	
		this.usedInstitute = usedInstitute;
	}

	@SuppressWarnings("unchecked")
	private List<String> getColumns() throws MetaDataAccessException
	{
		return (List<String>)JdbcUtils.extractDatabaseMetaData(dataSource, new ColumnMetaDataCallback(tableName));
	}

	
	protected String getSqlCommon() throws DAOException{
		if(null == sqlCommon){
			sqlCommon = getSQLSelect();
			if(usedInstitute)sqlCommon += DAOHelpers.getSQLForInstitute(tableName, "t");
		}
		return sqlCommon;
	}
	
	private String getSQLSelect() throws DAOException  
	{
		try {
			String sql = "SELECT ";
			if(usedInstitute)sql+="distinct ";
			for(String column : getColumns()){
				sql+="t."+column+", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql+=" FROM "+tableName+" as t";
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
			String sql = getSqlCommon()+" ORDER by code";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.query(sql, mapper);
		} catch (DataAccessException e) {
			return new ArrayList<T>();
		}
	}

	public List<ListObject> findAllForList(){
		String sql = "SELECT code, name from "+tableName;
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	public T findById(Long id) throws DAOException
	{
		if(null == id){
			throw new DAOException("id is mandatory");
		}
		try {
			String sql = getSqlCommon()+" WHERE id=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.queryForObject(sql, mapper, id);
		} catch (DataAccessException e) {
			return null;
		}
	}

	public T findByCode(String code) throws DAOException
	{
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		try {
			String sql = getSqlCommon()+" WHERE code=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
			return this.jdbcTemplate.queryForObject(sql, mapper, code);
		} catch (DataAccessException e) {
			Logger.warn(e.getMessage());
			return null;
		}
	}


	public long save(T value) throws DAOException
	{
		if(null == value){
			throw new DAOException("value is mandatory");
		}
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		long id  = (Long) jdbcInsert.executeAndReturnKey(ps);
		return id;
	}


	public void update(T value) throws DAOException
	{
		if(null == value){
			throw new DAOException("value is mandatory");
		}
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(getSQLUpdate(), ps);
	}





}
