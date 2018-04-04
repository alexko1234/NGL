package models.laboratory.common.description;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;

/**
 * Type definition
 * @author ejacoby
 *
 */
public class ObjectType extends Model<ObjectType> {
	
//	public static Finder<ObjectType> find = new Finder<ObjectType>(ObjectTypeDAO.class.getName()); 
	public static final Finder<ObjectType,ObjectTypeDAO> find = new Finder<>(ObjectTypeDAO.class); 

	public enum CODE {
		Project, 
		Process, 
		Sample, 
		Container, 
		Instrument, 
		Reagent,
		Experiment, 
		Import, 
		Run, 
		Treatment, 
		ReadSet, 
		Analysis, 
		SRASubmission, 
		SRAConfiguration, 
		SRAStudy, 
		SRASample, 
		SRAExperiment 
	} 

	//Set true if type has additional attributes compared to commonInfoType
	public Boolean generic;
		
	public ObjectType() {
		super(ObjectTypeDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ObjectType>> daoClass() {
		return ObjectTypeDAO.class;
	}

}

