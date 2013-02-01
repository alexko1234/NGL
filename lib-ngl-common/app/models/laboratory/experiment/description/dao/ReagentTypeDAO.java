package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ReagentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

import play.modules.spring.Spring;

@Repository
public class ReagentTypeDAO {

	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("reagent_type");
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
	
	public ReagentType findByCode(String code)
	{
		String sql = "SELECT rt.id as id, fk_common_info_type "+
				"FROM reagent_type as rt "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code = ? ";
		ReagentTypeMappingQuery reagentTypeMappingQuery=new ReagentTypeMappingQuery(dataSource,sql,new SqlParameter("code", Types.VARCHAR));
		return reagentTypeMappingQuery.findObject(code);
	}
	
	public ReagentType add(ReagentType reagentType)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(reagentType);
		reagentType.setCommonInfoType(cit);
		//Create new reagentType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", cit.id);
		parameters.put("id", cit.id);
		jdbcInsert.execute(parameters);
		reagentType.id = cit.id;
		return reagentType;
	}
	
	public void update(ReagentType reagentType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(reagentType);
	}
}
