package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.entity.UserScreen;
import com.shdatalink.sip.server.module.device.entity.UserScreenDevice;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.UserScreenMapper;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPresetParam;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPresetVO;
import com.shdatalink.sip.server.module.device.vo.PreviewPresetListVO;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import com.shdatalink.sip.server.utils.UserInfoUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.UserScreenService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class UserScreenService extends ServiceImpl<UserScreenMapper, UserScreen> {

    @Inject
    UserScreenDeviceService userScreenDeviceService;
    @Inject
    DevicePlayService devicePlayService;
    @Inject
    DeviceChannelMapper deviceChannelMapper;

    @Transactional
    public boolean save(DevicePreviewPresetParam param) {
        UserInfo userInfo = UserInfoUtil.getUserInfoWithThrow();
        UserScreen userScreen = new UserScreen();
        userScreen.setName(param.getName());
        userScreen.setScreenCount(param.getScreenCount());
        save(userScreen);
        userScreenDeviceService.save(param.getScreenList(), userScreen.getId());

        // 最多保留20个
        Long counts = counts();
        if (counts > 20) {
            remove(new LambdaQueryWrapper<UserScreen>()
                    .orderByAsc(UserScreen::getId)
                    .eq(UserScreen::getCreatedBy, userInfo.getId())
                    .last("limit "+(counts-20)));
        }
        return true;
    }

    public Long counts() {
        UserInfo userInfo = UserInfoUtil.getUserInfoWithThrow();
        return baseMapper.selectCount(new LambdaQueryWrapper<UserScreen>()
                .eq(UserScreen::getCreatedBy, userInfo.getId()));
    }

    public List<PreviewPresetListVO> getList() {
        UserInfo userInfo = UserInfoUtil.getUserInfoWithThrow();
        List<UserScreen> userScreens = baseMapper.selectList(new LambdaQueryWrapper<UserScreen>()
                .eq(UserScreen::getCreatedBy, userInfo.getId())
                .orderByDesc(UserScreen::getId)
                .last("limit 20"));
        return userScreens.stream()
                .map(preset -> {
                    PreviewPresetListVO vo = new PreviewPresetListVO();
                    vo.setId(preset.getId());
                    vo.setName(preset.getName());
                    return vo;
                }).toList();
    }

    public DevicePreviewPresetVO getDetail(Integer id) {
        UserScreen userScreen = baseMapper.selectById(id);
        if (userScreen == null) {
            throw new BizException("预设不存在");
        }
        List<UserScreenDevice> screens = userScreenDeviceService.getByPresetId(id);
        DevicePreviewPresetVO vo = new DevicePreviewPresetVO();
        vo.setScreenCount(userScreen.getScreenCount());
//        vo.setScreenList(
//                screens.stream()
//                        .map(s -> {
//                            DeviceChannel channel = deviceChannelMapper.selectByDeviceIdAndChannelId(s.getDeviceId(), s.getChannelId());
//                            if (channel == null) return null;
//                            return devicePlayService.playUrl(s.getDeviceId(), s.getChannelId(), channel.getId().toString());
//                        }).toList()
//        );
        return vo;
    }

    @Transactional
    public boolean delete(Integer id) {
        baseMapper.deleteById(id);
        userScreenDeviceService.deleteByPresetId(id);
        return true;
    }
}
