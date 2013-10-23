package models.laboratory.common.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.utils.Model;

/**
 * Type definition
 * @author ejacoby
 * dnoisett : add attribute List<State> states+ update codes
 */
public class ObjectType extends Model<ObjectType>{
	
	public List<State> states = new ArrayList<State>();
	
	public enum CODE {Project, Process, Sample, Instrument, Container, Reagent, Experiment, Import, Run, Treatment, ReadSet, Lane, File}; 
	
	public static Finder<ObjectType> find = new Finder<ObjectType>(ObjectTypeDAO.class.getName()); 

	public ObjectType() {
		super(ObjectTypeDAO.class.getName());
	}

	//Set true if type has additional attributes compared to commonInfoType
	public Boolean generic;
		
}


