package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import models.utils.IValidation;
import net.vz.mongodb.jackson.MongoCollection;

/*
 * Instances of RelationshipExperimentPurification are stored in MongoDB collection named RelationshipExperimentPurification 
 * Experiment instance is composed by a series of purification
 * 
 * */	
@MongoCollection(name="RelationshipExperimentPurification")
public class RelationshipExperimentPurification  implements IValidation{

	// Reference Code in Experiment 
	public String experimentCode;
	// Reference container out put of Experiment
	public String containerCode;
	//Reference Code in Purification
	public String purificationCode;
	//Position in serie of purification
	public String position;
	
	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exist(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* An other solution : We can model relation between Experiment and Purification in a tree

			String containerCode // Code Container
			String origineType // type is "Experiment", "Purification" or others if need
			String path 	// Path of code instance in tree ( Example : "EXP20130110>PURIF20130110")
	
	*/

}
