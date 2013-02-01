package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.List;

import models.laboratory.experiment.description.QualityControlType;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class QualityControlTypeDAO extends AbstractExperimentDAO<QualityControlType>{


	public QualityControlTypeDAO() {
		super("quality_control_type");
	}

	public List<QualityControlType> findByExperimentType(long idExperimentType)
	{
		String sql = "SELECT qc.id, fk_common_info_type "+
				"FROM quality_control_type as qc JOIN experiment_quality_control as eqc ON eqc.fk_quality_control_type=qc.id "+
				"WHERE eqc.fk_experiment_type = ?";
		QualityControlTypeMappingQuery qualityControlTypeMappingQuery = new QualityControlTypeMappingQuery(dataSource, sql, new SqlParameter("eqc.fk_experiment_type", Type.LONG));
		return qualityControlTypeMappingQuery.execute(idExperimentType);
	}

	public QualityControlType findById(long id)
	{
		String sql = "SELECT id, fk_common_info_type "+
				"FROM quality_control_type "+
				"WHERE id = ?";
		QualityControlTypeMappingQuery qualityControlTypeMappingQuery = new QualityControlTypeMappingQuery(dataSource, sql, new SqlParameter("id", Type.LONG));
		return qualityControlTypeMappingQuery.findObject(id);
	}
	
	public QualityControlType findByCode(String code)
	{
		String sql = "SELECT qc.id, fk_common_info_type "+
				"FROM quality_control_type as qc JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code = ?";
		QualityControlTypeMappingQuery qualityControlTypeMappingQuery = new QualityControlTypeMappingQuery(dataSource, sql, new SqlParameter("code", Types.VARCHAR));
		return qualityControlTypeMappingQuery.findObject(code);
	}
}
