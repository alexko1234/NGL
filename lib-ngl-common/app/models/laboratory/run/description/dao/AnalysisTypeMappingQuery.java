package models.laboratory.run.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.AnalysisType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class AnalysisTypeMappingQuery extends MappingSqlQuery<AnalysisType>{

	public AnalysisTypeMappingQuery() {
		super();
	}
	public AnalysisTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected AnalysisType mapRow(ResultSet rs, int rowNum) throws SQLException {
		AnalysisType analysisType = new AnalysisType();
			
		analysisType.id = rs.getLong("id");
			
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//long idReadSetCategory = rs.getLong("fk_readset_category");
		
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = null;
		try {
			 commonInfoType = (CommonInfoType) commonInfoTypeDAO.findById(idCommonInfoType);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		//Set commonInfoType
		if (commonInfoType != null) {
			analysisType.setCommonInfoType(commonInfoType);
		}
		//Get category
		
		return analysisType;
	}

}
