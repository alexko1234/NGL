package models.description.experiment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.description.common.CommonInfoType;

import play.db.ebean.Model;

@Entity
public class ReagentType extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3774684919960878738L;
	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	@OneToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_common_info_type")
	public CommonInfoType commonInfoType;
	
	@JsonIgnore
	@ManyToOne
	public Protocol protocol;
	
	public static Model.Finder<Long,ReagentType> find = new Model.Finder<Long,ReagentType>(Long.class, ReagentType.class);
}
