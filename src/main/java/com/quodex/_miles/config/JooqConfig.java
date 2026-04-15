package com.quodex._miles.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * jOOQ Configuration
 *
 * This configuration provides a DSLContext bean that can be injected
 * into services to perform type-safe database queries using the jOOQ library.
 *
 * Usage in service classes:
 * @Autowired
 * private DSLContext dsl;
 *
 * Example:
 * var result = dsl.select(USERS.asterisk())
 *     .from(USERS)
 *     .where(USERS.ID.eq(1L))
 *     .fetchOne();
 */
@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }
}

