package models.laboratory.sample.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.sample.description.SampleCategory;

import org.springframework.stereotype.Repository;

@Repository
public class SampleCategoryDAO extends AbstractCategoryDAO<SampleCategory>{

	public SampleCategoryDAO() {
		super("sample_category",SampleCategory.class);
	}

}
