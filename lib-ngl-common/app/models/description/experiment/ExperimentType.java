package models.description.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import models.description.IDynamicType;
import models.description.common.CommonInfoType;
import play.db.ebean.Model;

@Entity
public class ExperimentType extends Model implements IDynamicType{

	private static final long serialVersionUID = 4797465384249349581L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(
			name="next_experiment_types",
			joinColumns=@JoinColumn(name="fk_experiment_type"),
			inverseJoinColumns=@JoinColumn(name="fk_next_experiment_type")
			)
	public List<ExperimentType> nextExperimentTypes=new ArrayList<ExperimentType>();

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_experiment_type")
	public List<Protocol> protocols; 
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="experiment_type_instrument_type",
			joinColumns=@JoinColumn(name="fk_experiment_type"),
			inverseJoinColumns=@JoinColumn(name="fk_instrument_type")
			)
	public List<InstrumentUsedType> instrumentTypes;
	
	@OneToOne(optional=false, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_common_info_type")
	public CommonInfoType commonInfoType;

	
	
	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}
	
	public static Model.Finder<Long,ExperimentType> find = new Model.Finder<Long,ExperimentType>(Long.class, ExperimentType.class);
	
	public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(ExperimentType c: ExperimentType.find.findList()) {
            options.put(c.getInformations().id.toString(), c.getInformations().name);
        }
        return options;
    }

	public static Map<String, String> getMapExperimentTypes()
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		for(ExperimentType expType : ExperimentType.find.fetch("commonInfoType").findList()){
			mapExperimentTypes.put(expType.id.toString(), expType.commonInfoType.name);
		}
		return mapExperimentTypes;
	}
	
	public static Map<String, String> getMapExperimentTypes(long filterId)
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		for(ExperimentType expType : ExperimentType.find.fetch("commonInfoType").findList()){
			if(expType.id!=filterId)
				mapExperimentTypes.put(expType.id.toString(), expType.commonInfoType.name);
		}
		return mapExperimentTypes;
	}
	
	public static ExperimentType findByCommonInfoType(Long idCommonInfoType)
	{
		return ExperimentType.find.fetch("nextExperimentTypes").where().eq("commonInfoType.id", idCommonInfoType).findUnique();
	}
	@Override
	public long getIdType() {
		return id;
	}
	
	public IDynamicType findById(long id)
	{
		return ExperimentType.find.byId(id);
	}
	
	
}
