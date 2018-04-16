package models.utils.dao;

import static models.utils.dao.DAOException.daoAssertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import models.utils.ListObject;

// import fr.cea.ig.play.NGLContext;
//import play.Logger;


/**
 * Generic operations for SimpleDAO
 * 
 * @author ejacoby
 *
 * @param <T> DAO
 */
public abstract class AbstractDAODefault<T> extends AbstractDAO<T> {

	private static final play.Logger.ALogger logger = play.Logger.of(AbstractDAODefault.class);
	
	protected String  sqlCommon;
	protected boolean usedInstitute = false;
	
	@Inject
	protected AbstractDAODefault(String tableName, Class<T> entityClass, boolean useGeneratedKey) {
		super(tableName, entityClass, useGeneratedKey);	
		// this(tableName, entityClass, useGeneratedKey, false);
	}
	
	protected AbstractDAODefault(String tableName, Class<T> entityClass, boolean useGeneratedKey, boolean usedInstitute) {
		super(tableName, entityClass,useGeneratedKey);	
		this.usedInstitute = usedInstitute;
	}

	@SuppressWarnings("unchecked") // untypeable DB metadata access 
	private List<String> getColumns() throws MetaDataAccessException {
		return (List<String>)JdbcUtils.extractDatabaseMetaData(dataSource, new ColumnMetaDataCallback(tableName));
	}

	protected String getSqlCommon() throws DAOException {
		if (sqlCommon == null) {
			sqlCommon = getSQLSelect();
			if (usedInstitute) 
				sqlCommon += DAOHelpers.getSQLForInstitute(tableName, "t");
		}
		return sqlCommon;
	}
	
	// TODO: use String.join(",",getColumns())
	private String getSQLSelect() throws DAOException {
		try {
			String sql = "SELECT ";
			if (usedInstitute) sql += "distinct ";
			// sql += String.join(map(getColumns(),x -> "t." + x).intercalate(","));
			for (String column : getColumns()) {
				sql += "t." + column + ", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += " FROM " + tableName + " as t";
			return sql;
		} catch (MetaDataAccessException e) {
			throw new DAOException(e);
		}
	}
	
	private String getSQLUpdate() throws DAOException {
		try {
			String sql = "UPDATE " + tableName + " SET ";
			for (String column : getColumns()) {
				if (!column.equals("id"))
					sql += column + "=:" + column + ", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += " WHERE id=:id";
			return sql;
//			return Iterables.concat(
//					Iterables.filter(getColumns(), c -> !c.equals("id"))
//					        .map(c -> c + "=:" + c)
//					        .surround("UPDATE " + tableName + " SET ",
//					        		  ",",
//					        		  " WHERE id=:id"));
		} catch (MetaDataAccessException e) {
			throw new DAOException(e);
		}
	}

	// TODO: fix silent error handling
	@SuppressWarnings("deprecation")
	@Override
	public List<T> findAll() throws DAOException {
		try {
			String sql = getSqlCommon() + " ORDER by t.code";
			logger.debug(sql);
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<>(entityClass);
			return this.jdbcTemplate.query(sql, mapper);
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("deprecation")
	public List<ListObject> findAllForList() {
//		String sql = "SELECT code, name from " + tableName + " ORDER by t.code";
		String sql = "SELECT code, name from " + tableName + " ORDER by code";
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	// TODO: fix silent error handling	
	@SuppressWarnings("deprecation")
	@Override
	public T findById(Long id) throws DAOException {
		// if (id == null) throw new DAOIllegalArgumentException("id",id); //("id is mandatory");
		daoAssertNotNull("id",id);
		try {
			String sql = getSqlCommon() + " WHERE t.id=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<>(entityClass);
			return this.jdbcTemplate.queryForObject(sql, mapper, id);
		} catch (DataAccessException e) {
			return null;
		}
	}

	// TODO: fix silent error handling		
	@SuppressWarnings("deprecation")
	@Override
	public T findByCode(String code) throws DAOException {
		// if (code == null) throw new DAOIllegalArgumentException("code",code); // ("code is mandatory");
		daoAssertNotNull("code",code);
		T o = getObjectInCache(code);
		if (o != null) {
			//Logger.debug("find in cache "+entityClass.getCanonicalName() + " : "+code);
			return o;
		}
		try {
			String sql = getSqlCommon() + " WHERE t.code=?";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<>(entityClass);
			o = this.jdbcTemplate.queryForObject(sql, mapper, code);
			setObjectInCache(o, code);
			return o;
		} catch (IncorrectResultSizeDataAccessException e) {
			//Logger.warn(e.getMessage());
			return null;
		}
	}
	
	// ajout FDS 28/03/2018 NGL-1969: pour trouver un sampleType en se basant sur son code OU SUR SON NOM
	// sert dans quel cas ?????????????? la methode reellement utilisee est celle de AbstractDAOMapping....
	// TODO: fix silent error handling		
	public T findByCodeOrName(String code) throws DAOException {
		if (null == code) {
			throw new DAOException("code is mandatory");
		}
		T o = getObjectInCache(code);
		if (null != o) {
			//Logger.debug("find in cache "+entityClass.getCanonicalName() + " : "+code);
			return o;
		} else {
			try {
				String sql = getSqlCommon() + " WHERE t.code=? or t.name=?";
				BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(entityClass);
				o = this.jdbcTemplate.queryForObject(sql, mapper, code, code); /// ajout 2eme parametre
				setObjectInCache(o, code);
				return o;
			} catch (IncorrectResultSizeDataAccessException e) {
				//Logger.warn(e.getMessage());
				return null;
			}
		}
	}
	
	/// corriger le warning !!!!
	@SuppressWarnings("deprecation")
	@Override
	public List<T> findByCodes(List<String> codes) throws DAOException {
		// if (codes == null) throw new DAOIllegalArgumentException("codes",codes); // "codes is mandatory");
		daoAssertNotNull("codes",codes);
		try {
			String sql = getSqlCommon() + " WHERE t.code in (" + listToParameters(codes) + ")";
			BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<>(entityClass);
//			return this.jdbcTemplate.query(sql, mapper, listToSqlParameters(codes ,"t.code", Types.VARCHAR));
//			return jdbcTemplate.query(sql, mapper, listToSqlParameters(codes ,"t.code", Types.VARCHAR));
			return jdbcTemplate.query(sql, mapper, codes.toArray(new Object[codes.size()]));
		} catch (DataAccessException e) {
			logger.warn(e.getMessage());
			return null;
		}
	}

	@Override
	public long save(T value) throws DAOException {
		// if (value == null) throw new DAOIllegalArgumentException("value",value); //"value is mandatory");
		daoAssertNotNull("value",value);
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		long id  = (Long) jdbcInsert.executeAndReturnKey(ps);
		return id;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(T value) throws DAOException	{
		// if (null == value) {	throw new DAOException("value is mandatory"); }
		daoAssertNotNull("value",value);
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(getSQLUpdate(), ps);
	}

}
