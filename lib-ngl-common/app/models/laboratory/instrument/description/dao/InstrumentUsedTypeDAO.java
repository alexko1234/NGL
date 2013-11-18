package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;

@Repository
public class InstrumentUsedTypeDAO extends AbstractDAOCommonInfoType<InstrumentUsedType>{

	protected InstrumentUsedTypeDAO() {
		super("instrument_used_type", InstrumentUsedType.class, InstrumentUsedTypeMappingQuery.class, 
				"SELECT distinct c.id, c.fk_common_info_type, c.fk_instrument_category ",
						"FROM instrument_used_type as c "+sqlCommonInfoType, false);
	}

	public List<InstrumentUsedType> findByExperimentId(long id)
	{
		String sql=sqlCommonSelect+sqlCommonFrom+
				"JOIN experiment_type_instrument_type as cit ON fk_instrument_used_type=c.id " +
				DAOHelpers.getSQLForInstitute()+
				"and cit.fk_experiment_type = ?";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.execute(id);
	}

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
