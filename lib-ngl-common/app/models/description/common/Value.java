package models.description.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;
@Entity
public class Value extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5653508299965193830L;
	@Version
	public Long version;
	
	@Id
	public Long id;
	@Column(nullable=false)
	public String value;   
	@Column(nullable=false)
    public Boolean defaultValue = Boolean.FALSE;
  	
	@JsonIgnore
	@ManyToOne
	public PropertyDefinition propertyDefinition;
}
