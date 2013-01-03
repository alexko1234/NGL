package models.description.common;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;



import play.db.ebean.Model;

@Entity
public class ObjectType extends Model {
	
	
	private static final long serialVersionUID = 1L;

	@Version
	public Long version;
	
	@Id @GeneratedValue
	@Column(name="id", nullable=false)
	public Long id;
	
	@Column(nullable=false)
	public String type;
	
	@Column(nullable=false)	
	public Boolean generic;
	
	public static Model.Finder<Long,ObjectType> find = new Model.Finder<Long,ObjectType>(Long.class, ObjectType.class);
	
	public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(ObjectType c: ObjectType.find.orderBy("type").findList()) {
            options.put(c.id.toString(), c.type);
        }
        return options;
    }
}
