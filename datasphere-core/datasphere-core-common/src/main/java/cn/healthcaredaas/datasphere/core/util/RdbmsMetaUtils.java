package cn.healthcaredaas.datasphere.core.util;

import cn.healthcaredaas.datasphere.core.constant.SqlTemplate;
import cn.healthcaredaas.datasphere.core.dto.ColumnMeta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RDBMS 元数据工具类
 *
 * @author chenpan
 */
public class RdbmsMetaUtils {

    public static List<String> getTables(Connection conn) {
        final List<String> tables = new ArrayList<>();
        ResultSet rs = null;
        try {

            String catalog = RdbmsUtils.getCataLog(conn);
            String schema = RdbmsUtils.getSchema(conn);

            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(catalog, schema, tableNamePattern(), types());
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            RdbmsUtils.closeDBResources(rs, null, conn);
        }
        return tables;
    }


    /**
     * 获取表元数据
     *
     * @param conn      数据库连接
     * @param tableName 表名
     * @param column    列名
     * @return 列元数据列表
     */
    public static List<ColumnMeta> getColumnMetaData(Connection conn, String tableName, String column) {
        String querySql = String.format(SqlTemplate.queryColumn, column, tableName);
        return getColumnMetaData(conn, querySql);
    }

    /**
     * 获取SQL元数据
     *
     * @param conn 数据库连接
     * @param sql  SQL语句
     * @return 列元数据列表
     */
    public static List<ColumnMeta> getColumnMetaData(Connection conn, String sql) {
        Statement statement = null;
        ResultSet rs = null;

        List<ColumnMeta> columnMetaData = new ArrayList<>();
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(queryColumn(sql));
            ResultSetMetaData rsMetaData = rs.getMetaData();
            ColumnMeta meta;
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
                meta = new ColumnMeta();
                meta.setLabel(rsMetaData.getColumnLabel(i + 1));
                meta.setName(rsMetaData.getColumnName(i + 1));
                meta.setType(rsMetaData.getColumnType(i + 1));
                meta.setTypeName(rsMetaData.getColumnTypeName(i + 1));
                meta.setAutoIncrement(rsMetaData.isAutoIncrement(i + 1));
                columnMetaData.add(meta);
            }
            return columnMetaData;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            RdbmsUtils.closeDBResources(rs, statement, conn);
        }
    }

    protected static String queryColumn(String sql) {
        return String.format(SqlTemplate.queryColumnFromSql, sql);
    }


    protected static String tableNamePattern() {
        return "%";
    }

    protected static String[] types() {
        return new String[]{"TABLE", "VIEW"};
    }
}
