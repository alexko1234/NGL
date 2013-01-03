package models.description.common;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class PropertyDefinition extends Model {

	private static final long serialVersionUID = 1L;
	private static LinkedHashMap<String,String> options;
	{
	    options = new LinkedHashMap<String,String>();
	    options.put(String.class.getName(), "String");
	    options.put(Integer.class.getName(), "Integer");        
	    options.put(Long.class.getName(), "Long");
	    options.put(Float.class.getName(), "Float");
	    options.put(Double.class.getName(), "Double");
	    options.put(Date.class.getName(), "Date");	
	    options.put(Boolean.class.getName(), "Boolean");	
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Long id;
	
	@Version
	public Long version;
	
	@Required
	@Column(nullable = false)
	public String code;
	
	@Required	
	@Column(nullable = false)
	public String name;

	@Column(nullable = false)
	public Boolean required = Boolean.FALSE;

	@Column(nullable = false)
	public Boolean active = Boolean.TRUE;

	@Column(nullable = false)
	public Boolean choiceInList = Boolean.FALSE;
	
	@Required	
	@Column(nullable = false)
	public String type;
	public String displayFormat;
	public Integer displayOrder;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<Value> possibleValues;

	public String defaultValue;
	
	@ManyToOne
	public MeasureCategory measureCategory;
	
	@ManyToOne
	public MeasureValue measureValue;
	
	@JsonIgnore
	@ManyToOne
	public CommonInfoType commonInfoType; 
	//obligation de mettre des relation bidirectionnel sinon la sauvegarde ne marge pas avec update.
	//De plus pour eviter une recursivité dans la transformation en Json de l'objet CommonInfoType il faut mettre JsonIgnore.
	
	public static Map<String,String> options() {
        return options;
    }
	

}
