package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ReagentType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class ReagentTypeDAO extends AbstractDAOMapping<ReagentType>{

	protected ReagentTypeDAO() {
		super("reagent_type", ReagentType.class, ReagentTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type "+
						"FROM reagent_type as t "+
						"JOIN common_info_type as c ON c.id=fk_common_info_type ",false);
	}

	public List<ReagentType> findByProtocol(long idProtocol)
	{
		String sql = "SELECT id, fk_common_info_type "+
				"FROM reagent_type "+
				"JOIN protocol_reagent_type ON fk_reagent_type=id "+
				"WHERE fk_protocol = ? ";
		ReagentTypeMappingQuery reagentTypeMappingQuery=new ReagentTypeMappingQuery(dataSource,sql,new SqlParameter("id", Type.LONG));
		return reagentTypeMappingQuery.execute(idProtocol);
	}

	@Override
	public long save(ReagentType reagentType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		reagentType.id = commonInfoTypeDAO.save(reagentType);
		//reagentType.setCommonInfoType(reagentType);
		//Create new reagentType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", reagentType.id);
		parameters.put("id", reagentType.id);
		jdbcInsert.execute(parameters);
		return reagentType.id;
	}

	@Override
	public void update(ReagentType reagentType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(reagentType);
	}

	@Override
	public void remove(ReagentType reagentType) throws DAOException {
		//Remove reagent type protocol
		String sqlProtocol = "DELETE FROM protocol_reagent_type WHERE fk_reagent_type=?";
		jdbcTemplate.update(sqlProtocol, reagentType.id);
		//remove reagentType
		super.remove(reagentType);
		//remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(reagentType);
	}
}
