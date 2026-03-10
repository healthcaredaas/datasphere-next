package cn.healthcaredaas.datasphere.core.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP工具类
 *
 * @author chenpan
 */
public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 处理本地IP
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST;
        }

        return ip != null ? ip : UNKNOWN;
    }

    /**
     * 获取客户端IP地址（别名方法，用于兼容）
     */
    public static String getIpAddress(HttpServletRequest request) {
        return getClientIp(request);
    }

    /**
     * 获取本地IP地址
     */
    public static String getLocalIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return LOCALHOST;
        }
    }

    /**
     * 检查IP是否为空或unknown
     */
    private static boolean isEmpty(String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 判断是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (LOCALHOST.equals(ip)) {
            return true;
        }

        byte[] addr = textToNumericFormatV4(ip);
        if (addr == null) {
            return false;
        }

        final byte b0 = addr[0];
        final byte b1 = addr[1];

        // 10.x.x.x/8
        if (b0 == 10) {
            return true;
        }
        // 172.16.x.x/12
        if (b0 == (byte) 172 && (b1 >= 16 && b1 <= 31)) {
            return true;
        }
        // 192.168.x.x/16
        if (b0 == (byte) 192 && b1 == (byte) 168) {
            return true;
        }

        return false;
    }

    /**
     * 将IPv4地址转换为字节数组
     */
    private static byte[] textToNumericFormatV4(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        byte[] result = new byte[4];
        String[] parts = text.split("\\.", -1);

        if (parts.length != 4) {
            return null;
        }

        try {
            for (int i = 0; i < 4; i++) {
                int value = Integer.parseInt(parts[i]);
                if (value < 0 || value > 255) {
                    return null;
                }
                result[i] = (byte) value;
            }
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
