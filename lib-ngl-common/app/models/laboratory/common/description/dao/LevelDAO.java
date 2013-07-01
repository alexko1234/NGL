package models.laboratory.common.description.dao;

import models.laboratory.common.description.Level;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class LevelDAO extends AbstractDAO<Level>{

	protected LevelDAO() {
		super("level", Level.class, true);
	}

	

	
}
