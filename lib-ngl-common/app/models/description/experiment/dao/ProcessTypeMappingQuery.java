package models.description.experiment.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.ProcessCategory;
import models.description.experiment.ProcessType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class ProcessTypeMappingQuery extends MappingSqlQuery<ProcessType>{

	public ProcessTypeMappingQuery(DataSource ds)
	{
		super(ds,"SELECT id, fk_common_info_type, fk_process_category "+
				"FROM process_type "+
				"WHERE id = ? ");
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	
	@Override
	protected ProcessType mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessType processType = new ProcessType();
		processType.setId(rs.getLong("id"));
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		long idProjectCategory = rs.getLong("fk_process_category");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		processType.setCommonInfoType(commonInfoType);
		//Get category
		ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
		ProcessCategory processCategory = processCategoryDAO.findById(idProjectCategory);
		processType.setProcessCategory(processCategory);
		return processType;
	}

}
