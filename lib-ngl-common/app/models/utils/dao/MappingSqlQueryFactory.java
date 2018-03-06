package models.utils.dao;

import java.util.function.BiFunction;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

public interface MappingSqlQueryFactory<T> extends BiFunction<DataSource, String, MappingSqlQuery<T>> {
}