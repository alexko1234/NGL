package models.utils.dao;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;

/**
 * Common operations between Simple DAO et DAO Using mappingQuery
 * Must not implement an interface for transactional context because
 * If implements interface Spring creates an instance for the application and get not that class but a Java Dynamic Proxy that implements that classes interface
 * Because of this, you cannot cast that object to the original type.
 * If there aren't interfaces to implement, it will give a CGLib proxy of the class, which is basically just a runtime modified version of the class and so is assignable to the class itself
 * @author ejacoby
 *
 * @param <T>
 */
@Transactional(readOnly=false, rollbackFor=DAOException.class)
public abstract class AbstractCommonDAO<T> {


	protected String tableName;
	protected DataSource dataSource;
	protected SimpleJdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	protected Class<T> entityClass;
	//Use automatic key id generation 
	//False for type because id provided by commonInfoType
	protected boolean useGeneratedKey;

	protected AbstractCommonDAO(String tableName, Class<T> entityClass, boolean useGeneratedKey) {
		this.tableName = tableName;
		this.entityClass = entityClass;
		this.useGeneratedKey=useGeneratedKey;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);   
		if(useGeneratedKey)
			jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName).usingGeneratedKeyColumns("id");
		else
			jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);
	}

	public void remove(T value) throws DAOException
	{
		String sql = "DELETE FROM "+tableName+" WHERE id=:id";
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(sql, ps);
	}

	public abstract List<T> findAll() throws DAOException;

	public abstract T findById(Long id) throws DAOException;;

	public abstract T findByCode(String code) throws DAOException;


	public abstract long save(T value) throws DAOException;

	public abstract void update(T value) throws DAOException;

	public Boolean isCodeExist(String code) throws DAOException
	{
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		try {
			try{
				String sql = "select id from "+tableName+" WHERE code=?";
				long id =  this.jdbcTemplate.queryForLong(sql, code);
				if(id > 0){
					return Boolean.TRUE;
				}else{
					return Boolean.FALSE;
				}
			}catch (EmptyResultDataAccessException e ) {
				return Boolean.FALSE;
			}
		} catch (DataAccessException e) {
			Logger.warn(e.getMessage());
			return null;
		}
	}



}
