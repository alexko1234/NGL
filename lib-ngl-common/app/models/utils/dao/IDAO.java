package models.utils.dao;

import java.util.List;

import javax.sql.DataSource;

/**
 * Services must be implemented by all DAO
 * @author ejacoby
 *
 * @param <T>
 */
public interface IDAO<T> {

	public void setDataSource(DataSource dataSource);

	public List<T> findAll() throws DAOException;

	public T findById(long id) throws DAOException;;

	public T findByCode(String code) throws DAOException;

	public long save(T value) throws DAOException;
	
	public void update(T value) throws DAOException;;
	
	public void remove(T value);
	
}
