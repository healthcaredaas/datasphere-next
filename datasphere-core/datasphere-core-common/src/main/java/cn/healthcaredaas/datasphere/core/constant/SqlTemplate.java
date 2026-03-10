package cn.healthcaredaas.datasphere.core.constant;

/**
 * SQL模板常量
 *
 * @author chenpan
 */
public class SqlTemplate {

    public static final String queryColumn = "SELECT %s FROM %s LIMIT 0";

    public static final String queryColumnFromSql = "SELECT * FROM (%s) t LIMIT 0";
}
