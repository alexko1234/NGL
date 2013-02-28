package models.laboratory.experiment.description.dao;

import java.util.List;

import models.laboratory.experiment.description.PurificationMethodType;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class PurificationMethodTypeDAO extends AbstractExperimentDAO<PurificationMethodType>{

	public PurificationMethodTypeDAO() {
		super("purification_method_type", PurificationMethodType.class,PurificationMethodTypeMappingQuery.class,
				"SELECT t.id, fk_common_info_type, code "+
						"FROM purification_method_type as t "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type ");

	}


	public List<PurificationMethodType> findByExperimentType(long idExperimentType)
	{
		String sql = "SELECT pm.id, fk_common_info_type "+
				"FROM purification_method_type as pm JOIN experiment_purification_method as epm ON epm.fk_purification_method_type=pm.id "+
				"WHERE epm.fk_experiment_type = ?";
		PurificationMethodTypeMappingQuery purificationMethodTypeMappingQuery = new PurificationMethodTypeMappingQuery(dataSource, sql, new SqlParameter("epm.fk_experiment_type", Type.LONG));
		return purificationMethodTypeMappingQuery.execute(idExperimentType);
	}

	@Override
	public void remove(PurificationMethodType purificationMethodType) throws DAOException{
		//Remove in list associated with experiment_type
		String sqlState = "DELETE FROM experiment_purification_method WHERE fk_purification_method_type=?";
		jdbcTemplate.update(sqlState, purificationMethodType.id);
		super.remove(purificationMethodType);
	}

}
