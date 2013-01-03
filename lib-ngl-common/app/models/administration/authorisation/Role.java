package models.administration.authorisation;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Page;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Role extends Model{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public java.lang.Integer id;
	
	@Required
	public String label;
	
	@ManyToMany(cascade={CascadeType.ALL})
	public List<Permission> permissions;

	public static Finder<Integer,Role> find = new Finder<Integer,Role>(Integer.class, Role.class);
	
	public static Page<Role> page(int page, int pageSize, String sortBy, String order, String label) {
		return 
				find.where()
				.ilike("label",  "%" + label + "%")
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.getPage(page);
	}
}
