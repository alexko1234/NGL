package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ReagentType;

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
		reagentType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		reagentType.commonInfoType = commonInfoType;
		return reagentType;
	}

}
