package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.control.enums.ControlCmdType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@RegisterForReflection
public class PtzCmd extends ControlMessage{
    @Builder.Default
    private ControlCmdType cmdType = ControlCmdType.DeviceControl;

    @JacksonXmlProperty(localName = "PTZCmd")
    private String ptzCmd;

    public static class PtzControl {
        // PTZ 类型
        public static class PTZType {
            public static final String STOP = "stop";
            public static final String RIGHT = "right";
            public static final String LEFT = "left";
            public static final String UP = "up";
            public static final String DOWN = "down";
            public static final String LEFT_UP = "leftUp";
            public static final String LEFT_DOWN = "leftDown";
            public static final String RIGHT_UP = "rightUp";
            public static final String RIGHT_DOWN = "rightDown";
            public static final String ZOOM_FAR = "zoomFar";
            public static final String ZOOM_NEAR = "zoomNear";
            public static final String APERTURE_FAR = "apertureFar";
            public static final String APERTURE_NEAR = "apertureNear";
            public static final String FOCUS_FAR = "focusFar";
            public static final String FOCUS_NEAR = "focusNear";
            public static final String SET_POS = "setPos";
            public static final String CAL_POS = "calPos";
            public static final String DEL_POS = "delPos";
            public static final String WIPER_OPEN = "wiperOpen";
            public static final String WIPER_CLOSE = "wiperClose";
        }

        // PTZ 命令映射
        private static final Map<String, Integer> PTZ_CMD_TYPE = new HashMap<>();
        static {
            PTZ_CMD_TYPE.put(PTZType.STOP, 0x00);
            PTZ_CMD_TYPE.put(PTZType.RIGHT, 0x01);
            PTZ_CMD_TYPE.put(PTZType.LEFT, 0x02);
            PTZ_CMD_TYPE.put(PTZType.UP, 0x08);
            PTZ_CMD_TYPE.put(PTZType.DOWN, 0x04);
            PTZ_CMD_TYPE.put(PTZType.LEFT_UP, 0x0A);
            PTZ_CMD_TYPE.put(PTZType.LEFT_DOWN, 0x06);
            PTZ_CMD_TYPE.put(PTZType.RIGHT_UP, 0x09);
            PTZ_CMD_TYPE.put(PTZType.RIGHT_DOWN, 0x05);
            PTZ_CMD_TYPE.put(PTZType.ZOOM_FAR, 0x10);
            PTZ_CMD_TYPE.put(PTZType.ZOOM_NEAR, 0x20);
            PTZ_CMD_TYPE.put(PTZType.APERTURE_FAR, 0x48);
            PTZ_CMD_TYPE.put(PTZType.APERTURE_NEAR, 0x44);
            PTZ_CMD_TYPE.put(PTZType.FOCUS_FAR, 0x42);
            PTZ_CMD_TYPE.put(PTZType.FOCUS_NEAR, 0x41);
            PTZ_CMD_TYPE.put(PTZType.SET_POS, 0x81);
            PTZ_CMD_TYPE.put(PTZType.CAL_POS, 0x82);
            PTZ_CMD_TYPE.put(PTZType.DEL_POS, 0x83);
            PTZ_CMD_TYPE.put(PTZType.WIPER_OPEN, 0x8C);
            PTZ_CMD_TYPE.put(PTZType.WIPER_CLOSE, 0x8D);
        }

        // 速度数组
        private static final int[] SPEED_ARRAY = {0x19, 0x32, 0x4B, 0x64, 0x7D, 0x96, 0xAF, 0xC8, 0xE1, 0xFA};
        private static final int[] ZOOM_ARRAY = {0x10, 0x30, 0x50, 0x70, 0x90, 0xA0, 0xB0, 0xC0, 0xD0, 0xE0};
        private static final int[] POSITION_ARRAY = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x10};

        // 获取 PTZ 命令
        public static String getPTZCmd(String type, Integer speed, Integer index) {
            int ptzSpeed = getPTZSpeed(speed);
            Integer indexValue3 = PTZ_CMD_TYPE.get(type);
            Integer indexValue4 = null;
            Integer indexValue5 = null;
            Integer indexValue6 = null;

            switch (type) {
                case PTZType.UP:
                case PTZType.DOWN:
                    indexValue5 = ptzSpeed;
                    break;
                case PTZType.APERTURE_FAR:
                case PTZType.APERTURE_NEAR:
                    indexValue5 = ptzSpeed;
                    break;
                case PTZType.RIGHT:
                case PTZType.LEFT:
                    indexValue4 = ptzSpeed;
                    break;
                case PTZType.FOCUS_FAR:
                case PTZType.FOCUS_NEAR:
                    indexValue4 = ptzSpeed;
                    break;
                case PTZType.LEFT_UP:
                case PTZType.LEFT_DOWN:
                case PTZType.RIGHT_UP:
                case PTZType.RIGHT_DOWN:
                    indexValue4 = ptzSpeed;
                    indexValue5 = ptzSpeed;
                    break;
                case PTZType.ZOOM_FAR:
                case PTZType.ZOOM_NEAR:
                    indexValue6 = getZoomSpeed(speed);
                    break;
                case PTZType.SET_POS:
                case PTZType.CAL_POS:
                case PTZType.DEL_POS:
                    indexValue5 = getPTZPositionIndex(index);
                    break;
                case PTZType.WIPER_OPEN:
                case PTZType.WIPER_CLOSE:
                    indexValue4 = 0x01;
                    break;
                default:
                    break;
            }
            return ptzCmdToString(indexValue3, indexValue4, indexValue5, indexValue6);
        }

        private static int getPTZSpeed(Integer speed) {
            int s = (speed == null ? 5 : speed);
            return SPEED_ARRAY[Math.min(Math.max(s-1, 0), SPEED_ARRAY.length-1)];
        }

        private static int getZoomSpeed(Integer speed) {
            int s = (speed == null ? 5 : speed);
            return ZOOM_ARRAY[Math.min(Math.max(s-1, 0), ZOOM_ARRAY.length-1)];
        }

        private static int getPTZPositionIndex(Integer index) {
            if (index == null || index < 1 || index > POSITION_ARRAY.length) {
                return POSITION_ARRAY[0];
            }
            return POSITION_ARRAY[index-1];
        }

        private static String ptzCmdToString(Integer indexValue3, Integer indexValue4, Integer indexValue5, Integer indexValue6) {
            byte[] cmd = new byte[8];
            cmd[0] = (byte) 0xA5;
            cmd[1] = 0x0F;
            cmd[2] = 0x01;

            if (indexValue3 != null) cmd[3] = indexValue3.byteValue();
            if (indexValue4 != null) cmd[4] = indexValue4.byteValue();
            if (indexValue5 != null) cmd[5] = indexValue5.byteValue();
            if (indexValue6 != null) cmd[6] = indexValue6.byteValue();

            int sum = 0;
            for (int i = 0; i < 7; i++) {
                sum += cmd[i] & 0xFF;
            }
            cmd[7] = (byte) (sum % 256);

            return bytesToHexString(cmd);
        }

        private static String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF).toUpperCase();
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        }
    }
}
