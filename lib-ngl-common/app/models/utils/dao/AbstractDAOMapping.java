package models.utils.dao;

import java.sql.Types;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.utils.DescriptionHelper;

import org.springframework.asm.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.Logger;

/**
 *Generic operation DAO with MappingSQL object 
 * @author ejacoby
 *
 * @param <T>
 */
public abstract class AbstractDAOMapping<T> extends AbstractCommonDAO<T> {

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
		String sql = sqlCommon+" WHERE t.id = ? ";
		return initializeMapping(sql, new SqlParameter("id", Type.LONG)).findObject(id);
	}

	
	public List<T> findAll() throws DAOException {
		return initializeMapping(sqlCommon).execute();
	}

	 
	public T findByCode(String code) throws DAOException {
		return findByCode(code, true); 
	}
	
	public T findByCode(String code, Boolean forCurrentInstitute) throws DAOException {
		
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		
		String sql= sqlCommon;
		
		if (forCurrentInstitute && entityClass.getSuperclass().equals(CommonInfoType.class) ) {
			sql += " and  c.code = ?  " + 
					"join common_info_type_institute ci on c.id =ci.fk_common_info_type "+
					"join institute i on i.id = ci.fk_institute and i.code=" + DescriptionHelper.getInstitute();
		}
		else {
			if (entityClass.getSuperclass().equals(CommonInfoType.class)) {
				sql += " WHERE c.code = ?";
			}
			else {
				sql += " WHERE t.code = ?";
			}
		}
		
		/*
		if (!forCurrentInstitute) {
			sql = sqlCommon ;
			if (entityClass.getSuperclass().equals(CommonInfoType.class)) {
				sql += " WHERE c.code = ?";
			}
			else {
				sql += " WHERE t.code = ?";
			}
		}
		else {
			if (entityClass.getSuperclass().equals(CommonInfoType.class)) {
				sql += " and  c.code = ?  " + 
						"join common_info_type_institute ci on c.id =ci.fk_common_info_type "+
						"join institute i on i.id = ci.fk_institute and i.code=" + DescriptionHelper.getInstitute();
			}
			else {
				Logger.warn("Can't find institute for this object !, no dependency with CommonInfoType !");
				sql += " WHERE t.code = ?";
				Logger.debug("SQL =" + sql);
			}
		}
		*/ 
		
		return initializeMapping(sql, new SqlParameter("code",Types.VARCHAR)).findObject(code);
	}


	public Boolean isCodeExist(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		try {
			String sql= null;
			
			if (entityClass.getSuperclass().equals(CommonInfoType.class)) {
				sql = "SELECT c.id FROM common_info_type c join common_info_type_institute ci on c.id =ci.fk_common_info_type "+
				"join institute i on i.id = ci.fk_institute and i.code=" + DescriptionHelper.getInstitute() + " and c.code=?";
			}
			else {
				sql = "SELECT id FROM "+tableName+" WHERE code=?";
			}
			try {
				long id =  this.jdbcTemplate.queryForLong(sql, code);
				if(id > 0) {
					return Boolean.TRUE;
				}else{
					return Boolean.FALSE;
				}
			} catch (EmptyResultDataAccessException e) {
				return Boolean.FALSE;
			}
		} catch (DataAccessException e) {
			Logger.warn(e.getMessage());
			return null;
		}
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
