package models.laboratory.instrument.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;
import models.utils.DescriptionHelper;

@Repository
public class InstrumentUsedTypeDAO extends AbstractDAOMapping<InstrumentUsedType>{

	protected InstrumentUsedTypeDAO() {
		super("instrument_used_type", InstrumentUsedType.class, InstrumentUsedTypeMappingQuery.class, 
				"SELECT t.id, t.fk_common_info_type, t.fk_instrument_category "+
						"FROM instrument_used_type as t "+
						"JOIN common_info_type as c ON c.id=t.fk_common_info_type ", false);
	}

	public List<InstrumentUsedType> findByExperimentId(long id)
	{
		String sql = "SELECT it.id, it.fk_common_info_type, it.fk_instrument_category "+
				"FROM instrument_used_type as it "+
				"JOIN experiment_type_instrument_type as cit ON fk_instrument_used_type=it.id " +
				"JOIN common_info_type as c ON c.id=it.fk_common_info_type "+
				"JOIN common_info_type_institute ci on c.id =ci.fk_common_info_type "+
				"JOIN institute i on i.id = ci.fk_institute and i.code=" + DescriptionHelper.getInstitute() + " "+
				"WHERE cit.fk_experiment_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.execute(id);
	}

	/*
	public void save(List<InstrumentUsedType> instrumentUsedTypes, long idCommonInfoType) throws DAOException
	{
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				save(instrumentUsedType, idCommonInfoType);
			}
		}
	}

	public void save(InstrumentUsedType instrumentUsedType, long idCommonInfoType) throws DAOException
	{
		String sql = "INSERT INTO common_info_type_instrument_type(fk_common_info_type, fk_instrument_type) VALUES(?,?)";
		if(instrumentUsedType.code!=null && InstrumentUsedType.find.findByCode(instrumentUsedType.code)==null)
			instrumentUsedType.id = save(instrumentUsedType);
		jdbcTemplate.update(sql, idCommonInfoType, instrumentUsedType.id);
	}
*/
	@Override
	public long save(InstrumentUsedType instrumentUsedType) throws DAOException
	{
		
		if(null == instrumentUsedType){
			throw new DAOException("InstrumentUsedType is mandatory");
		}
		//Check if category exist
		if(instrumentUsedType.category == null || instrumentUsedType.category.id == null){
			throw new DAOException("InstrumentCategory is not present !!");
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		instrumentUsedType.id =commonInfoTypeDAO.save(instrumentUsedType);
		instrumentUsedType.setCommonInfoType(instrumentUsedType);
		
		//Create new InstrumentUsedType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", instrumentUsedType.id);
		parameters.put("fk_common_info_type", instrumentUsedType.id);
		parameters.put("fk_instrument_category", instrumentUsedType.category.id);
		jdbcInsert.execute(parameters);

		//Add instruments list
		saveInstruments(instrumentUsedType.id, instrumentUsedType.instruments, false);
		saveContainerSupportCategoryOut(instrumentUsedType.id,instrumentUsedType.outContainerSupportCategories,false);
		saveContainerSupportCategoryIn(instrumentUsedType.id,instrumentUsedType.inContainerSupportCategories,false);
		return instrumentUsedType.id;
	}


	private void saveContainerSupportCategoryIn(Long id,List<ContainerSupportCategory> containerSupportCategories, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeContainerSupportCategoryIn(id);
		}
		//Add resolutions list		
		if(containerSupportCategories!=null && containerSupportCategories.size()>0){
			String sql = "INSERT INTO instrument_ut_in_container_support_cat (fk_instrument_used_type,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory:containerSupportCategories){
				if(containerSupportCategory == null || containerSupportCategory.id == null ){
					throw new DAOException("containerSupportCategory is mandatory");
				}
				jdbcTemplate.update(sql, id,containerSupportCategory.id);
			}
		}
		
	}
	
	private void saveContainerSupportCategoryOut(Long id,List<ContainerSupportCategory> containerSupportCategories,  boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeContainerSupportCategoryOut(id);
		}
		
		//Add resolutions list		
		if(containerSupportCategories!=null && containerSupportCategories.size()>0){
			String sql = "INSERT INTO instrument_ut_out_container_support_cat (fk_instrument_used_type,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory:containerSupportCategories){
				Logger.debug("Out container support type save "+containerSupportCategory);
				if(containerSupportCategory == null || containerSupportCategory.id == null ){
					throw new DAOException("containerSupportCategory is mandatory");
				}
				jdbcTemplate.update(sql, id,containerSupportCategory.id);
			}
		}
		
	}

	private void removeContainerSupportCategoryOut(Long id) {
	
			String sql = "DELETE FROM instrument_ut_out_container_support_cat WHERE fk_instrument_used_type=?";
			jdbcTemplate.update(sql, id);
	}
	
	private void removeContainerSupportCategoryIn(Long id) {
		
		String sql = "DELETE FROM instrument_ut_in_container_support_cat WHERE fk_instrument_used_type=?";
		jdbcTemplate.update(sql, id);
}


	@Override
	public void update(InstrumentUsedType instrumentUsedType) throws DAOException 
	{
		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(instrumentUsedType);
		
		//Update instrument list
		saveInstruments(instrumentUsedType.id, instrumentUsedType.instruments, true);
		saveContainerSupportCategoryIn(instrumentUsedType.id, instrumentUsedType.inContainerSupportCategories, true);
		saveContainerSupportCategoryOut(instrumentUsedType.id, instrumentUsedType.outContainerSupportCategories, true);
	}

	@Override
	public void remove(InstrumentUsedType instrumentUsedType) throws DAOException {
		//remove from abstractExperiment common_info_type_instrument_type
		String sqlExp = "DELETE FROM experiment_type_instrument_type WHERE fk_instrument_used_type=?";
		jdbcTemplate.update(sqlExp, instrumentUsedType.id);
		//remove instruments
		removeInstruments(instrumentUsedType.id);
		removeContainerSupportCategoryIn(instrumentUsedType.id);
		removeContainerSupportCategoryOut(instrumentUsedType.id);
		//remove instrument used type
		super.remove(instrumentUsedType);
		//remove common_info_type
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(instrumentUsedType);
	}


	private void saveInstruments(Long id, List<Instrument> instruments, boolean deleteBefore) {
		if(deleteBefore){
			removeInstruments(id);
		}		
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				instrumentDAO.save(instrument, id);
			}
		}
	}

	private void removeInstruments(Long id) {
		String sqlInst = "DELETE FROM instrument WHERE fk_instrument_used_type=?";
		jdbcTemplate.update(sqlInst, id);
	}
}
