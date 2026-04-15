package com.quodex._miles.util;

import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JooqUtil {

    private final DSLContext dsl;

    /**
     * Fetch all records
     */
    public Result<Record> fetchAll(ResultQuery<? extends Record> query) {
        return (Result<Record>) query.fetch();
    }

    /**
     * Fetch one record safely
     */
    public Optional<Record> fetchOne(ResultQuery<? extends Record> query) {
        return (Optional<Record>) query.fetchOptional();
    }

    /**
     * Efficient count query
     */
    public long count(Select<? extends Record1<Long>> query) {
        return query.fetchOne(0, Long.class);
    }

    /**
     * Get DSLContext for custom queries
     */
    public DSLContext getDsl() {
        return dsl;
    }
}