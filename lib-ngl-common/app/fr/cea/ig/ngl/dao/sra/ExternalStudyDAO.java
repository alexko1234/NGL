package fr.cea.ig.ngl.dao.sra;

import javax.inject.Inject;

import models.sra.submit.common.instance.AbstractStudy;
import models.sra.submit.common.instance.ExternalStudy;
import models.utils.InstanceConstants;
import fr.cea.ig.ngl.dao.GenericMongoDAO;

public class ExternalStudyDAO extends GenericMongoDAO<ExternalStudy> {

	@Inject
	public ExternalStudyDAO() {
		super(InstanceConstants.SRA_STUDY_COLL_NAME, ExternalStudy.class);
	}

}
