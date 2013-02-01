package models.laboratory.processus.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class ProcessTypeMappingQuery extends MappingSqlQuery<ProcessType>{

	public ProcessTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
		
	}
	
	@Override
	protected ProcessType mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProcessType processType = new ProcessType();
		processType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		long idProjectCategory = rs.getLong("fk_process_category");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		processType.setCommonInfoType(commonInfoType);
		//Get category
		ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
		ProcessCategory processCategory = (ProcessCategory) processCategoryDAO.findById(idProjectCategory);
		processType.processCategory = processCategory;
		//Get list experimentType
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> experimentTypes = expTypeDAO.findByProcessId(processType.id);
		processType.experimentTypes=experimentTypes;
		return processType;
	}

}
