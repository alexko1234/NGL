package fr.cea.ig.mongo;

import java.util.Arrays;
import java.util.Collections;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;

import views.components.datatable.IDatatableForm;

public interface DBObjectRestrictor {
	default BasicDBObject getKeys(IDatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		if(!form.includes().contains("*")){
			keys.putAll((BSONObject)getIncludeKeys(form.includes().toArray(new String[form.includes().size()])));
		}
		keys.putAll((BSONObject)getExcludeKeys(form.excludes().toArray(new String[form.excludes().size()])));		
		return keys;
	}
	
	default BasicDBObject getIncludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	default BasicDBObject getExcludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 0);
		}
		return values;
    }
}
