package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.shdatalink.excel.utils.ExcelUtil;
//import com.shdatalink.excel.utils.ExcelUtil;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.excel.utils.ExcelUtil;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.SipMessageTemplate;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.*;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.Dto.RemoteInfo;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control.PtzCmd;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceInfo;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.req.CloseStreamsReq;
import com.shdatalink.sip.server.module.device.convert.DeviceConvert;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.entity.DeviceLog;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.vo.*;
import com.shdatalink.sip.server.utils.SipUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

    @Inject
    SipMessageTemplate sipMessageTemplate;
    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    Validator validator;
    @Inject
    MediaService mediaService;
    @Inject
    RedisUtil redisUtil;
    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    DeviceSnapService deviceSnapService;
    @Inject
    DeviceLogService deviceLogService;
    @Inject
    DeviceMapper deviceMapper;
    @Inject
    DeviceConvert deviceConvert;
    @Inject
    Event<DeviceOnlineEvent> deviceOnlineEventEvent;
    @VirtualThreads
    ExecutorService executor;

    public Optional<Device> getByDeviceId(String deviceId) {
        return getOneOpt(Wrappers.<Device>lambdaQuery().eq(Device::getDeviceId, deviceId));
    }

    public Optional<Device> getEnabledByDeviceId(String deviceId) {
        return getOneOpt(Wrappers.<Device>lambdaQuery().eq(Device::getDeviceId, deviceId).eq(Device::getEnable, true));
    }


    @Transactional(rollbackOn = Exception.class)
    public boolean saveDevice(Device device, RemoteInfo remoteInfo) {
        GbDevice gbDevice = Device.toGbDevice(device.getDeviceId(), remoteInfo);

        DeviceInfo deviceInfo = sipMessageTemplate.getDeviceInfo(gbDevice);

        device.setIp(remoteInfo.getIp());
        device.setPort(remoteInfo.getPort());
        device.setTransport(remoteInfo.getTransport());
        device.setModel(deviceInfo.getModel());
        device.setManufacturer(deviceInfo.getManufacturer());
        device.setFirmware(deviceInfo.getFirmware());
        if (device.getRegisterTime() == null) {
            device.setRegisterTime(LocalDateTime.now());
        }
        device.setKeepaliveTime(LocalDateTime.now());
        device.setOnline(true);
        // 保存设备信息
        return updateById(device);
    }

    public IPage<DevicePage> getPage(DevicePageParam param) {
        return baseMapper.getPage(new Page<>(param.getPage(), param.getPageSize()), param)
                .convert(item -> {
                    DevicePage page = deviceConvert.toDevicePage(item);
                    page.setTransport(TransportTypeEnum.parse(item.getTransport()));
                    if (item.getProtocolType() == ProtocolTypeEnum.GB28181 && item.getRegisterTime() != null) {
                        page.setIpaddr(item.getTransport()+"://"+item.getIp()+":"+item.getPort());
                    }
                    if(item.getProtocolType() == ProtocolTypeEnum.RTMP){
                        deviceChannelMapper.selectByDeviceId(item.getDeviceId()).forEach(channel -> {
                            String streamId = StreamFactory.streamId(InviteTypeEnum.Rtmp, channel.getId().toString());
                            page.setStreamUrl(mediaService.rtmpUrl(streamId));
                        });
                    }
                    return page;
                });
    }


    public List<PreviewDeviceList> previewDeviceList() {
        return baseMapper.selectPreviewList()
                .stream()
                .map((item) -> {
                    PreviewDeviceList vo = new PreviewDeviceList();
                    vo.setName(item.getName());
                    vo.setDeviceId(item.getDeviceId());
                    vo.setOnline(item.getOnline());
                    return vo;
                }).toList();
    }

    public List<PreviewDeviceChannel> previewChannels(String deviceId) {
        return deviceChannelMapper.selectPreviewByDeviceId(deviceId)
                .stream()
                .map(c -> {
                    PreviewDeviceChannel channel = new PreviewDeviceChannel();
                    channel.setDeviceId(c.getDeviceId());
                    channel.setChannelId(c.getChannelId());
                    channel.setName(c.getName());
                    channel.setOnline(c.getOnline());
                    channel.setPtz(PtzTypeEnum.PTZCamera.equals(c.getPtzType()));
                    Device device = deviceMapper.selectByChannelId(c.getChannelId());
                    if(device != null){
                        channel.setPlayUrl(mediaService.getPlayUrl(device, c));
                    }
                    return channel;
                }).toList();
    }

    public void updateOnline(String deviceId, boolean online) {
        update(new LambdaUpdateWrapper<Device>()
                .set(Device::getOnline,online)
                .eq(Device::getDeviceId, deviceId)
        );
    }

    @Transactional
    public boolean switchEnable(String deviceId, Boolean enable) {
        Device device = getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        device.setEnable(enable);
        if (!enable) {
            device.setOnline(false);
            deviceChannelMapper.setDeviceOffline(device.getDeviceId());
        }
        return updateById(device);
    }

    @Transactional
    public boolean delete(String deviceId) {
        Device device = baseMapper.selectByDeviceId(deviceId);
        if(device.getProtocolType() == ProtocolTypeEnum.RTMP){
            deviceChannelMapper.selectByDeviceId(deviceId).forEach(channel -> {
                String streamId = StreamFactory.streamId(InviteTypeEnum.Rtmp, channel.getId().toString());
                mediaHttpClient.closeStreams(new CloseStreamsReq(streamId, 1));
            });
        }
        baseMapper.delete(new LambdaQueryWrapper<Device>()
                .eq(Device::getDeviceId, deviceId));
        deviceChannelMapper.delete(new LambdaQueryWrapper<DeviceChannel>()
                .eq(DeviceChannel::getDeviceId, deviceId));
        deviceLogService.remove(new LambdaQueryWrapper<DeviceLog>().eq(DeviceLog::getDeviceId, deviceId));
        return true;
    }

    @Transactional
    public boolean update(DeviceUpdateParam param) {
        Device device = getByDeviceId(param.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        device.setName(param.getName());
        device.setManufacturer(param.getManufacturer() == null ? DeviceManufacturerEnum.NONE.getCode() : param.getManufacturer().getCode());
        device.setStreamUrl(param.getStreamUrl());
        device.setRemark(param.getRemark());
        device.setTransport(param.getTransport() == null ? null : param.getTransport().name());
        device.setEnableAudio(param.getEnableAudio());
        if (StringUtils.isNotBlank(param.getPassword())) {
            device.setRegisterPassword(param.getPassword());
        }
        device.setRemark(param.getRemark());
        baseMapper.updateById(device);
        if(device.getProtocolType() == ProtocolTypeEnum.PULL){
            List<DeviceChannel> deviceChannels = deviceChannelMapper.selectByDeviceId(param.getDeviceId());
            if (CollectionUtils.isEmpty(deviceChannels)) {
                throw new BizException("设备不存在或被禁用");
            }
            DeviceChannel channel = deviceChannels.get(0);
            updateDeviceStatus(device, channel, device.getStreamUrl(), device.getEnableAudio());
        }
        return true;
    }

    public boolean ptzControl(PtzControlParam param) {
        DeviceChannel channel = deviceChannelMapper.selectByDeviceIdAndChannelId(param.getDeviceId(), param.getChannelId());
        if (channel == null) {
            throw new BizException("设备不存在或被禁用");
        }
        if (channel.getPtzType() != PtzTypeEnum.PTZCamera) {
            return false;
        }
        String redisKey = "ptzControl:" + channel.getDeviceId() + "_" + channel.getChannelId();
        if (redisUtil.exists(redisKey)) {
            return false;
        }

        redisUtil.setEx(redisKey, param.getAction().getAction(), Duration.ofMillis(1500));

        executor.execute(() -> {
            Device device = getByDeviceId(param.getDeviceId()).orElseThrow(() -> new BizException("设备不存在或被禁用！"));
            PtzCmd cmd = PtzCmd.builder()
                    .ptzCmd(PtzCmd.PtzControl.getPTZCmd(param.getAction().getAction(), param.getSpeed(), null))
                    .deviceId(param.getChannelId())
                    .sn(SipUtil.generateSn())
                    .build();
            GBRequest.message(device.toGbDevice())
                    .newSession()
                    .execute(cmd);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PtzCmd stop = PtzCmd.builder()
                    .ptzCmd(PtzCmd.PtzControl.getPTZCmd(PtzCmd.PtzControl.PTZType.STOP, 5, null))
                    .deviceId(param.getChannelId())
                    .sn(SipUtil.generateSn())
                    .build();
            GBRequest.message(device.toGbDevice())
                    .newSession()
                    .execute(stop);
            redisUtil.del(redisKey);
        });
        return true;
    }

    public boolean ptzControlStart(PtzControlParam param) {
        if (StringUtils.isBlank(param.getSerialNo())) {
            throw new BizException("操作序号必须");
        }
        DeviceChannel channel = deviceChannelMapper.selectByDeviceIdAndChannelId(param.getDeviceId(), param.getChannelId());
        if (channel == null) {
            throw new BizException("设备不存在或被禁用");
        }
        if (channel.getPtzType() != PtzTypeEnum.PTZCamera) {
            return false;
        }
        String redisKey = RedisKeyConstants.ptzControl(param.getDeviceId(), param.getChannelId());
        if (redisUtil.exists(redisKey)) {
            return false;
        }

        redisUtil.setEx(redisKey, param.getAction().getAction(), Duration.ofMillis(10000));

        // 操作序列锁，防止被别人停止掉
        String redisOptKey = RedisKeyConstants.ptzControl(param.getDeviceId(), param.getChannelId(), param.getSerialNo());
        redisUtil.setEx(redisOptKey, param.getAction().getAction(),Duration.ofMillis(10000));

        executor.execute(() -> {
            Device device = getByDeviceId(param.getDeviceId()).orElseThrow(() -> new BizException("设备不存在或被禁用！"));
            PtzCmd cmd = PtzCmd.builder()
                    .ptzCmd(PtzCmd.PtzControl.getPTZCmd(param.getAction().getAction(), param.getSpeed(), null))
                    .deviceId(param.getChannelId())
                    .sn(SipUtil.generateSn())
                    .build();
            GBRequest.message(device.toGbDevice())
                    .newSession()
                    .execute(cmd);
        });
        return true;
    }

    public boolean ptzControlStop(PtzControlStopParam param) {
        if (StringUtils.isBlank(param.getSerialNo())) {
            throw new BizException("操作序号必须");
        }
        String redisOptKey = RedisKeyConstants.ptzControl(param.getDeviceId(), param.getChannelId(), param.getSerialNo());
        if (!redisUtil.exists(redisOptKey)) {
            return false;
        }
        executor.execute(() -> {
            Device device = getByDeviceId(param.getDeviceId()).orElseThrow(() -> new BizException("设备不存在或被禁用！"));
            PtzCmd cmd = PtzCmd.builder()
                    .ptzCmd(PtzCmd.PtzControl.getPTZCmd(PtzCmd.PtzControl.PTZType.STOP, 5, null))
                    .deviceId(param.getChannelId())
                    .sn(SipUtil.generateSn())
                    .build();
            GBRequest.message(device.toGbDevice())
                    .newSession()
                    .execute(cmd);
            redisUtil.del(RedisKeyConstants.ptzControl(param.getDeviceId(), param.getChannelId()));
            redisUtil.del(redisOptKey);
        });
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    public String addDeviceConfig(DeviceAddParam param) {
        int maxNumber = this.getMaxSerialNumber();
        Device device = new Device();
        device.setProtocolType(param.getProtocolType());
        device.setName(param.getName());
        device.setDeviceType(param.getDeviceType() == null ? DeviceTypeEnum.IPC : param.getDeviceType());
        DeviceManufacturerEnum deviceManufacturerEnum = param.getManufacturer() == null ? DeviceManufacturerEnum.NONE : param.getManufacturer();
        device.setManufacturer(deviceManufacturerEnum.getCode());
        device.setRegisterPassword(param.getRegisterPassword());
        if (device.getDeviceType() == DeviceTypeEnum.IPC) {
            device.setChannelCount(1);
        } else {
            device.setChannelCount(param.getChannelCount());
        }
        String deviceId = generateDeviceId(deviceManufacturerEnum, device.getDeviceType(), getSerialNumber(++maxNumber));
        device.setDeviceId(deviceId);
        device.setTransport(param.getTransport() == null ? null : param.getTransport().name());
        device.setEnableAudio(param.getEnableAudio());
        if(param.getProtocolType() == ProtocolTypeEnum.PULL){
            device.setStreamUrl(param.getStreamUrl());
            device.setStreamMode(MediaStreamModeEnum.TCP_ACTIVE);
            device.setRegisterTime(LocalDateTime.now());
        }else if(param.getProtocolType() == ProtocolTypeEnum.RTMP){
            device.setRegisterTime(LocalDateTime.now());
        }else{
            device.setStreamMode(MediaStreamModeEnum.TCP_PASSIVE);
        }
        device.setRemark(param.getRemark());
        save(device);

        if (device.getDeviceType() == DeviceTypeEnum.IPC) {
            DeviceChannel channel = new DeviceChannel();
            channel.setDeviceId(device.getDeviceId());
            channel.setChannelId(deviceId);
            channel.setName(device.getName());
            deviceChannelMapper.insert(channel);
            updateDeviceStatus(device, channel, param.getStreamUrl(), param.getEnableAudio());
        } else {
            for (int i = 0; i < device.getChannelCount(); i++) {
                DeviceChannel channel = new DeviceChannel();
                channel.setDeviceId(device.getDeviceId());
                String channelId = generateDeviceId(param.getManufacturer(), DeviceTypeEnum.IPC, getSerialNumber(++maxNumber));
                channel.setChannelId(channelId);
                channel.setName("通道-" + (i + 1));
                deviceChannelMapper.insert(channel);
            }
        }
        return deviceId;
    }

    public void updateDeviceStatus(Device device, DeviceChannel channel, String rtspUrl, Boolean enableAudio) {
        if(device.getProtocolType() == ProtocolTypeEnum.PULL){
            String streamId = StreamFactory.streamId(InviteTypeEnum.PullStream, channel.getId().toString());
            Boolean online = mediaService.addPullStream(rtspUrl, streamId, TransportTypeEnum.parse(device.getTransport()), enableAudio);
            device.setEnable(true);
            device.setOnline(online);
            if(online){
                device.setRegisterTime(LocalDateTime.now());
                device.setKeepaliveTime(LocalDateTime.now());
            }
            updateById(device);
            channel.setEnable(true);
            channel.setOnline(online);
            deviceChannelMapper.updateById(channel);
            deviceOnlineEventEvent.fireAsync(new DeviceOnlineEvent(device.getDeviceId(), online));

            executor.execute(() -> {
                if (online) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    deviceSnapService.updateDeviceSnap(device, channel);
                }
            });
        }
    }

    /**
     * 获取序号
     */
    private String getSerialNumber(int maxNumber) {
        // 查询数据库最大编号
        return String.format("%06d", maxNumber);
    }

    /**
     * 查询数据库最大编号
     * <p>
     * 并发场景下可能生成重复编号，此处使用synchronized关键字保证线程安全
     */
    private synchronized int getMaxSerialNumber() {
        // 实际实现中应更新数据库中的最大编号
        return deviceChannelMapper.getMaxSerialNumber();
    }


    /**
     * 生成设备ID
     *
     * <p>
     * ID统一编码规则:联网系统应对前端设备、监控中心设备、用户终端ID进行统一编码,该编码具有全局唯一性。
     * 编码应采用符合附录E中的E.1 规定的编码规则(20 位十进制数字字符编码)。联网系统管理平台之间的通信、管理平台与其他系统之间的通信应采用本章规定的统一编码标识联网系统的设备和用户
     * <p>
     * 编码规则:
     * 1、中心编码(8 位)、
     * 2、行业编码(2 位)、
     * 3、类型编码(3 位)、
     * 4、设备厂商(1 位)
     * 5、序号(6 位)
     *
     * @param manufacturer 生产厂商
     * @param deviceType   设备类型
     * @param serialNumber 序号
     */
    private String generateDeviceId(DeviceManufacturerEnum manufacturer, DeviceTypeEnum deviceType, String serialNumber) {
        // 1、中心编码(8 位)
        // 2、行业编码(2 位)、默认: 00-社会治安路面接人
        return   sipConfigProperties.server().domain() +
                // 3、设备类型(3 位)、默认: IPC:132   NVR:118
                deviceType.getIdentifier() +
                // 4 厂商编码(1位)
                manufacturer.getIdentifier() +
                // 5、序号(6 位)
                serialNumber;
    }

    public void updatePassword(String deviceId, String password) {
        baseMapper.update(new LambdaUpdateWrapper<Device>()
                .set(Device::getRegisterPassword, password)
                .eq(Device::getDeviceId, deviceId));
    }


    public List<String> importDevice(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        List<DeviceImportVO> rows = ExcelUtil.importExcel(fileInputStream, DeviceImportVO.class);
        if (rows.isEmpty()) {
            throw new BizException("文件为空，请填写文件内容");
        }
        List<String> failMsgList = new ArrayList<>();
        List<DeviceAddParam> addParams = new ArrayList<>();
        int rowNum = 0;
        for (DeviceImportVO row : rows) {
            rowNum++;
            List<String> messages = new ArrayList<>();
            Set<ConstraintViolation<DeviceImportVO>> validate = validator.validate(row);
            if (!validate.isEmpty()) {
                messages.addAll(validate.stream().map(ConstraintViolation::getMessage).toList());
            }
            DeviceManufacturerEnum manufacturerEnum = DeviceManufacturerEnum.fromText(row.getManufacturer());
            if (manufacturerEnum == null) {
                messages.add("生产厂家“"+row.getManufacturer()+"”尚未支持");
            }
            DeviceTypeEnum deviceTypeEnum = DeviceTypeEnum.fromText(row.getDeviceType());
            if (deviceTypeEnum == null) {
                messages.add("设备类型“"+row.getManufacturer()+"”不正确");
            }
            if (messages.isEmpty()) {
                DeviceAddParam addParam = deviceConvert.toDeviceAddParam(row);
                addParam.setManufacturer(manufacturerEnum);
                addParam.setDeviceType(deviceTypeEnum);
                addParams.add(addParam);
            } else {
                failMsgList.add("第"+rowNum+"行："+String.join("、", messages));
            }
        }
        if (!failMsgList.isEmpty()) {
            return failMsgList;
        }

        for (DeviceAddParam addParam : addParams) {
            addParam.setProtocolType(ProtocolTypeEnum.GB28181);
            addDeviceConfig(addParam);
        }
        return Collections.emptyList();
    }

    public Device getByChannelId(String channelId) {
        return baseMapper.selectByChannelId(channelId);
    }

}
