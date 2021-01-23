package tech.jhipster.sample.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import tech.jhipster.sample.domain.Blog;
import tech.jhipster.sample.service.ColumnConverter;

/**
 * Converter between {@link Row} to {@link Blog}, with proper type conversions.
 */
@Service
public class BlogRowMapper implements BiFunction<Row, String, Blog> {

    private final ColumnConverter converter;

    public BlogRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Blog} stored in the database.
     */
    @Override
    public Blog apply(Row row, String prefix) {
        Blog entity = new Blog();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
