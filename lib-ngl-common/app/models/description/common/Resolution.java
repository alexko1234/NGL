package models.description.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import models.description.IDynamicType;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;

/**
 * Value of the resolution of final possible state
 * @author ejacoby
 *
 */
@Entity
public class Resolution extends Model{

	private static final long serialVersionUID = 1L;

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
	@ManyToOne
	public CommonInfoType commonInfoType; 
	
}
