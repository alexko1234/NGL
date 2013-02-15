package models.laboratory.experiment.description.dao;

import models.laboratory.experiment.description.PurificationMethodType;

import org.springframework.stereotype.Repository;

@Repository
public class TransferMethodTypeDAO extends AbstractExperimentDAO<PurificationMethodType>{

	public TransferMethodTypeDAO() {
		super("transfer_method_type", PurificationMethodType.class,PurificationMethodTypeMappingQuery.class,
				"SELECT t.id, fk_common_info_type, code "+
				"FROM transfer_method_type as t "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type ");
		
	}

}
