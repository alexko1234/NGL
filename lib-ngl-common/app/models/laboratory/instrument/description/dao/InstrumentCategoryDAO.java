package models.laboratory.instrument.description.dao;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class InstrumentCategoryDAO extends AbstractDAO<InstrumentCategory>{

	public InstrumentCategoryDAO() {
		super("instrument_category",InstrumentCategory.class,true);
	}

	
	
/*
	@Override
	public void update(InstrumentCategory instrumentCategory) throws DAOException
	{
		InstrumentCategory instrumentCategoryDB = findById(instrumentCategory.id);
		super.update(instrumentCategory);

		//Add new in and out containerSupportCategories
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		if(instrumentCategory.inContainerSupportCategories!=null && instrumentCategory.inContainerSupportCategories.size()>0){
			String sqlIn = "INSERT INTO instrumentCategory_inContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.inContainerSupportCategories){
				if(instrumentCategoryDB.inContainerSupportCategories==null || (instrumentCategoryDB.inContainerSupportCategories!=null && !instrumentCategoryDB.inContainerSupportCategories.contains(containerSupportCategory))){
					ContainerSupportCategory containerSupportCategoryDB = ContainerSupportCategory.find.findByCode(containerSupportCategory.code);
					if(containerSupportCategoryDB ==null)
						containerSupportCategory.id=containerSupportCategoryDAO.save(containerSupportCategory);
					else
						containerSupportCategory=containerSupportCategoryDB;
					jdbcTemplate.update(sqlIn, instrumentCategory.id, containerSupportCategory.id);
				}
			}
		}
		if(instrumentCategory.outContainerSupportCategories!=null && instrumentCategory.outContainerSupportCategories.size()>0){
			String sqlOut = "INSERT INTO instrumentCategory_outContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.outContainerSupportCategories){
				if(instrumentCategoryDB.outContainerSupportCategories==null || (instrumentCategoryDB.outContainerSupportCategories!=null && !instrumentCategoryDB.outContainerSupportCategories.contains(containerSupportCategory))){
					ContainerSupportCategory containerSupportCategoryDB = ContainerSupportCategory.find.findByCode(containerSupportCategory.code);
					if(containerSupportCategoryDB ==null)
						containerSupportCategory.id=containerSupportCategoryDAO.save(containerSupportCategory);
					else
						containerSupportCategory=containerSupportCategoryDB;
					jdbcTemplate.update(sqlOut, instrumentCategory.id, containerSupportCategory.id);
				}
			}
		}
	}

	@Override
	public void remove(InstrumentCategory instrumentCategory) throws DAOException
	{
		//remove inContainerSupportCategories instrumentCategory_inContainerSupportCategory
		String sqlIn = "DELETE FROM instrumentCategory_inContainerSupportCategory WHERE fk_instrument_category=?";
		jdbcTemplate.update(sqlIn, instrumentCategory.id);
		//remove outContainerSupportCategories instrumentCategory_outContainerSupportCategory
		String sqlOut = "DELETE FROM instrumentCategory_outContainerSupportCategory WHERE fk_instrument_category=?";
		jdbcTemplate.update(sqlOut, instrumentCategory.id);
		//remove instrumentCategory
		super.remove(instrumentCategory);
	}
	*/
}
