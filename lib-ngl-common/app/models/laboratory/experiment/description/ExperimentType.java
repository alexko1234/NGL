package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.Level;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.InstrumentUsedType;

/**
 * Parent class categories not represented by a table in the database
 * Database relationship for experiment with instrumentUsedType and protocol are represented in CommonInfoType table
 * @author ejacoby
 *
 */
public class ExperimentType extends CommonInfoType{
 
	public ExperimentCategory category;
	//Relationship accessible by the parent table in the database
	public List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
	public List<Protocol> protocols = new ArrayList<Protocol>();
	public String atomicTransfertMethod;
	
	public ExperimentType() {
		super(ExperimentTypeDAO.class.getName());		
	}
	
	public static Finder<ExperimentType> find = new Finder<ExperimentType>(ExperimentTypeDAO.class.getName());
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(models.laboratory.common.description.Level.CODE.Experiment);
	}
	
}
