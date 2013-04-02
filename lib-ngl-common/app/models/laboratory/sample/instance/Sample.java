package models.laboratory.sample.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.HelperObjects;
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
	
	
	@JsonIgnore
	public final static String HEADER="Sample.code;Sample.projectCodes;Sample.name;Sample.referenceCollab;Sample.taxonCode;Sample.comments";
	
	// SampleType Ref
	public String typeCode;
	//Sample Category Ref
	public String categoryCode;
	
	public List<String> projectCodes;
	
	// ?? Wath is difference with code / referenceCollbab => code s'est interne au genoscope
	public String name;
	public String referenceCollab; 
	public Map<String,PropertyValue> properties;
	// Valid taxon
	public TBoolean valid;
	//public List<CollaboratorInvolve> collaborators;
	public String taxonCode;
		
	public List<Comment> comments;
	public TraceInformation traceInformation;
	
	public Sample(){
		this.traceInformation=new TraceInformation();
	}
	
	
	@JsonIgnore
	public SampleType getSampleType(){
		return new HelperObjects<SampleType>().getObject(SampleType.class, typeCode, null);
	}
	
	@JsonIgnore
	public SampleCategory getSampleCategory(){
		return new HelperObjects<SampleCategory>().getObject(SampleCategory.class, categoryCode, null);
	}
	
	@JsonIgnore
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}
	

}
