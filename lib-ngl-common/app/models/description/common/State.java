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
import javax.persistence.Version;

import models.description.IDynamicType;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;

/**
 * Value of the possible state of type
 * must implement IDynamicType interface in order to be used in GenericType (temporary)
 * @author ejacoby
 *
 */
@Entity
public class State extends Model implements IDynamicType{

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
	
	@Column(nullable=false)
	public boolean active;
	
	public Integer priority;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(
			name="common_info_type_state",
			joinColumns=@JoinColumn(name="fk_state"),
			inverseJoinColumns=@JoinColumn(name="fk_common_info_type")
			)
	@JsonIgnore
	public List<CommonInfoType> commonInfoTypes;
	
	public static Model.Finder<Long,State> find = new Model.Finder<Long,State>(Long.class, State.class);
	
	public static Map<String, String> getMapPossibleStates()
	{
		Map<String, String> mapPossibleStates = new HashMap<String, String>();
		for(State possibleState : State.find.all()){
			mapPossibleStates.put(possibleState.id.toString(), possibleState.name);
		}
		return mapPossibleStates;
	}

	@Override
	public CommonInfoType getInformations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getIdType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDynamicType findById(long id) {
		return State.find.byId(id);
	}
	
}
