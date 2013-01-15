package models.description.experiment.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.ReagentType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class ReagentTypeMappingQuery extends MappingSqlQuery<ReagentType>{

	public ReagentTypeMappingQuery(DataSource ds)
	{
		super(ds,"SELECT id, fk_common_info_type "+
				"FROM reagent_type "+
				"JOIN protocol_reagent_type ON fk_reagent_type=id "+
				"WHERE fk_protocol = ? ");
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	@Override
	protected ReagentType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		ReagentType reagentType = new ReagentType();
		reagentType.setId(rs.getLong("id"));
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		reagentType.setCommonInfoType(commonInfoType);
		return reagentType;
	}

}
