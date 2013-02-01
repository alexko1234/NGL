package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.List;

import models.laboratory.experiment.description.PurificationMethodType;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class PurificationMethodTypeDAO extends AbstractExperimentDAO<PurificationMethodType>{

	public PurificationMethodTypeDAO() {
		super("purification_method_type");
	}
	

	public List<PurificationMethodType> findByExperimentType(long idExperimentType)
	{
		String sql = "SELECT pm.id, fk_common_info_type "+
				"FROM purification_method_type as pm JOIN experiment_purification_method as epm ON epm.fk_purification_method_type=pm.id "+
				"WHERE epm.fk_experiment_type = ?";
		PurificationMethodTypeMappingQuery purificationMethodTypeMappingQuery = new PurificationMethodTypeMappingQuery(dataSource, sql, new SqlParameter("epm.fk_experiment_type", Type.LONG));
		return purificationMethodTypeMappingQuery.execute(idExperimentType);
	}

	public PurificationMethodType findByCode(String code)
	{
		String sql = "SELECT pm.id, fk_common_info_type "+
				"FROM purification_method_type as pm JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code = ?";
		PurificationMethodTypeMappingQuery purificationMethodTypeMappingQuery = new PurificationMethodTypeMappingQuery(dataSource, sql, new SqlParameter("code", Types.VARCHAR));
		return purificationMethodTypeMappingQuery.findObject(code);
	}
	
	public PurificationMethodType findById(long id)
	{
		String sql = "SELECT id, fk_common_info_type "+
				"FROM purification_method_type "+
				"WHERE id = ?";
		PurificationMethodTypeMappingQuery purificationMethodTypeMappingQuery = new PurificationMethodTypeMappingQuery(dataSource, sql, new SqlParameter("id", Type.LONG));
		return purificationMethodTypeMappingQuery.findObject(id);
	}
	
}
