package models.description.experiment;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import play.db.ebean.Model;
@Entity
public class Protocol extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6569957949770322825L;
	
	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	public String name;
	public String filePath;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public List<ReagentType> reagentTypes;
	
	//TODO Detail manip et dosage manip ???
	
	public static Model.Finder<Long,Protocol> find = new Model.Finder<Long,Protocol>(Long.class, Protocol.class);
}
