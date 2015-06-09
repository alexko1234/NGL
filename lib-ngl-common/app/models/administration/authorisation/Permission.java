package models.administration.authorisation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

@Entity
public class Permission extends Model{

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Integer id;	
		
		@Required
		public String label;
		
		@Required
		public String code;
		
		public static Finder<Integer,Permission> find = new Finder<Integer,Permission>(Integer.class, Permission.class);
		
		public static Page<Permission> page(int page, int pageSize, String sortBy, String order, String code,  String label) {
			return 
					find.where()
					.ilike("code", "%" + code + "%")
					.ilike("label",  "%" + label + "%")
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
					.getPage(page);
		}

}
