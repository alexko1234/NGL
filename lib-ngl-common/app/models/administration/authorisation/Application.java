package models.administration.authorisation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Application extends Model{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;	
	
	@Required
	public String label;
	
	@Required
	public String code;
	
	public static Finder<Integer,Application> find = new Finder<Integer,Application>(Integer.class, Application.class);
	
	public static Page<Application> page(int page, int pageSize, String sortBy, String order, String code,  String label) {
		return 
				find.where()
				.ilike("code", "%" + code + "%")
				.ilike("label",  "%" + label + "%")
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.getPage(page);
	}
	
}
