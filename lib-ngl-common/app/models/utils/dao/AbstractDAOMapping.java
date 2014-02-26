package models.utils.dao;

import java.sql.Types;
import java.util.List;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.dao.InstrumentMappingQuery;

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
public abstract class AbstractDAOMapping<T> extends AbstractDAO<T> {

	//table of class <T> must be named as "t" and "code" field must be unique
	protected String sqlCommon;
	protected Class<? extends MappingSqlQuery<T>> classMapping;

	protected AbstractDAOMapping(String tableName, Class<T> entityClass, Class<? extends MappingSqlQuery<T>> classMapping, String sqlCommon, boolean useGeneratedKey) {
		super(tableName, entityClass, useGeneratedKey);
		this.classMapping=classMapping;
		this.sqlCommon=sqlCommon;
	}

	

	public T findById(Long id) throws DAOException {
		if(null == id){
			throw new DAOException("id is mandatory");
		}
		String sql = sqlCommon+" where t.id = ? ";
		return initializeMapping(sql, new SqlParameter("id", Type.LONG)).findObject(id);
	}

	
	public List<T> findAll() throws DAOException {
		return initializeMapping(sqlCommon).execute();
	}
	
	
	public T findByCode(String code) throws DAOException {
		
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		
		String sql= sqlCommon+" where t.code = ?";
		return initializeMapping(sql, new SqlParameter("code",Types.VARCHAR)).findObject(code);
	}


	protected MappingSqlQuery<T> initializeMapping(String sql, SqlParameter...sqlParams) throws DAOException
	{
		try {
			MappingSqlQuery<T> mapping = classMapping.newInstance();
			mapping.setDataSource(dataSource);
			mapping.setSql(sql);
			if(sqlParams!=null && sqlParams.length > 0){
				for(SqlParameter sqlParam: sqlParams){
					mapping.declareParameter(sqlParam);
				}
			}
			mapping.compile();
			return mapping;
		} catch (InvalidDataAccessApiUsageException e) {
			throw new DAOException(e);
		} catch (InstantiationException e) {
			throw new DAOException(e);
		} catch (IllegalAccessException e) {
			throw new DAOException(e);
		}
	}
}
