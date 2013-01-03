package models.description.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import play.db.ebean.Model;

@Entity
public class ContainerCategory extends Model{

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
}
