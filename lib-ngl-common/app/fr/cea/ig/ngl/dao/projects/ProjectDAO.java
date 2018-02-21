package fr.cea.ig.ngl.dao.projects;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.sun.org.apache.regexp.internal.recompile;

import controllers.ListForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.ngl.dao.GenericMongoDAO;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

@Singleton
public class ProjectDAO {

	// Generic access to db
	private final GenericMongoDAO<Project> gdao;
	
	@Inject
	public ProjectDAO() {
		gdao = new GenericMongoDAO<>(InstanceConstants.PROJECT_COLL_NAME, Project.class);
	}
	
	public Project findOne(DBQuery.Query q) throws DAOException {
		return gdao.findOne(q);
	}
	
	public Project findByCode(String code) throws DAOException {
		return gdao.findByCode(code);
	}
	
	public Iterable<Project> all() throws DAOException {
		return gdao.all();
	}
	
	public Iterable<Project> find(DBQuery.Query q) throws DAOException {
		return gdao.find(q);
	}
	
	public List<Project> findAsList(DBQuery.Query q) throws DAOException {
		return gdao.findAsList(q);
	}
	
	public MongoDBResult<Project> mongoDBFinder(ListForm form,  Query query) throws DAOException {
		return gdao.mongoDBFinder(form, query);
	}

	public Project saveObject(Project projectInput) {
		return gdao.saveObject(projectInput);
	}
	
	
}
