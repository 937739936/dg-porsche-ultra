package com.shdatalink.sip.server.utils;

import com.shdatalink.framework.common.utils.QuarkusUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FFmpegUtil {
    private static final Map<Long, Process> processMap = new ConcurrentHashMap<>();

    public static void run(int timeoutSec, String ...cmd) throws IOException, InterruptedException {
        List<String> ffCmd = new ArrayList<>() {{ add("ffmpeg");}};
        ffCmd.addAll(Stream.of(cmd).toList());
        System.out.println(String.join(" ", ffCmd));
        ProcessBuilder pb = new ProcessBuilder(ffCmd);
        if (QuarkusUtil.isDev()) {
            pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT);
        } else {
            pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD);
        }
        Process process = pb.start();
        process.waitFor(timeoutSec, TimeUnit.SECONDS);
    }

    public static void pipe(int timeoutSec, Consumer<byte[]> consumer, String... cmd)
            throws IOException, InterruptedException {
        List<String> ffCmd = new ArrayList<>();
        ffCmd.add("ffmpeg");
        ffCmd.addAll(List.of(cmd));
        ffCmd.add("pipe:1");

        String processCmd = String.join(" ", ffCmd);
        System.out.println("[FFmpeg CMD] " + processCmd);

        ProcessBuilder pb = new ProcessBuilder(ffCmd);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = pb.start();

        try (InputStream inputStream = process.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                consumer.accept(Arrays.copyOf(buffer, bytesRead));
            }

            process.waitFor(timeoutSec, TimeUnit.SECONDS);
        } finally {
            process.destroy();
        }
    }

    public static String probeCodec(String url)
            throws IOException, InterruptedException {
        String[] cmd = new String[]{
                "ffprobe",
                "-v", "error",
                "-select_streams", "v",
                "-show_entries", "stream=codec_name",
                "-of", "csv=p=0",
                url
        };

        String processCmd = String.join(" ", cmd);
        System.out.println("[FFmpeg CMD] " + processCmd);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // 输出格式通常是 codec_name，例如: "h264"
                    return line;
                }
            }

            process.waitFor(30, TimeUnit.SECONDS);
        } finally {
            process.destroy();
        }
        return null;
    }

//    public static void stop(Long pid) {
//        if (processMap.containsKey(pid)) {
//            Process process = processMap.get(pid);
//            process.destroy();
//            processMap.remove(pid);
//        }
//    }
}
