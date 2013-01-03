package models.description.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;


import play.db.ebean.Model;

@Entity
public class DefaultValue extends Model{

	private static final long serialVersionUID = 1L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="defaultValue_id", nullable=false)
	public Long id;
	
	public String value;
	
}
