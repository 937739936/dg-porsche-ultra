package com.shdatalink.sip.server.gb28181.core.bean.constants;


import com.shdatalink.sip.server.utils.SipUtil;

import javax.sip.ListeningPoint;
import javax.sip.header.ContentTypeHeader;

/**
 * 国标协议常量
 */
public class SipConstant {
    public static final ContentTypeHeader XML = SipUtil.createContentTypeHeader("APPLICATION", "MANSCDP+xml");
    public static final ContentTypeHeader SDP = SipUtil.createContentTypeHeader("APPLICATION", "SDP");
    public static final ContentTypeHeader RTSP = SipUtil.createContentTypeHeader("APPLICATION", "MANSRTSP");

    /**
     * 时区
     */
    public static final String TIME_ZONE = "Asia/Shanghai";

    /**
     * 字符集
     */
    public static final String CHARSET = "GB2312";


    /**
     * 地图坐标系统
     */
    public static final String GEO_COORD_SYS = "WGS84";


    /**
     * datetime格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


    public static final String DATETIME_FORMAT_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String REGISTER_DIGEST_MD5_ALGORITHM = "MD5";

    public static final String SNAPSHOT_TOKEN = "SNAPSHOT";
    /**
     * 传输协议
     *
     */
    public static class TransPort {

        /**
         * UDP
         */
        public static final String UDP = ListeningPoint.UDP;


        /**
         * TCP
         */
        public static final String TCP = ListeningPoint.TCP;

    }
}
