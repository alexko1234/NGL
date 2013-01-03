package models.description.experiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.codehaus.jackson.annotate.JsonIgnore;

import models.description.IDynamicType;
import models.description.common.CommonInfoType;
import play.db.ebean.Model;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
@Entity
public class InstrumentUsedType extends Model implements IDynamicType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2838156267681157498L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="experiment_type_instrument_type",
			joinColumns=@JoinColumn(name="FK_instrument_type"),
			inverseJoinColumns=@JoinColumn(name="FK_experiment_type")
			)
	public List<ExperimentType> experimentTypes;
	
	@OneToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_common_info_type")
	public CommonInfoType commonInfoType;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Instrument> instruments;
	
	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}
	
	public static Model.Finder<Long,InstrumentUsedType> find = new Model.Finder<Long,InstrumentUsedType>(Long.class, InstrumentUsedType.class);
	//public static FinderType find = new FinderType(Long.class, InstrumentType.class);

	@Override
	public long getIdType() {
		return id;
	}
	
	public static Map<String, String> getMapInstrumentTypes()
	{
		Map<String, String> mapInstrumentTypes = new HashMap<String, String>();
		for(InstrumentUsedType instType : InstrumentUsedType.find.fetch("commonInfoType").findList()){
			mapInstrumentTypes.put(instType.id.toString(), instType.commonInfoType.name);
		}
		return mapInstrumentTypes;
	}
	public IDynamicType findById(long id)
	{
		return InstrumentUsedType.find.byId(id);
	}

	public static InstrumentUsedType findByCommonInfoType(Long idCommonInfoType)
	{
		return InstrumentUsedType.find.where().eq("commonInfoType.id", idCommonInfoType).findUnique();
	}

	
}
