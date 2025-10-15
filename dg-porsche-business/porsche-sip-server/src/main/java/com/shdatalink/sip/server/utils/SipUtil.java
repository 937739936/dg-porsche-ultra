package com.shdatalink.sip.server.utils;

import com.shdatalink.framework.common.utils.QuarkusUtil;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.Dto.RemoteInfo;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Subject;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@UtilityClass
public class SipUtil {

    private final static Logger logger = LoggerFactory.getLogger(SipUtil.class);

    public static UserAgentHeader userAgentHeader = SipUtil.createUserAgentHeader();


    @Setter
    public static String DEFAULT_CHARSET = SipConstant.CHARSET;

    public static String generateSn() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    public static String generateCallId(String localAddress) {
        return nanoId(32) + "@" + localAddress;
    }

    public static String generateViaTag() {
        return "z9hG4bK" + nanoId(10);
    }

    public String getTransactionKey(String callId, int cseq, String method) {
        return callId + ";" + cseq + ";" + method;
    }

    public static String nanoId(int size) {
        return RandomStringUtils.random(size, true, true);
    }


    private static SipFactory getSipFactory() {
        return SipFactory.getInstance();
    }


    @SneakyThrows
    public static MessageFactory getMessageFactory() {
        MessageFactory messageFactory = getMessageFactory(DEFAULT_CHARSET);
        return messageFactory;
    }

    @SneakyThrows
    public static MessageFactory getMessageFactory(String charset) {
        MessageFactoryImpl messageFactory = (MessageFactoryImpl) getSipFactory().createMessageFactory();
        messageFactory.setDefaultContentEncodingCharset(charset);
        messageFactory.setDefaultUserAgentHeader(userAgentHeader);
        return messageFactory;
    }


    @SneakyThrows
    public static AddressFactory getAddressFactory() {
        return getSipFactory().createAddressFactory();
    }

    @SneakyThrows
    public static HeaderFactory getHeaderFactory() {
        return getSipFactory().createHeaderFactory();
    }

    @SneakyThrows
    public static SipURI createSipURI(String id, String address) {
        return getAddressFactory().createSipURI(id, address);
    }

    @SneakyThrows
    public static Address createAddress(SipURI uri) {
        return getAddressFactory().createAddress(uri);
    }

    @SneakyThrows
    public static ToHeader createToHeader(Address toAddress, String toTag) {
        return getHeaderFactory().createToHeader(toAddress, toTag);
    }

    @SneakyThrows
    public static ToHeader createToHeader(Address toAddress) {
        return createToHeader(toAddress, null);
    }

    @SneakyThrows
    public static FromHeader createFromHeader(Address fromAddress, String fromTag) {
        return getHeaderFactory().createFromHeader(fromAddress, fromTag);
    }

    @SneakyThrows
    public static CallIdHeader createCallIdHeader(String callId) {
        return getHeaderFactory().createCallIdHeader(callId);
    }

    @SneakyThrows
    public static CSeqHeader createCSeqHeader(long cSeq, String method) {
        return getHeaderFactory().createCSeqHeader(cSeq, method);
    }

    @SneakyThrows
    public static ViaHeader createViaHeader(String ip, int port, String transport, String viaTag) {
        ViaHeader viaHeader = getHeaderFactory().createViaHeader(ip, port, transport, viaTag);
        viaHeader.setRPort();
        return viaHeader;
    }

    @SneakyThrows
    public static List<ViaHeader> createViaHeaders(String ip, int port, String transport, String viaTag) {
        return Collections.singletonList(createViaHeader(ip, port, transport, viaTag));
    }

    @SneakyThrows
    public static MaxForwardsHeader createMaxForwardsHeader(int maxForwards) {
        return getHeaderFactory().createMaxForwardsHeader(maxForwards);
    }

    @SneakyThrows
    public static ContentTypeHeader createContentTypeHeader(String contentType, String subType) {
        return getHeaderFactory().createContentTypeHeader(contentType, subType);
    }

    public static String createHostAddress(String ip, int port) {
        return StringUtils.joinWith(":", ip, port);
    }

    @SneakyThrows
    public static UserAgentHeader createUserAgentHeader() {
        List<String> agentParam = new ArrayList<>();
        agentParam.add("shdatalink");
        return SipFactory.getInstance().createHeaderFactory().createUserAgentHeader(agentParam);
    }


    @SneakyThrows
    public static ToHeader getToHeader(Message message) {
        return (ToHeader) message.getHeader(ToHeader.NAME);
    }

