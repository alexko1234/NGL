package models.laboratory.run.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class RunTypeMappingQuery extends MappingSqlQuery<RunType>{

	public RunTypeMappingQuery() {
		super();
	}
	public RunTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected RunType mapRow(ResultSet rs, int rowNum) throws SQLException {
			RunType runType = new RunType();
			
			runType.id = rs.getLong("id");
			runType.nbLanes = rs.getInt("nb_lanes");
			long idCommonInfoType = rs.getLong("fk_common_info_type");
			long idRunCategory = rs.getLong("fk_run_category");
			
			//Get commonInfoType
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType commonInfoType = null;
			try {
				 commonInfoType = (CommonInfoType) commonInfoTypeDAO.findById(idCommonInfoType);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			//Set commonInfoType
			runType.setCommonInfoType(commonInfoType);
			//Get category
			RunCategoryDAO runCategoryDAO = Spring.getBeanOfType(RunCategoryDAO.class);
			RunCategory runCategory=null;
			try {
				runCategory = (RunCategory) runCategoryDAO.findById(idRunCategory);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			//Set category
			runType.category = runCategory;
			return runType;
	}

}
