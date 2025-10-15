package com.shdatalink.sip.service.module.device.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PtzControlAction {
    // 向右旋转
    RIGHT("right"),
    // 向左旋转
    LEFT("left"),
    // 向上旋转
    UP("up"),
    // 向下旋转
    DOWN("down"),
    // 左上旋转
    LEFT_UP("leftUp"),
    // 左下旋转
    LEFT_DOWN("leftDown"),
    // 右上旋转
    RIGHT_UP("rightUp"),
    // 右下旋转
    RIGHT_DOWN("rightDown"),
    // 缩放-拉远
    ZOOM_FAR("zoomFar"),
    // 缩放-拉近
    ZOOM_NEAR("zoomNear"),
    // 光圈-拉远
    APERTURE_FAR("apertureFar"),
    // 光圈-拉近
    APERTURE_NEAR("apertureNear"),
    // 焦距-拉远
    FOCUS_FAR("focusFar"),
    // 焦距-拉近
    FOCUS_NEAR("focusNear"),
    ;
    @Getter
    private final String action;
}
