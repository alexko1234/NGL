package models.description.common;


import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.Valid;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

@Entity
public class CommonInfoType extends Model{
	
	private static final long serialVersionUID = 1L;

	@Version
	public Long version;

	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	@Required
	@MaxLength(10)
	@Column(nullable=false,unique=true)
	public String name; //used as label
	@Required
	@Column(nullable=false,unique=true)
	public String code; //used for research in mongodb

	//document-oriented NoSQL database system (actually MongoDB) collection name 
	@Required
	@Column(nullable=false,unique=true)
	public String collectionName;

	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(
			name="common_info_type_state",
			joinColumns=@JoinColumn(name="fk_common_info_type"),
			inverseJoinColumns=@JoinColumn(name="fk_state")
			)
	public List<State> variableStates = new ArrayList<State>();

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<Resolution> resolutions = new ArrayList<Resolution>();
	
	@Valid
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<PropertyDefinition> propertiesDefinition=new ArrayList<PropertyDefinition>();

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="fk_object_type")
	public ObjectType objectType;
	
	public static Model.Finder<Long,CommonInfoType> find = new Model.Finder<Long,CommonInfoType>(Long.class, CommonInfoType.class);

	/**
	 * Return a page of CommonInfoType
	 *
	 * @param page Page to display
	 * @param pageSize Number of computers per page
	 * @param sortBy Computer property used for sorting
	 * @param order Sort order (either or asc or desc)
	 * @param filter Filter applied on the name column
	 */
	public static Page<CommonInfoType> page(int page, int pageSize, String sortBy, String order, String typeName, Long objectTypeId) {
		System.out.println("Find page");
		return 
				find.where()
				.ilike("name", "%" + typeName + "%")
				.eq("objectType.id", objectTypeId)
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.getPage(page);
	}


	public List<PropertyDefinition> getPropertiesDefinition() {
		return propertiesDefinition;
	}


	public void setPropertiesDefinition(
			List<PropertyDefinition> propertiesDefinition) {
		this.propertiesDefinition = propertiesDefinition;
	}


}
