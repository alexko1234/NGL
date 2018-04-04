package fr.cea.ig.mongo;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Those are methods that have been deprecated. The implementation
 * should be faithful to the original implementation.
 * 
 * @author vrd
 *
 */
public class MongoDeprecation {

	public static long queryForLong(JdbcTemplate t, String sql, Object... args) {
		Long l = t.queryForObject(sql, Long.class, args);
		if (l == null)
			return 0;
		return l.longValue();
	}
	
	public static int queryForInt(JdbcTemplate t, String sql, Object... args) {
		Integer l = t.queryForObject(sql, Integer.class, args);
		if (l == null)
			return 0;
		return l.intValue();
	}

}
