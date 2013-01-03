package models.description.experiment;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import models.description.common.CommonInfoType;
import play.db.ebean.Model;

@Entity
public class PurificationMethodType extends Model{

	private static final long serialVersionUID = 4797465384249349581L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_experiment_type")
	public List<Protocol> protocols; 
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(
			name="experiment_type_instrument_type",
			joinColumns=@JoinColumn(name="fk_experiment_type"),
			inverseJoinColumns=@JoinColumn(name="fk_instrument_type")
			)
	public List<InstrumentUsedType> instrumentTypes;
	
	@OneToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="fk_common_info_type")
	public CommonInfoType commonInfoType;

}