    @SneakyThrows
    public static FromHeader getFromHeader(Message message) {
        return (FromHeader) message.getHeader(FromHeader.NAME);
    }

    public static String getUserIdFromToHeader(Response response) {
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }

    public static String getUserIdFromToHeader(Request request) {
        ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }

    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdFromFromHeader(Response response) {
        FromHeader fromHeader = (FromHeader) response.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdFromHeader(HeaderAddress headerAddress) {
        AddressImpl address = (AddressImpl) headerAddress.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }


    @SneakyThrows
    public static Address getAddressFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        return fromHeader.getAddress();
    }


    public static SipProvider getSipProvider(String transport) {
        return StringUtils.equalsIgnoreCase(transport, ListeningPoint.TCP) ? QuarkusUtil.getBean("tcpSipProvider", SipProvider.class) : QuarkusUtil.getBean("udpSipProvider", SipProvider.class);
    }


    public static CallIdHeader getNewCallIdHeader(String transport) {
        return getSipProvider(transport).getNewCallId();
    }

    public static String getNewCallId(String transport) {
        return getNewCallIdHeader(transport).getCallId();
    }


    public static long getMessageSeq() {
        long timestamp = System.currentTimeMillis();
        return (timestamp & 0x3FFF) % Integer.MAX_VALUE;
    }


    public static String getChannelIdFromRequest(Request request) {
        Header subject = request.getHeader("subject");
        if (subject == null) {
            return null;
        }
        return ((Subject) subject).getSubject().split(":")[0];
    }

    public static String getNewFromTag() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getNewTag() {
        return String.valueOf(System.currentTimeMillis());
    }


    public static String genSubscribeKey(String prefix, String... ids) {
        final String SEPARATOR = ":";
        return StringUtils.joinWith(SEPARATOR, (Object[]) ArrayUtils.addFirst(ids, prefix));
    }


    /**
     * 云台指令码计算
     *
     * @param leftRight 镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown    镜头上移下移 0:停止 1:上移 2:下移
     * @param inOut     镜头放大缩小 0:停止 1:缩小 2:放大
     * @param moveSpeed 镜头移动速度 默认 0XFF (0-255)
     * @param zoomSpeed 镜头缩放速度 默认 0X1 (0-255)
     */
    public static String cmdString(int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed) {
        int cmdCode = 0;
        if (leftRight == 2) {
            cmdCode |= 0x01;        // 右移
        } else if (leftRight == 1) {
            cmdCode |= 0x02;        // 左移
        }
        if (upDown == 2) {
            cmdCode |= 0x04;        // 下移
        } else if (upDown == 1) {
            cmdCode |= 0x08;        // 上移
        }
        if (inOut == 2) {
            cmdCode |= 0x10;    // 放大
        } else if (inOut == 1) {
            cmdCode |= 0x20;    // 缩小
        }
        StringBuilder builder = new StringBuilder("A50F01");
        String strTmp;
        strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", moveSpeed);
        builder.append(strTmp, 0, 2);
        builder.append(strTmp, 0, 2);

        //优化zoom低倍速下的变倍速率
        if ((zoomSpeed > 0) && (zoomSpeed < 16)) {
            zoomSpeed = 16;
        }
        strTmp = String.format("%X", zoomSpeed);
        builder.append(strTmp, 0, 1).append("0");
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + moveSpeed + moveSpeed + (zoomSpeed /*<< 4*/ & 0XF0)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }

    /**
     * 从请求中获取设备ip地址和端口号
     */
    public static RemoteInfo getRemoteInfoFromRequest(SIPRequest request) {
        String remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
        String transport = request.getTopmostViaHeader().getTransport();
        int remotePort = request.getPeerPacketSourcePort();
        String deviceId = SipUtil.getUserIdFromFromHeader(request);
        return new RemoteInfo(remoteAddress, remotePort, transport, deviceId);
    }


    public static String parseTime(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            try {
                localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).withZone(ZoneId.of("Asia/Shanghai")));
            } catch (DateTimeParseException e2) {
                logger.error("[格式化时间] 无法格式化时间： {}", timeStr);
                return null;
            }
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).withZone(ZoneId.of("Asia/Shanghai")));
    }


    public boolean isTCP(Request request) {
        return isTCP((ViaHeader) request.getHeader(ViaHeader.NAME));
    }

    public boolean isTCP(Response response) {
        return isTCP((ViaHeader) response.getHeader(ViaHeader.NAME));
    }

    private boolean isTCP(ViaHeader viaHeader) {
        String protocol = viaHeader.getProtocol();
        return protocol.equals("TCP");
    }
}
