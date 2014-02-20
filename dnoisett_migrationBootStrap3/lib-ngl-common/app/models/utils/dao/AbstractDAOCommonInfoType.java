package models.utils.dao;

import java.sql.Types;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.State;
import models.laboratory.common.description.dao.StateDAO;
import models.utils.ListObject;

import org.springframework.asm.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.Logger;

public abstract class AbstractDAOCommonInfoType<T extends CommonInfoType> extends AbstractDAOMapping<T> {

	protected final static String sqlCommonInfoType="JOIN common_info_type as t ON t.id=c.fk_common_info_type ";
	protected String sqlCommonFrom;
	protected String sqlCommonSelect;

	protected AbstractDAOCommonInfoType(String tableName, Class<T> entityClass,
			Class<? extends MappingSqlQuery<T>> classMapping, String sqlCommonSelect, String sqlCommonFrom,
					boolean useGeneratedKey) {
		super(tableName, entityClass, classMapping, sqlCommonSelect+sqlCommonFrom+DAOHelpers.getCommonInfoTypeDefaultSQLForInstitute(), useGeneratedKey);
		this.sqlCommonFrom=sqlCommonFrom+DAOHelpers.getCommonInfoTypeDefaultSQLForInstitute();
		this.sqlCommonSelect=sqlCommonSelect;		
	}
	
	public Boolean isCodeExist(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		try {
			String	sql = "SELECT t.id FROM common_info_type t "+DAOHelpers.getCommonInfoTypeDefaultSQLForInstitute()+" where t.code=?";
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

	public T findById(Long id) throws DAOException {
		if(null == id){
			throw new DAOException("id is mandatory");
		}
		String sql = sqlCommon+" where t.id = ? ";
		return initializeMapping(sql, new SqlParameter("id", Type.LONG)).findObject(id);
	}


	public T findByCode(String code) throws DAOException {

		if(null == code){
			throw new DAOException("code is mandatory");
		}

		String sql= sqlCommon+" where t.code = ?";
		return initializeMapping(sql, new SqlParameter("code",Types.VARCHAR)).findObject(code);
	}


	public List<ListObject> findAllForList(){
		String sql = "SELECT t.code, t.name "+sqlCommonFrom;
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}

}
