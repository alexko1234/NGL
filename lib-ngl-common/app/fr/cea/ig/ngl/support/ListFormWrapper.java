package fr.cea.ig.ngl.support;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.ListForm;
import fr.cea.ig.DBObject;
import views.components.datatable.IDatatableForm;

/**
 * Wrapper around {@link ListForm} object. 
 * @author ajosso
 *
 * @param <T> resource object
 */
public class ListFormWrapper<T extends DBObject> {

	private final ListForm form;
	private final Function<IDatatableForm, BasicDBObject> basicDBObjectGenerator;
	
	public ListFormWrapper(ListForm form, Function<IDatatableForm, BasicDBObject> basicDBObjectGenerator) {
		this.form = form;
		this.basicDBObjectGenerator = basicDBObjectGenerator;
	}
	
	public boolean isMongoJackMode() {
		return ! isAggregateMode() && ! isReportingMode(); // default mode
	}

	public boolean isAggregateMode() {
		// TODO check conditions
		return form.aggregate && ! form.reporting;
	}

	public boolean isReportingMode() {
		return form.reporting && StringUtils.isNotBlank(form.reportingQuery) &&  ! form.aggregate;
	}
	
	public String reportingQuery() {
		if(isReportingMode()) {
			return form.reportingQuery;
		} else {
			return null;
		}
	}
	
	public DBQuery.Query getQuery() {
		if(isMongoJackMode()) {
			return form.getQuery();
		} else {
			return null;
		}
	} 

	public BasicDBObject getKeys(List<String> defaultKeys) {
		// replace "default" keyword by the list of default keys
		if(form.includes().contains("default")){
			form.includes().remove("default");
			if(CollectionUtils.isNotEmpty(defaultKeys)){
				form.includes().addAll(defaultKeys);
			}
		}
		return this.basicDBObjectGenerator.apply(form);
	}

	/**
	 * Define how to return results. 
	 * @see ListForm#transform() Concrete implementation
	 * @return the function to transform results
	 */
	public Function<Iterable<T>, Source<ByteString,?>> transform() {
		return form.transform();
	}
}
