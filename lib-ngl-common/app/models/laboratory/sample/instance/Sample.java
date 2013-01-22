package models.laboratory.sample.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.ObjectSGBDReference;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;

/**
 * 
 * Instances Sample are stored in MongoDB collection named Sample
 *  
 * Sample is referenced in Content
 *  
 * @author mhaquell
 *
 */
@MongoCollection(name="Sample")
public class Sample extends DBObject{
	
	// SampleType Ref
	public String typeCode;
	//Sample Category Ref
	public String categoryCode;
	
	// ?? Wath is difference with code / referenceCollbab 
	public String name;
	public String referenceCollab; 
	public Map<String,PropertyValue> properties;
	// Valid taxon
	public TBoolean valid;
	//public List<CollaboratorInvolve> collaborators;
	public String taxonCode;
	
	@JsonIgnore
	public SampleType getSampleType(){
		try {
			return new ObjectSGBDReference<SampleType>(SampleType.class,typeCode).getObject();
		} catch (Exception e) {
			// TODO
		
		}
		return null;
	}
	
	@JsonIgnore
	public SampleCategory getSampleCategory(){
		try {
			return new ObjectSGBDReference<SampleCategory>(SampleCategory.class,categoryCode).getObject();
		} catch (Exception e) {
			// TODO
		
		}
		return null;
	}
	

}
