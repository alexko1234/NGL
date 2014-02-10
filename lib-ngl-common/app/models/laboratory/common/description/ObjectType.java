package models.laboratory.common.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.utils.Model;

/**
 * Type definition
 * @author ejacoby
 *
 */
public class ObjectType extends Model<ObjectType>{
	public enum CODE {Project, Process, Sample, Container, Instrument, Reagent,
		Experiment, Import, Run, Treatment, ReadSet }; 
	
	public List<State> states = new ArrayList<State>();
	
	public static Finder<ObjectType> find = new Finder<ObjectType>(ObjectTypeDAO.class.getName()); 

	public ObjectType() {
		super(ObjectTypeDAO.class.getName());
	}

	//Set true if type has additional attributes compared to commonInfoType
	public Boolean generic;
		
}


