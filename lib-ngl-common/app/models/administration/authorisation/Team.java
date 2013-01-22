package models.administration.authorisation;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

@Entity
public class Team extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;	
	
	@Required
	public String nom;	
	
	public static Finder<Integer,Team> find = new Finder<Integer,Team>(Integer.class, Team.class);
	
	public static Page<Team> page(int page, int pageSize, String sortBy, String order, String nom) {
		return 
				find.where()
				.ilike("nom", "%" + nom + "%")
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.getPage(page);
	}
}
