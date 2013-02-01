package models.laboratory.processus.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class ProcessTypeDAO {

	private SimpleJdbcInsert jdbcInsert;
	private SimpleJdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("process_type");
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);  
	}

	public ProcessType findById(long id)
	{
		String sql = "SELECT id, fk_common_info_type, fk_process_category "+
				"FROM process_type "+
				"WHERE id = ? ";
		ProcessTypeMappingQuery processTypeMappingQuery=new ProcessTypeMappingQuery(dataSource,sql, new SqlParameter("id", Type.LONG));
		return processTypeMappingQuery.findObject(id);
	}
	
	public ProcessType findByCode(String code)
	{
		String sql = "SELECT pt.id, fk_common_info_type, fk_process_category "+
				"FROM process_type as pt  "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code = ? ";
		ProcessTypeMappingQuery processTypeMappingQuery=new ProcessTypeMappingQuery(dataSource,sql, new SqlParameter("code", Types.VARCHAR));
		return processTypeMappingQuery.findObject(code);
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
			ProcessCategory pc = (ProcessCategory) processCategoryDAO.add(processType.processCategory);
			processType.processCategory = pc;
		}

		//Create new processType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", processType.id);
		parameters.put("fk_common_info_type", processType.id);
		parameters.put("fk_process_category", processType.processCategory.id);
		jdbcInsert.execute(parameters);

		//Add list experimentType
		List<ExperimentType> experimentTypes = processType.experimentTypes;
		if(experimentTypes!=null && experimentTypes.size()>0){
			ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType : experimentTypes){
				if(experimentType.id==null)
					experimentType = experimentTypeDAO.add(experimentType);
				jdbcTemplate.update(sql, processType.id, experimentType.id);
			}
		}
		return processType;
	}

	public void update(ProcessType processType)
	{
		ProcessType processTypeDB = findById(processType.id);
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(processType);

		//Update InstrumentUsedTypes list
		List<ExperimentType> experimentTypes = processType.experimentTypes;
		if(experimentTypes!=null && experimentTypes.size()>0){
			ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType : experimentTypes){
				if(processTypeDB.experimentTypes==null || (processTypeDB.experimentTypes!=null && !processTypeDB.experimentTypes.contains(experimentType))){
					if(experimentType.id==null)
						experimentType = experimentTypeDAO.add(experimentType);
					jdbcTemplate.update(sql, processType.id, experimentType.id);
				}
			}
		}
	}
}
