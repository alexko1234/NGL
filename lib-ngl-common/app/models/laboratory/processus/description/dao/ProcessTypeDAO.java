package models.laboratory.processus.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ProcessTypeDAO {

	private ProcessTypeMappingQuery processTypeMappingQuery;
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.processTypeMappingQuery=new ProcessTypeMappingQuery(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("process_type").usingGeneratedKeyColumns("id");
	}

	public ProcessType findById(long id)
	{
		return this.processTypeMappingQuery.findObject(id);
	}

	public ProcessType add(ProcessType processType)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(processType);
		processType.setCommonInfoType(cit);
		//Check if category exist
		if(processType.processCategory!=null && processType.processCategory.id==null)
		{
			ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
			ProcessCategory pc = processCategoryDAO.add(processType.processCategory);
			processType.processCategory = pc;
		}

		//Create new reagentType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", processType.getIdCommonInfoType());
		parameters.put("fk_process_category", processType.processCategory.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		processType.id = newId;
		return processType;
	}

	public void update(ProcessType processType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(processType);
	}
}
