package com.shdatalink.sip.server.common.constants;

public class RedisKeyConstants {
    public static final String USER_LOGIN_INFO = "sip:user_login_info:";
    public static final String ALARM_NOTIFY = "sip:alarm_notify:";
    public static final String PTZ_CONTROL = "sip:ptzControl:";
    public static String ptzControl(String deviceId, String channelId) {
        return PTZ_CONTROL + deviceId + "_" + channelId;
    }

    public static String ptzControl(String deviceId, String channelId, String serialNo) {
        return PTZ_CONTROL + deviceId + "_" + channelId + "_" + serialNo;
    }

    public static final String NO_VIEWER_PUSH_STREAM = "sip:no_viewer_push_stream:";
    public static final String PUSH_STREAM_VIEWER = "sip:push_stream_viewer:";

}
