package cn.healthcaredaas.datasphere.core.constant;

/**
 * RDBMS 常量定义
 *
 * @author chenpan
 */
public class RdbmsConstant {

    public static final int TIMEOUT_SECONDS = 15;

    public static final int QUERY_TIMEOUT_INSECOND = 172800;

    public static final String ALL_COLUMN = "*";

    public static final String JDBC_PARAM_PLACEHOLDER = "?";

    public static final String SQL_KEY_WHERE = "where";

    public static class Properties {
        public static final String DRIVER = "driver";
        public static final String JDBC_URL = "jdbcUrl";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String POOLED = "usePool";
    }

    public static class Meta {
        public static final String KEY_COLUMN_LABEL = "label";
        public static final String KEY_COLUMN_NAME = "name";
        public static final String KEY_COLUMN_TYPE = "type";
        public static final String KEY_COLUMN_TYPE_NAME = "typeName";
        public static final String KEY_COLUMN_AUTO_INCREMENT = "autoIncrement";
    }

    public static class SqlDataType {
        public static final String YEAR = "year";
    }
}
