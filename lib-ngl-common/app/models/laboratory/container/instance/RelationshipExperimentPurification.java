package models.laboratory.container.instance;


import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;


import org.mongojack.MongoCollection;

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
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}
	
	/* An other solution : We can model relation between Experiment and Purification in a tree

			String containerCode // Code Container
			String origineType // type is "Experiment", "Purification" or others if need
			String path 	// Path of code instance in tree ( Example : "EXP20130110>PURIF20130110")
	
	*/

}
