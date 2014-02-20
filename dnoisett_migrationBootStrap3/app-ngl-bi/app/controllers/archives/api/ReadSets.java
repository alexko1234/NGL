package controllers.archives.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.codehaus.jackson.JsonNode;


import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
import controllers.authorisation.Permission;
/**
 * Controller that manage the readset archive
 * @author galbini
 *
 */
public class ReadSets extends CommonController{
/**
	 *
	 * @param archive : default 2
	 * @return
	 */
	 
	//@Permission(value={"reading"})
	public static Result list(){

		Integer archive = getArchiveValue();
		List<Archive> archives = new ArrayList<Archive>();

		List<ReadSet> readSets =  MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, getQuery(archive)).toList();
		if (readSets == null) {
			return notFound();
		}
		for (ReadSet readSet : readSets) {
			if (readSet != null) {
				if ( (archive.intValue() == 0) ||
						(archive.intValue() == 1 && readSet.archiveId != null) ||
						(archive.intValue() == 2 && readSet.archiveId == null) ) {
					archives.add(createArchive(readSet));
				}
			}
		}
		return ok(Json.toJson(new DatatableResponse<Archive>(archives)));
	}



	private static Integer getArchiveValue() {
		try {
			return Integer.valueOf(request().queryString().get("archive")[0]);

		} catch(Exception e) {
			Logger.error(e.getMessage());
			return 2; // default value;
		}
	}

	private static Query getQuery(Integer archive) {
		Query query = null;
		if (archive.intValue() == 0) { //all
			query = DBQuery.is("dispatch", true);
		} else if(archive.intValue() == 1) { //archive
			query = DBQuery.and(DBQuery.is("dispatch", true), DBQuery.exists("archiveId"));
		} else { //not archive value = 2
			query = DBQuery.and(DBQuery.is("dispatch", true), DBQuery.notExists("archiveId"));
		}
		return query;
	}

	private static Archive createArchive(ReadSet readset) {
		Archive archive =  new Archive();
		archive.runCode=readset.runCode;
		archive.projectCode=readset.projectCode;
		archive.readSetCode = readset.code;
		archive.path = readset.path;
		archive.id = readset.archiveId;
		archive.date = readset.archiveDate;

		return archive;
	}

	//@Permission(value={"archiving"})
	public static Result save(String readSetCode) {
		JsonNode json = request().body().asJson();
		String archiveId = json.get("archiveId").asText();

		if (archiveId != null) {
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);

			if(readSet == null) {
				return notFound();
			}

			if (readSet.code.equals(readSetCode)) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("archiveId", archiveId); //Update
				map.put("archiveDate", new Date());
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet, map);
				return ok();
			}
			else {
				return notFound();
			}
		}
		else{
			return badRequest();
		}
	}


}
