package models.description.content;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import models.description.IDynamicType;
import models.description.common.CommonInfoType;
import play.db.ebean.Model;

@Entity
public class SampleType extends Model implements IDynamicType{

	private static final long serialVersionUID = 4797465384249349581L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	
	@OneToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_common_info_type")
	public CommonInfoType commonInfoType;

	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}
	
	public static Model.Finder<Long,SampleType> find = new Model.Finder<Long,SampleType>(Long.class, SampleType.class);
	
	@Override
	public long getIdType() {
		return id;
	}
	
	public IDynamicType findById(long id)
	{
		return SampleType.find.byId(id);
	}
}
