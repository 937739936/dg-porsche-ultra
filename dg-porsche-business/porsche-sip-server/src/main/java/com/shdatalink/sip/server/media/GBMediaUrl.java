package com.shdatalink.sip.server.media;

import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.RandomUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.SNAPSHOT_TOKEN;

@ApplicationScoped
public class GBMediaUrl extends AbstractMediaUrl {
    @Override
    public ProtocolTypeEnum type() {
        return ProtocolTypeEnum.GB28181;
    }

    @Override
    public DevicePreviewPlayVO play(Integer channelPrimaryId) {
        String stream = StreamFactory.streamId(InviteTypeEnum.Play, channelPrimaryId.toString());
        String sign = mediaSignService.sign(stream);
        return build(stream, sign, "");
    }

    @Override
    public DevicePreviewPlayVO playback(Integer channelPrimaryId, LocalDateTime start) {
        String stream = StreamFactory.streamId(InviteTypeEnum.Playback, RandomUtils.insecure().randomInt(0, 99), channelPrimaryId.toString());
        String sign = mediaSignService.sign(stream);
        LocalDateTime end = start.toLocalDate().atTime(LocalTime.of(23, 59, 59));
        String startStr = start.format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
        String endStr = end.format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
        String ext = "&start=" + URLEncoder.encode(startStr, StandardCharsets.UTF_8) + "&end=" + URLEncoder.encode(endStr, StandardCharsets.UTF_8);
        return build(stream, sign, ext);
    }

    @Override
    public String snapshot(Integer channelPrimaryId) {
        String stream = StreamFactory.streamId(InviteTypeEnum.Play, channelPrimaryId.toString());
        return buildInner(stream, SNAPSHOT_TOKEN, "").getRtspUrl();
    }
}
