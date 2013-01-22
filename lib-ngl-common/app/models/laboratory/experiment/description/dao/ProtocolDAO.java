package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.ReagentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ProtocolDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private SimpleJdbcInsert jdbcInsert;
	private String sqlCommon = "SELECT id, name, file_path, version, fk_protocol_category "+
			"FROM protocol ";

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert=new SimpleJdbcInsert(dataSource).withTableName("protocol").usingGeneratedKeyColumns("id");
	}

	public List<Protocol> findByExperimentType(long idExperimentType)
	{
		String sql = sqlCommon+" WHERE fk_experiment_type = ? ";
		ProtocolMappingQuery protocolMappingQuery=new ProtocolMappingQuery(dataSource, sql);
		return protocolMappingQuery.execute(idExperimentType);
	}

	public Protocol findById(long id)
	{
		String sql = sqlCommon+" WHERE id=?";
		ProtocolMappingQuery protocolMappingQuery = new ProtocolMappingQuery(dataSource, sql);
		return protocolMappingQuery.findObject(id);
	}

	public Protocol add(Protocol protocol, long idExpType)
	{
		//Check if category exist
		if(protocol.getProtocolCategory()!=null && protocol.getProtocolCategory().getId()==null)
		{
			ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
			ProtocolCategory pc = protocolCategoryDAO.add(protocol.getProtocolCategory());
			protocol.setProtocolCategory(pc);
		}

		//Create new Protocol
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", protocol.getName());
		parameters.put("file_path", protocol.getFilePath());
		parameters.put("version", protocol.getVersion());
		parameters.put("fk_experiment_type", idExpType);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		protocol.setId(newId);

		//Add reagent type list
		List<ReagentType> reagentTypes = protocol.getReagentTypes();
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sql = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				if(reagentType.getId()==null)
					reagentType = reagentTypeDAO.add(reagentType);
				jdbcTemplate.update(sql, newId,reagentType.getId());
			}
		}
		return protocol;
	}

	public void update(Protocol protocol)
	{
		Protocol protoDB = findById(protocol.getId());

		String sql = "UPDATE protocol SET name=?, file_path=?, version=? WHERE id=?";
		jdbcTemplate.update(sql, protocol.getName(), protocol.getFilePath(), protocol.getVersion(), protocol.getId());

		//Update reagentTypes list
		List<ReagentType> reagentTypes = protocol.getReagentTypes();
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sqlReagent = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				if(protoDB.getReagentTypes()==null || (protoDB.getReagentTypes()!=null && !protoDB.getReagentTypes().contains(reagentType))){
					if(reagentType.getCommonInfoType()==null)
						reagentType = reagentTypeDAO.add(reagentType);
					jdbcTemplate.update(sqlReagent, protoDB.getId(), reagentType.getId());
				}
			}

		}
	}
}
