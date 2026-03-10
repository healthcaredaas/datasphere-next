package cn.healthcaredaas.datasphere.core.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 数据库类型枚举
 *
 * @author chenpan
 */
public enum DbType {

    MySql("mysql", "com.mysql.cj.jdbc.Driver"),
    Oracle("oracle", "oracle.jdbc.OracleDriver"),
    SQLServer("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    PostgreSQL("postgresql", "org.postgresql.Driver"),
    KingbaseES("kingbasees", "com.kingbase8.Driver"),
    GBase("gbase", "com.gbase.jdbc.Driver"),
    OpenGauss("openGauss", "org.opengauss.Driver"),
    StarRocks("starRocks", "com.mysql.cj.jdbc.Driver"),
    DM("dm", "com.dameng.DmDriver");

    @Getter
    private final String type;

    @Getter
    private final String driverClass;

    DbType(String type, String driverClass) {
        this.type = type;
        this.driverClass = driverClass;
    }

    /**
     * 是否为指定数据库方言
     *
     * @param dialectName 方言名称
     * @return 是否匹配
     */
    public boolean match(String dialectName) {
        return StrUtil.equalsIgnoreCase(dialectName, name());
    }

    /**
     * 根据类型获取数据库枚举
     *
     * @param type 数据库类型
     * @return 数据库枚举
     */
    public static DbType getByType(String type) {
        for (DbType dbType : values()) {
            if (dbType.getType().equalsIgnoreCase(type)) {
                return dbType;
            }
        }
        return null;
    }
}
