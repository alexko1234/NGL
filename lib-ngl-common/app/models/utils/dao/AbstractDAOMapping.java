package models.utils.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.asm.Type;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 *Generic operation DAO with MappingSQL object 
 * @author ejacoby
 *
 * @param <T>
 */
public abstract class AbstractDAOMapping<T> extends AbstractCommonDAO<T> {

	protected MappingSqlQuery<T> mapping;
	//table of class <T> must be named as "t" and "code" field must be unique
	protected String sqlCommon;
	protected Class<? extends MappingSqlQuery<T>> classMapping;

	protected AbstractDAOMapping(String tableName, Class<T> entityClass, Class<? extends MappingSqlQuery<T>> classMapping, String sqlCommon, boolean useGeneratedKey) {
		super(tableName, entityClass,useGeneratedKey);
		this.classMapping=classMapping;
		this.sqlCommon=sqlCommon;
	}

	public T findById(long id) throws DAOException
	{
		String sql = sqlCommon+
				"WHERE t.id = ? ";
		initializeMapping(sql, new SqlParameter("id", Type.LONG));
		return mapping.findObject(id);
	}

	public List<T> findAll() throws DAOException
	{
		initializeMapping(sqlCommon, null);
		return mapping.execute();
	}

	public T findByCode(String code) throws DAOException
	{
		String sql = sqlCommon+
				"WHERE code = ? ";
		initializeMapping(sql, new SqlParameter("code",Types.VARCHAR));
		return mapping.findObject(code);
	}
	
	private void initializeMapping(String sql, SqlParameter sqlParam) throws DAOException
	{
		try {
			mapping = classMapping.newInstance();
			mapping.setDataSource(dataSource);
			mapping.setSql(sql);
			if(sqlParam!=null)
				mapping.declareParameter(sqlParam);
			mapping.compile();
		} catch (InvalidDataAccessApiUsageException e) {
			throw new DAOException(e);
		} catch (InstantiationException e) {
			throw new DAOException(e);
		} catch (IllegalAccessException e) {
			throw new DAOException(e);
		}
	}
}
