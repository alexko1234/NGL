package models.laboratory.reagent.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.reagent.description.ReagentType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class ReagentTypeMappingQuery extends MappingSqlQuery<ReagentType>{

	public ReagentTypeMappingQuery()
	{
		super();
	}
	public ReagentTypeMappingQuery(DataSource ds,String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
		
	}
	@Override
	protected ReagentType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		ReagentType reagentType = new ReagentType();
		reagentType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType=null;
		try {
			commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		if (commonInfoType != null) {
			reagentType.setCommonInfoType(commonInfoType);
		}
		return reagentType;
	}

}
