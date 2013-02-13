package models.laboratory.sample.description.dao;

import models.laboratory.sample.description.SampleCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class SampleCategoryDAO extends AbstractDAO<SampleCategory>{

	public SampleCategoryDAO() {
		super("sample_category",SampleCategory.class,true);
	}

}
