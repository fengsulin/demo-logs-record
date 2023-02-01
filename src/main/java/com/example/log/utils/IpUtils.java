package com.example.log.utils;


import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 获取客户端所在地工具类
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }

        // 本机访问
        if("localhost".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)){
            // 根据网卡获取本机配置的IP
            InetAddress inet;
            try {
                inet = InetAddress.getLocalHost();
                ip = inet.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真是IP，多个IP按照‘,’分割
        if(null != ip && ip.length() > 15){
            if(ip.indexOf(",") > 15){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获取mac 地址
     * @return
     * @throws UnknownHostException
     * @throws SocketException
     */
    public static String getMacAddress() throws UnknownHostException, SocketException {
        byte[] address = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();

        // 把mac地址拼装成String
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i< address.length;i++){
            if(i != 0){
                sb.append("-");
            }
            // mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(address[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        return sb.toString().trim().toUpperCase();
    }

    /**
     * 完全基于文件查询
     * @param dbPath：xdb文件所在路径
     * @param ip：需要查询的ip
     * @return String：返回ip所在地字符串
     */
    public static String getIpOnlyFile(String dbPath,String ip) {
        // 1、创建 searcher 对象
        Searcher searcher = null;
        String region = null;
        try {
            searcher = Searcher.newWithFileOnly(dbPath);
            // 查询
            region = searcher.search(ip);
        } catch (IOException e) {

            logger.error("failed to create searcher with `%s`: %s\n", dbPath, e);
        } catch (Exception e) {
            logger.info("failed to search(%s): %s\n", ip, e);
        }
        // 3、关闭资源
        try {
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return region;
        // 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。
    }

    /**
     * 缓存 VectorIndex 索引
     * @param dbPath：xdb文件所在路径
     * @param ip：需要查询的ip
     * @return String：返回ip所在地字符串
     */
    public static String getIpWithCache(String dbPath,String ip){
        // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
        byte[] vIndex = new byte[0];
        // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
        Searcher searcher = null;
        String region = null;
        try {
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
            searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
            region = searcher.search(ip);
        } catch (Exception e) {
            logger.error("failed to load vector index from `%s`: %s\n", dbPath, e);
        }

        // 4、关闭资源
        if(searcher != null){
            try {
                searcher.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return region;
        // 备注：每个线程需要单独创建一个独立的 Searcher 对象，但是都共享全局的制度 vIndex 缓存。
    }

    /**
     * 缓存整个sdb数据
     * @param dbPath：xdb文件所在路径
     * @param ip：需要查询的ip
     * @return String：返回ip所在地字符串
     */
    public static String getIpWithAllCache(String dbPath,String ip){
        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;
        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher;
        String region = null;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
            searcher = Searcher.newWithBuffer(cBuff);
            region = searcher.search(ip);
        } catch (Exception e) {
            logger.error("failed to load content from `%s`: %s\n", dbPath, e);
        }
        return region;
        // 4、关闭资源 - 该 searcher 对象可以安全用于并发，等整个服务关闭的时候再关闭 searcher
        // searcher.close();
        // 备注：并发使用，用整个 xdb 数据缓存创建的查询对象可以安全的用于并发，也就是你可以把这个 searcher 对象做成全局对象去跨线程访问。
    }

    /**
     * 获取浏览器名称
     * @param request
     * @return
     */
    public static String getBrowser(HttpServletRequest request){
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }
}
