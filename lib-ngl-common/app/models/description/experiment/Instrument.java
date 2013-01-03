package models.description.experiment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class Instrument extends Model {

	private static final long serialVersionUID = 6457426460396972102L;
	
	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	public String name;
	
	@Column(nullable=false,unique=true)
	public String code;
	
	@JsonIgnore
	@ManyToOne
	public InstrumentUsedType instrumentUsedType;

}
