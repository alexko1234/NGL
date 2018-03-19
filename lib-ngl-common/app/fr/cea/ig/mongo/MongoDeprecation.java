package fr.cea.ig.mongo;

import org.springframework.jdbc.core.JdbcTemplate;

public class MongoDeprecation {

	public static long queryForLong(JdbcTemplate t, String sql, Object... args) {
		Long l = t.queryForObject(sql, Long.class, args);
		return l == null ? 0 : l.longValue();
	}
	
	public static int queryForInt(JdbcTemplate t, String sql, Object... args) {
		Integer l = t.queryForObject(sql, Integer.class, args);
		return l == null ? 0 : l.intValue();
	}

}
