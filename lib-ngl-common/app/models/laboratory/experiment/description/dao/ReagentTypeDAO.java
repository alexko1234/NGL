package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ReagentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ReagentTypeDAO {

	private ReagentTypeMappingQuery reagentTypeMappingQuery;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.reagentTypeMappingQuery=new ReagentTypeMappingQuery(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("reagent_type").usingGeneratedKeyColumns("id");
	}
	
	public List<ReagentType> findByProtocol(long idProtocol)
	{
		return this.reagentTypeMappingQuery.execute(idProtocol);
	}
	
	public ReagentType add(ReagentType reagentType)
	{
		//Check if commonInfoType exist
		if(reagentType.commonInfoType!=null && reagentType.commonInfoType.id==null)
		{
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType cit = commonInfoTypeDAO.add(reagentType.commonInfoType);
			reagentType.commonInfoType = cit;
		}
		//Create new reagentType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", reagentType.commonInfoType.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		reagentType.id = newId;
		return reagentType;
	}
	
	public void update(ReagentType reagentType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(reagentType.commonInfoType);
	}
}
