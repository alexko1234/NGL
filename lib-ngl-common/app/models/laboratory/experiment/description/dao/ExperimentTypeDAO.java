package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;


@Repository
public class ExperimentTypeDAO extends AbstractExperimentDAO<ExperimentType>{

	private NextExperimentTypeMappingQuery nextExperimentTypeMappingQuery;
	private String sqlCommon = "SELECT id, doPurification, mandatoryPurification, doQualityControl, mandatoryQualityControl, fk_experiment_category, fk_common_info_type ";
	
	public ExperimentTypeDAO() {
		super("experiment_type");
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.nextExperimentTypeMappingQuery=new NextExperimentTypeMappingQuery(dataSource);
	}

	public List<ExperimentType> findNextExperiments(long idExperimentType)
	{
		return this.nextExperimentTypeMappingQuery.execute(idExperimentType);
	}

	public ExperimentType findById(long id){
		String sql = sqlCommon+
				"FROM experiment_type "+
				"WHERE id=?";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return experimentTypeMappingQuery.findObject(id);
	}

	public List<ExperimentType> findAll()
	{
		String sql = sqlCommon+
				"FROM experiment_type";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql, null);
		return experimentTypeMappingQuery.execute();
	}

	public ExperimentType findByCode(String code)
	{
		String sql = "SELECT et.id, doPurification, mandatoryPurification, doQualityControl, mandatoryQualityControl,fk_experiment_category, fk_common_info_type "+
				"FROM experiment_type as et "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code=?";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return experimentTypeMappingQuery.findObject(code);
	}

	public List<ExperimentType> findByProcessId(long id)
	{
		String sql = "SELECT et.id, doPurification, mandatoryPurification, doQualityControl, mandatoryQualityControl,fk_common_info_type, fk_experiment_category, fk_common_info_type "+
				"FROM experiment_type as et JOIN process_experiment_type as p ON p.fk_experiment_type=et.id "+
				"WHERE p.fk_process_type = ?";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql, new SqlParameter("p.fk_process_type", Type.LONG));
		return experimentTypeMappingQuery.execute(id);
	}
	public ExperimentType add(ExperimentType experimentType)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(experimentType);
		experimentType.setCommonInfoType(cit);

		//Check if category exist
		if(experimentType.experimentCategory!=null && experimentType.experimentCategory.id==null)
		{
			ExperimentCategoryDAO experimentCategoryDAO = Spring.getBeanOfType(ExperimentCategoryDAO.class);
			ExperimentCategory ec = (ExperimentCategory) experimentCategoryDAO.add(experimentType.experimentCategory);
			experimentType.experimentCategory = ec;
		}
		//Create experimentType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", experimentType.id);
		parameters.put("fk_common_info_type", experimentType.id);
		parameters.put("fk_experiment_category", experimentType.experimentCategory.id);
		parameters.put("doPurification", experimentType.doPurification);
		parameters.put("mandatoryPurification", experimentType.mandatoryPurification);
		parameters.put("doQualityControl", experimentType.doQualityControl);
		parameters.put("mandatoryQualityControl", experimentType.mandatoryQualityControl);
		jdbcInsert.execute(parameters);

		//Add list protocols
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocolDAO.add(experimentType.protocols, experimentType.id);
		//Add list instruments
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypeDAO.add(experimentType.instrumentUsedTypes, experimentType.id);
		//Add nextExperimentTypes list
		List<ExperimentType> nextExpTypes = experimentType.previousExperimentTypes;
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				jdbcTemplate.update(sql, experimentType.id, expType.id);
			}
		}

		//Add QualityControl
		List<QualityControlType> qualityControlTypes = experimentType.possibleQualityControlTypes;
		if(qualityControlTypes!=null && qualityControlTypes.size()>0){
			QualityControlTypeDAO qualityControlTypeDAO = Spring.getBeanOfType(QualityControlTypeDAO.class);
			String sql = "INSERT INTO experiment_quality_control(fk_quality_control_type,fk_experiment_type) VALUES(?,?)";
			for(QualityControlType qualityControlType : qualityControlTypes){
				if(qualityControlType.id==null)
					qualityControlType = qualityControlTypeDAO.add(qualityControlType);
				jdbcTemplate.update(sql, qualityControlType.id, experimentType.id);
			}
		}
		//Add Purification method
		List<PurificationMethodType> purificationMethodTypes = experimentType.possiblePurificationMethodTypes;
		if(purificationMethodTypes!=null && purificationMethodTypes.size()>0){
			PurificationMethodTypeDAO purificationMethodTypeDAO = Spring.getBeanOfType(PurificationMethodTypeDAO.class);
			String sql = "INSERT INTO experiment_purification_method(fk_purification_method_type,fk_experiment_type) VALUES(?,?)";
			for(PurificationMethodType purificationMethodType : purificationMethodTypes){
				if(purificationMethodType.id==null)
					purificationMethodType = purificationMethodTypeDAO.add(purificationMethodType);
				jdbcTemplate.update(sql, purificationMethodType.id, experimentType.id);
			}
		}
		return experimentType;
	}

	public void update(ExperimentType experimentType)
	{
		ExperimentType expTypeDB = findById(experimentType.id);

		//Update experimentType attributes 
		String sqlUpdate = "UPDATE experiment_type SET doPurification=?, mandatoryPurification=?, doQualityControl=?,  mandatoryQualityControl=? WHERE id=?";
		jdbcTemplate.update(sqlUpdate, experimentType.doPurification, experimentType.mandatoryPurification, experimentType.doQualityControl, experimentType.mandatoryQualityControl, experimentType.id);
		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(experimentType);

		//Update protocols list (add new)
		List<Protocol> protocols = experimentType.protocols;
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				if(expTypeDB.protocols==null || (expTypeDB.protocols!=null && !expTypeDB.protocols.contains(protocol)))
					protocolDAO.add(protocol, experimentType.id);
			}
		}
		//Update InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experimentType.instrumentUsedTypes;
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(expTypeDB.instrumentUsedTypes==null || (expTypeDB.instrumentUsedTypes!=null && !expTypeDB.instrumentUsedTypes.contains(instrumentUsedType))){
					instrumentUsedTypeDAO.add(instrumentUsedType, experimentType.id);
				}
			}
		}

		//Update experimentCategory
		if(expTypeDB.experimentCategory.id!=experimentType.experimentCategory.id){
			sqlUpdate = "UPDATE experiment_type SET fk_experiment_category=? WHERE id=?";
			jdbcTemplate.update(sqlUpdate, experimentType.experimentCategory.id, experimentType.id);
		}
		//Update nexExperiment list (add new)
		List<ExperimentType> nextExpTypes = experimentType.previousExperimentTypes;
		System.out.println("Next experiment db "+expTypeDB.previousExperimentTypes);
		for(ExperimentType nextExpTypeDB : expTypeDB.previousExperimentTypes){
			System.out.println(nextExpTypeDB.id);
		}
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				if(expTypeDB.previousExperimentTypes==null || (expTypeDB.previousExperimentTypes!=null && !expTypeDB.previousExperimentTypes.contains(expType))){
					play.Logger.debug("Add next experiment types "+expType.id);
					jdbcTemplate.update(sql, experimentType.id, expType.id);
				}
			}
		}

		//Update QualityControl List
		List<QualityControlType> qualityControlTypes = experimentType.possibleQualityControlTypes;
		if(qualityControlTypes!=null && qualityControlTypes.size()>0){
			QualityControlTypeDAO qualityControlTypeDAO = Spring.getBeanOfType(QualityControlTypeDAO.class);
			String sql = "INSERT INTO experiment_quality_control(fk_quality_control_type,fk_experiment_type) VALUES(?,?)";
			for(QualityControlType qualityControlType : qualityControlTypes){
				if(expTypeDB.possibleQualityControlTypes==null || (expTypeDB.possibleQualityControlTypes!=null && !expTypeDB.possibleQualityControlTypes.contains(qualityControlType))){
					if(qualityControlType.id==null)
						qualityControlType=qualityControlTypeDAO.add(qualityControlType);
					jdbcTemplate.update(sql, qualityControlType.id,experimentType.id);
				}
			}
		}
		//Update Purification Method List
		List<PurificationMethodType> purificationMethodTypes = experimentType.possiblePurificationMethodTypes;
		if(purificationMethodTypes!=null && purificationMethodTypes.size()>0){
			PurificationMethodTypeDAO purificationMethodTypeDAO = Spring.getBeanOfType(PurificationMethodTypeDAO.class);
			String sql = "INSERT INTO experiment_purification_method(fk_purification_method_type,fk_experiment_type) VALUES(?,?)";
			for(PurificationMethodType purificationMethodType : purificationMethodTypes){
				if(expTypeDB.possiblePurificationMethodTypes==null || (expTypeDB.possiblePurificationMethodTypes!=null && !expTypeDB.possiblePurificationMethodTypes.contains(purificationMethodType))){
					if(purificationMethodType.id==null)
						purificationMethodType=purificationMethodTypeDAO.add(purificationMethodType);
					jdbcTemplate.update(sql, purificationMethodType.id,experimentType.id);
				}
			}
		}
	}
}
