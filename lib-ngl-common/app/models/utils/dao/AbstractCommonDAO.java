package models.utils.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Common opérations between Simple DAO et DAO Using mappingQuery
 * @author ejacoby
 *
 * @param <T>
 */
public abstract class AbstractCommonDAO<T> implements IDAO<T>{

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

	@Transactional
	public void remove(T value)
	{
		String sql = "DELETE FROM "+tableName+" WHERE id=:id";
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(sql, ps);
	}
}
