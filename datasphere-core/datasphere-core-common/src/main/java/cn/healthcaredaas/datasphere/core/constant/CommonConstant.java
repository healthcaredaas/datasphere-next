package cn.healthcaredaas.datasphere.core.constant;

/**
 * 通用常量
 *
 * @author chenpan
 */
public final class CommonConstant {

    private CommonConstant() {}

    /** 启用状态 */
    public static final int STATUS_ENABLE = 1;

    /** 禁用状态 */
    public static final int STATUS_DISABLE = 0;

    /** 删除标识-已删除 */
    public static final String DELETE_FLAG_YES = "1";

    /** 删除标识-未删除 */
    public static final String DELETE_FLAG_NO = "0";

    /** 默认页码 */
    public static final long DEFAULT_PAGE_CURRENT = 1;

    /** 默认每页大小 */
    public static final long DEFAULT_PAGE_SIZE = 10;

    /** 最大每页大小 */
    public static final long MAX_PAGE_SIZE = 1000;
}
