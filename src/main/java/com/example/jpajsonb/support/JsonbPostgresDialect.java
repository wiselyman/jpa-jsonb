package com.example.jpajsonb.support;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

/**
 * Created by wangyunfei on 2017/6/26.
 */
public class JsonbPostgresDialect extends PostgreSQL94Dialect {

    public JsonbPostgresDialect() {
        this.registerColumnType(Types.JAVA_OBJECT,"jsonb");
    }
}
