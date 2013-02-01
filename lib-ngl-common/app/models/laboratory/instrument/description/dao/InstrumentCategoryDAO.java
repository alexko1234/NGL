package models.laboratory.instrument.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.laboratory.instrument.description.InstrumentCategory;

import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class InstrumentCategoryDAO extends AbstractCategoryDAO<InstrumentCategory>{

	public InstrumentCategoryDAO() {
		super("instrument_category",InstrumentCategory.class);
	}

	public InstrumentCategory findById(long id)
	{
		InstrumentCategory instrumentCategory = (InstrumentCategory) super.findById(id);
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		//Find inContainerSupportCategories
		instrumentCategory.inContainerSupportCategories = containerSupportCategoryDAO.findInByInstrumentCategory(instrumentCategory.id);
		//Find outContainerSupportCategorie
		instrumentCategory.outContainerSupportCategories = containerSupportCategoryDAO.findOutByInstrumentCategory(instrumentCategory.id);
		return instrumentCategory;
	}

	public InstrumentCategory findByCode(String code)
	{
		InstrumentCategory instrumentCategory = (InstrumentCategory) super.findByCode(code);
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		//Find inContainerSupportCategories
		instrumentCategory.inContainerSupportCategories = containerSupportCategoryDAO.findInByInstrumentCategory(instrumentCategory.id);
		//Find outContainerSupportCategorie
		instrumentCategory.outContainerSupportCategories = containerSupportCategoryDAO.findOutByInstrumentCategory(instrumentCategory.id);
		return instrumentCategory;
	}


	public InstrumentCategory add(InstrumentCategory instrumentCategory)
	{
		instrumentCategory = (InstrumentCategory) super.add(instrumentCategory);
		//Add in and out containerSupportCategories
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		if(instrumentCategory.inContainerSupportCategories!=null && instrumentCategory.inContainerSupportCategories.size()>0){
			String sqlIn = "INSERT INTO instrumentCategory_inContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.inContainerSupportCategories){
				if(containerSupportCategory.id==null)
					containerSupportCategory=containerSupportCategoryDAO.add(containerSupportCategory);
				jdbcTemplate.update(sqlIn, instrumentCategory.id, containerSupportCategory.id);
			}
		}
		if(instrumentCategory.outContainerSupportCategories!=null && instrumentCategory.outContainerSupportCategories.size()>0){
			String sqlOut = "INSERT INTO instrumentCategory_outContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.outContainerSupportCategories){
				if(containerSupportCategory.id==null)
					containerSupportCategory=containerSupportCategoryDAO.add(containerSupportCategory);
				jdbcTemplate.update(sqlOut, instrumentCategory.id, containerSupportCategory.id);
			}
		}

		return instrumentCategory;
	}

	public void update(InstrumentCategory instrumentCategory)
	{
		InstrumentCategory instrumentCategoryDB = findById(instrumentCategory.id);
		super.update(instrumentCategory);

		//Add new in and out containerSupportCategories
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		if(instrumentCategory.inContainerSupportCategories!=null && instrumentCategory.inContainerSupportCategories.size()>0){
			String sqlIn = "INSERT INTO instrumentCategory_inContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.inContainerSupportCategories){
				if(instrumentCategoryDB.inContainerSupportCategories==null || (instrumentCategoryDB.inContainerSupportCategories!=null && !instrumentCategoryDB.inContainerSupportCategories.contains(containerSupportCategory))){
					if(containerSupportCategory.id==null)
						containerSupportCategory=containerSupportCategoryDAO.add(containerSupportCategory);
					jdbcTemplate.update(sqlIn, instrumentCategory.id, containerSupportCategory.id);
				}
			}
		}
		if(instrumentCategory.outContainerSupportCategories!=null && instrumentCategory.outContainerSupportCategories.size()>0){
			String sqlOut = "INSERT INTO instrumentCategory_outContainerSupportCategory(fk_instrument_category,fk_container_support_category) VALUES(?,?)";
			for(ContainerSupportCategory containerSupportCategory : instrumentCategory.outContainerSupportCategories){
				if(instrumentCategoryDB.outContainerSupportCategories==null || (instrumentCategoryDB.outContainerSupportCategories!=null && !instrumentCategoryDB.outContainerSupportCategories.contains(containerSupportCategory))){
					if(containerSupportCategory.id==null)
						containerSupportCategory=containerSupportCategoryDAO.add(containerSupportCategory);
					jdbcTemplate.update(sqlOut, instrumentCategory.id, containerSupportCategory.id);
				}
			}
		}

	}
}
