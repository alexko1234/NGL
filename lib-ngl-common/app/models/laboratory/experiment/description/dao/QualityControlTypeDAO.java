package models.laboratory.experiment.description.dao;

import java.util.List;

import models.laboratory.experiment.description.QualityControlType;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class QualityControlTypeDAO extends AbstractExperimentDAO<QualityControlType>{


	public QualityControlTypeDAO() {
		super("quality_control_type", QualityControlType.class,QualityControlTypeMappingQuery.class,
				"SELECT t.id, fk_common_info_type "+
				"FROM quality_control_type as t JOIN common_info_type as c ON c.id=fk_common_info_type ");
	}

	public List<QualityControlType> findByExperimentType(long idExperimentType)
	{
		String sql = "SELECT qc.id, fk_common_info_type "+
				"FROM quality_control_type as qc JOIN experiment_quality_control as eqc ON eqc.fk_quality_control_type=qc.id "+
				"WHERE eqc.fk_experiment_type = ?";
		QualityControlTypeMappingQuery qualityControlTypeMappingQuery = new QualityControlTypeMappingQuery(dataSource, sql, new SqlParameter("eqc.fk_experiment_type", Type.LONG));
		return qualityControlTypeMappingQuery.execute(idExperimentType);
	}
	
	@Override
	public void remove(QualityControlType qualityControlType) throws DAOException{
		//Remove in list associated with experiment_type
		String sqlState = "DELETE FROM experiment_quality_control WHERE fk_quality_control_type=?";
		jdbcTemplate.update(sqlState, qualityControlType.id);
		super.remove(qualityControlType);
	}

}
