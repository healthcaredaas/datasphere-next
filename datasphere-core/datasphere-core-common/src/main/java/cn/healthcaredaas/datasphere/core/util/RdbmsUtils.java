package cn.healthcaredaas.datasphere.core.util;

import cn.healthcaredaas.datasphere.core.constant.RdbmsConstant;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * RDBMS 工具类
 *
 * @author chenpan
 */
public class RdbmsUtils {

    public static DataSource getDataSource(String driver, String url, String user, String pass) {
        HikariConfig configuration = new HikariConfig();
        configuration.setDriverClassName(driver);
        configuration.setJdbcUrl(url);
        configuration.setUsername(user);
        configuration.setPassword(pass);
        return new HikariDataSource(configuration);
    }

    public static synchronized Connection getConnection(String driver, JSONObject json) {
        return getConnection(driver,
                json.getString(RdbmsConstant.Properties.JDBC_URL),
                json.getString(RdbmsConstant.Properties.USERNAME),
                json.getString(RdbmsConstant.Properties.PASSWORD)
        );
    }

    public static synchronized Connection getConnection(String driver, String url, String user, String pass) {
        Properties prop = new Properties();
        prop.put("user", user);
        if (StrUtil.isNotBlank(pass)) {
            prop.put("password", pass);
        }

        return connect(driver, url, prop);
    }

    private static synchronized Connection connect(String driver, String url, Properties prop) {
        try {
            Class.forName(driver);
            DriverManager.setLoginTimeout(RdbmsConstant.TIMEOUT_SECONDS);
            return DriverManager.getConnection(url, prop);
        } catch (Exception e) {
            throw new RuntimeException(String.format("数据库连接失败. 连接信息:[%s].异常信息：[%s]", url, e.getMessage()));
        }
    }

    /**
     * 获取catalog，获取失败返回{@code null}
     *
     * @param conn {@link Connection} 数据库连接，{@code null}时返回null
     * @return catalog，获取失败返回{@code null}
     */
    public static String getCataLog(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getCatalog();
        } catch (SQLException e) {
            // ignore
        }

        return null;
    }

    /**
     * 获取schema，获取失败返回{@code null}
     *
     * @param conn {@link Connection} 数据库连接，{@code null}时返回null
     * @return schema，获取失败返回{@code null}
     */
    public static String getSchema(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getSchema();
        } catch (SQLException e) {
            // ignore
        }

        return null;
    }

    public static ResultSet query(Statement stmt, String sql)
            throws SQLException {
        return stmt.executeQuery(sql);
    }

    public static ResultSet query(Connection conn, String sql, int fetchSize) throws SQLException {
        return query(conn, sql, fetchSize, RdbmsConstant.QUERY_TIMEOUT_INSECOND);
    }

    public static ResultSet query(Connection conn, String sql, int fetchSize, int queryTimeout)
            throws SQLException {

        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(fetchSize);
        stmt.setQueryTimeout(queryTimeout);
        return query(stmt, sql);
    }

    public static Object querySimpleData(Connection conn, String sql) {
        Statement statement = null;
        Object res = null;
        try {
            statement = conn.createStatement();
            statement.executeQuery(sql);
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                res = rs.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeDBResources(statement, null);
        }
        return res;
    }

    public static void execUpdate(Connection conn, String sql) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeDBResources(statement, null);
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (null != rs) {
                Statement stmt = rs.getStatement();
                if (null != stmt) {
                    stmt.close();
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void closeDBResources(Connection conn) {
        closeDBResources(null, null, conn);
    }

    public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException unused) {
            }
        }

        if (null != stmt) {
            try {
                stmt.close();
            } catch (SQLException unused) {
            }
        }

        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException unused) {
            }
        }
    }

    public static void closeDBResources(Statement stmt, Connection conn) {
        closeDBResources(null, stmt, conn);
    }
}
