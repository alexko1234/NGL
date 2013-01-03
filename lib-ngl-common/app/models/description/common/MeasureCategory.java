package models.description.common;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class MeasureCategory extends Model{

	private static final long serialVersionUID = -7100165963375776740L;
	
	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	@Column(nullable=false)
	public String name;
	
	@Column(nullable=false,unique=true)
	public String code;
	
	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<PropertyDefinition> propertyDefinitions;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<MeasureValue> measurePossibleValues;
}
