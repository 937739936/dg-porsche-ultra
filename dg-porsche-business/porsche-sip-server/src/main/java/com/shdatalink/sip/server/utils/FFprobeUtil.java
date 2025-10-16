package com.shdatalink.sip.server.utils;

import com.shdatalink.sip.server.module.plan.entity.VideoRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FFprobeUtil {
    
    public static Pair<Map<String, String>, Map<String, String>> getCodecsWithFFprobe(Path filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffprobe", 
                "-v", "quiet",
                "-show_streams",
                filePath.toAbsolutePath().toString()
            );
            
            Process process = pb.start();
            String codecStr = new String(process.getInputStream().readAllBytes());
            List<Map<String, String>> streams = new ArrayList<>();
            Map<String, String> stream = new HashMap<>();
            for (String string : codecStr.split("\n")) {
                if (string.equals("[STREAM]")) {
                    stream = new HashMap<>();
                } else if (string.equals("[/STREAM]")) {
                    streams.add(stream);
                } else {
                    stream.put(string.substring(0, string.indexOf("=")), string.substring(string.indexOf("=")+1));
                }
            }

            Map<String, String> videoCodec = streams.stream().filter(s -> s.get("codec_type").equals("video")).findFirst().orElse(null);
            Map<String, String> audioCodec = streams.stream().filter(s -> s.get("codec_type").equals("audio")).findFirst().orElse(null);

            return new ImmutablePair<>(videoCodec, audioCodec);
            
        } catch (Exception e) {
            throw new RuntimeException("FFprobe 执行失败", e);
        }
    }

    public static String buildM3u8CodecString(VideoRecord videoRecord) {
        String videoCodec = switch (videoRecord.getVideoCodec()) {
            case "h264" -> "avc1.42e01e";
            case "hevc", "h265" -> "hvc1.1.6.L93.B0";
            case "vp9" -> "vp09";
            default -> "";
        };

        if (StringUtils.isNotBlank(videoRecord.getAudioCodec())) {
            String audioCodec = switch (videoRecord.getAudioCodec()) {
                case "aac", "mp3" ->"mp4a.40.2";
                case "pcm_mulaw" -> "ulaw.40.34";
                default -> "";
            };
            videoCodec += ","+audioCodec;
        }
        return videoCodec;
    }

}