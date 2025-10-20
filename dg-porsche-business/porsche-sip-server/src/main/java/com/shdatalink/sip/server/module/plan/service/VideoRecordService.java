package com.shdatalink.sip.server.module.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.builder.InfoRequest;
import com.shdatalink.sip.server.media.hook.req.RecordMp4Req;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.server.module.config.service.ConfigService;
import com.shdatalink.sip.server.module.config.vo.VideoRecordConfig;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.plan.entity.VideoRecord;
import com.shdatalink.sip.server.module.plan.mapper.VideoRecordMapper;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import com.shdatalink.sip.server.utils.FFmpegUtil;
import com.shdatalink.sip.server.utils.FFprobeUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.plan.service.VideoRecordService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class VideoRecordService extends ServiceImpl<VideoRecordMapper, VideoRecord> {
    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    ConfigService configService;
    @Inject
    DeviceMapper deviceMapper;

    private final Long continuousMaxInterval = 1000L;
    // 最少保留的硬盘空间大小, 录像清理在此基础上进行
    private final long minDiskSpace = 1024 * 1024 * 500; // 500MB

    public List<VideoRecordTimeLineVO> timeline(String deviceId, String channelId, LocalDate date) {
        List<VideoRecord> list = baseMapper.getVideoRecords(deviceId, channelId, date);

        // 合并连续的时间线
        long start = 0;
        int duration = 0;
        long lastStop = 0;
        String codec = "";
        List<VideoRecordTimeLineVO> merged = new ArrayList<>();
        for (VideoRecord videoRecord : list) {
            if (start == 0) {
                start = videoRecord.getStartTime();
            }

            if (lastStop != 0 && (videoRecord.getStartTime() - lastStop > continuousMaxInterval || !Objects.equals(codec, videoRecord.getVideoCodec() + ":" + videoRecord.getResolution()))) {
                merged.add(new VideoRecordTimeLineVO(start, duration));
                start = videoRecord.getStartTime();
                duration = 0;
            }
            duration += videoRecord.getDuration();
            codec = videoRecord.getVideoCodec() + ":" + videoRecord.getResolution();
            lastStop = videoRecord.getStartTime() + videoRecord.getDuration();
        }
        if (duration > 0) {
            merged.add(new VideoRecordTimeLineVO(start, duration));
        }
        return merged;
    }

    public void clearDisk(String folder) throws IOException {
        VideoRecordConfig config = configService.getConfig(ConfigTypesEnum.VideoRecord);

        // 最后一级的目录总是流id，取出父级就是视频保存的目录了
        // 录制视频时会指定用户配置的视频目录
        Path parent = Paths.get(folder).getParent();
        FileStore fileStore = Files.getFileStore(parent);
        long totalSpace = fileStore.getTotalSpace();
        long usableSpace = fileStore.getUsableSpace();
        long usedSpace = totalSpace - usableSpace;
        BigDecimal usedPre = BigDecimal.valueOf(usedSpace).divide(BigDecimal.valueOf(totalSpace), 2, RoundingMode.HALF_UP);
        BigDecimal diskStoreMax = config.getDiskStoreMax();
        // 最大保存默认95%
        if (diskStoreMax == null) diskStoreMax = BigDecimal.valueOf(95);
        diskStoreMax = diskStoreMax.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        // 需要清理的空间大小
        long clearSpace = 0;
        if (usedPre.compareTo(diskStoreMax) >= 1) {
            clearSpace = usedSpace - BigDecimal.valueOf(totalSpace).multiply(diskStoreMax).longValue();
        }
        // 预期清理完之后磁盘空间还是不足以支持sip服务正常运转，就用最小空闲磁盘代替
        if (minDiskSpace > clearSpace + usableSpace) {
            clearSpace = minDiskSpace;
        }
        while (clearSpace > 0) {
            List<VideoRecord> videoRecords = baseMapper.selectList(new LambdaQueryWrapper<VideoRecord>().orderByAsc(VideoRecord::getStartTime).last("limit 100"));
            for (VideoRecord videoRecord : videoRecords) {
                Path path = Paths.get(videoRecord.getFolder(), videoRecord.getDate().format(DateTimeFormatter.ISO_DATE), videoRecord.getFilePath());
                int fileId = 0;
                while (true) {
                    Path tsPath = Paths.get(videoRecord.getFolder(), videoRecord.getDate().format(DateTimeFormatter.ISO_DATE), videoRecord.getFilePath().replace(".m3u8", String.format("_%03d.ts", fileId)));
                    if (!Files.exists(tsPath)) {
                        break;
                    }
                    clearSpace -= Files.size(tsPath);
                    Files.deleteIfExists(tsPath);
                    fileId++;
                }
                if (Files.exists(path)) {
                    clearSpace -= Files.size(path);
                    Files.deleteIfExists(path);
                }
                baseMapper.deleteById(videoRecord.getId());
                if (clearSpace <= 0) {
                    break;
                }
            }
        }

    }

    public void save(RecordMp4Req req) throws IOException, InterruptedException {
        clearDisk(req.getFolder());
        DeviceChannel channel = deviceChannelMapper.selectById(StreamFactory.extractChannel(req.getStream()));
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        LocalDate localDate = Instant.ofEpochSecond(req.getStartTime()).atZone(ZoneOffset.systemDefault()).toLocalDate();

        String newFileName = req.getFileName().replace(".mp4", ".m3u8");
        String segment = Paths.get(req.getFolder(), localDate.format(DateTimeFormatter.ISO_DATE), req.getFileName().replace(".mp4", "") + "_%03d.ts").toAbsolutePath().toString();

        Path srcPath = Paths.get(req.getFolder(), localDate.format(DateTimeFormatter.ISO_DATE), req.getFileName());
        Path dstPath = Paths.get(req.getFolder(), localDate.format(DateTimeFormatter.ISO_DATE), newFileName);
        // 预转码
        tsSplit(srcPath, dstPath, segment);

        Pair<Map<String, String>, Map<String, String>> codecs = FFprobeUtil.getCodecsWithFFprobe(srcPath);
        Map<String, String> videoCodec = codecs.getLeft();
        Map<String, String> audioCodec = codecs.getRight();

        Files.deleteIfExists(srcPath);

        int bandWidth = Integer.parseInt(videoCodec.get("bit_rate"));

        VideoRecord record = baseMapper.selectMp4File(channel.getDeviceId(), channel.getChannelId(), localDate, req.getFileName()).orElseGet(() -> new VideoRecord());
        record.setChannelId(channel.getChannelId());
        record.setDeviceId(channel.getDeviceId());
        record.setDuration(Float.valueOf(req.getTimeLen() * 1000).intValue());
        record.setStartTime(req.getStartTime() * 1000);
        record.setDate(localDate);
        record.setFilePath(newFileName);
        record.setFolder(req.getFolder());
        record.setSize(req.getFileSize());
        if (audioCodec != null) {
            bandWidth += Integer.parseInt(audioCodec.get("bit_rate"));
            record.setBandwidth(bandWidth);
            record.setAudioCodec(audioCodec.get("codec_name"));
            record.setVideoCodec(videoCodec.get("codec_name"));
        }
        record.setResolution(videoCodec.get("width") + "x" + videoCodec.get("height"));
        saveOrUpdate(record);
    }

    private void tsSplit(Path src, Path dst, String segment) throws IOException, InterruptedException {
        tsSplit(src, dst, segment, 5);
    }

    private void tsSplit(Path src, Path dst, String segment, Integer hlsTime) throws IOException, InterruptedException {
        FFmpegUtil.run(
                60,
                "-i", src.toAbsolutePath().toString(),
                "-c:v", "copy",
                "-c:a", "aac", "-ab", "48k", "-ar", "44100",
                "-f", "hls",
                "-hls_time", hlsTime.toString(),
                "-hls_list_size", "0",
                "-hls_segment_filename", segment,
                dst.toAbsolutePath().toString()
        );
    }

    public DevicePreviewPlayVO playback(String deviceId, String channelId, LocalDateTime start) throws IOException, InterruptedException {
        DevicePreviewPlayVO vo = new DevicePreviewPlayVO();
        vo.setChannelId(channelId);
        vo.setDeviceId(deviceId);
        vo.setHlsUrl("/admin/device/record/hls.m3u8?deviceId=" + deviceId + "&channelId=" + channelId + "&start=" + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return vo;
    }

    private List<VideoRecord> transToContinuous(LocalDateTime start, List<VideoRecord> records) {
        AtomicLong lastStopTime = new AtomicLong();
        Long startMill = DateUtil.toMill(start);
        return records.stream().filter(item -> {
            if (lastStopTime.get() == 0) {
                if (item.getStartTime() >= startMill || item.getStartTime() + item.getDuration() >= startMill) {
                    lastStopTime.set(item.getStartTime() + item.getDuration());
                    return true;
                } else {
                    return false;
                }
            }

            if (item.getStartTime() - lastStopTime.get() > continuousMaxInterval) {
                return false;
            }

            lastStopTime.set(item.getStartTime() + item.getDuration());
            return true;
        }).toList();
    }

    private List<String> buildEXTINF(List<VideoRecord> records, Long start) {
        @AllArgsConstructor
        @EqualsAndHashCode
        class DiffOfDuration {
            public int index;
            public long diff;
            public VideoRecord record;
            public Long duration;
            public String ts;
        }

        int index = 0;
        List<DiffOfDuration> durations = new ArrayList<>();
        for (VideoRecord record : records) {
            long recordStart = record.getStartTime();
            Path path = Paths.get(record.getFolder(), record.getDate().format(DateTimeFormatter.ISO_DATE), record.getFilePath());
            try {
                List<String> lines = Files.readAllLines(path);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.startsWith("#EXTINF")) {
                        long duration = new BigDecimal(line.substring("#EXTINF".length() + 1).replace(",", "")).multiply(BigDecimal.valueOf(1000)).longValue();
                        durations.add(new DiffOfDuration(index, start - recordStart, record, duration, lines.get(i + 1)));
                        recordStart += duration;
                        index++;
                    }
                }
            } catch (IOException e) {
                log.error("m3u8读取失败, path={}, err={}", path, e.getMessage());
            }
        }
        if (durations.isEmpty()) {
            return new ArrayList<>();
        }

        // 找出时间差大于0的，正序第一个就是最接近用户播放时间的片段,片段的开始时间总是早于或等于用户选中的时间
        List<DiffOfDuration> sorted = durations.stream().filter(item -> item.diff >= 0).sorted(Comparator.comparing(k -> k.diff)).toList();
        int splitIndex;
        if (sorted.isEmpty()) {
            sorted = durations.stream().filter(item -> item.diff < 0).sorted(Comparator.comparing(k -> k.diff)).toList();
            splitIndex = sorted.get(sorted.size() - 1).index;
        } else {
            splitIndex = sorted.get(0).index;
        }

        List<String> result = new ArrayList<>();
        int offset = 0;
        List<DiffOfDuration> subList = durations.subList(splitIndex, durations.size());
        String mergeTs = "0";
        for (int i = 0; i < subList.size(); i++) {
            DiffOfDuration diffOfDuration = subList.get(i);
            VideoRecord record = diffOfDuration.record;
            if (i == 0) {
                long duration = diffOfDuration.duration - diffOfDuration.diff;
                if (diffOfDuration.diff < 5000) {
                    // 合并前两段视频
                    mergeTs = subList.get(i + 1).ts;
                    duration += subList.get(i + 1).duration;
                }
                String durationStr = BigDecimal.valueOf(duration).divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP).toPlainString();
                result.add("#EXTINF:" + durationStr + ",\n");
                result.add(String.format("ts/%s/%d/%d/%s/%s\n", record.getId(), offset, diffOfDuration.diff, mergeTs, diffOfDuration.ts));
                offset += (int) (duration);
            } else if (i == 1 && !mergeTs.equals("0")) {
                continue;
            } else {
                String duration = BigDecimal.valueOf(diffOfDuration.duration).divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP).toPlainString();
                result.add("#EXTINF:" + duration + ",\n");
                result.add(String.format("ts/%s/%d/%d/0/%s\n", record.getId(), offset, 0, diffOfDuration.ts));
                offset += diffOfDuration.duration;
            }
        }
        return result;
    }

    public Response m3u8(String deviceId, String channelId, LocalDateTime start, LocalDateTime end) throws IOException, InterruptedException {
        DeviceChannel channel = deviceChannelMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        if (end == null) end = start.toLocalDate().atTime(23, 59, 59);
        Long startMill = DateUtil.toMill(start);

        List<VideoRecord> records = list(new LambdaQueryWrapper<VideoRecord>()
                .eq(VideoRecord::getDeviceId, channel.getDeviceId())
                .eq(VideoRecord::getChannelId, channel.getChannelId())
                .ge(VideoRecord::getStartTime, DateUtil.toMill(start.minusMinutes(3).withSecond(0)))
                .le(VideoRecord::getStartTime, DateUtil.toMill(end.plusMinutes(3)))
                .orderByAsc(VideoRecord::getStartTime));
        records = transToContinuous(start, records);

        if (records.isEmpty()) {
            throw new BizException("没有可播放的内容");
        }

        List<String> strings = buildEXTINF(records, startMill);
        Integer max = strings.stream().filter(item -> item.startsWith("#EXTINF"))
                .map(item -> item.substring("#EXTINF".length() + 1).replace(",", ""))
                .map(Double::parseDouble)
                .map(Math::ceil)
                .map(Double::intValue)
                .max(Integer::compareTo)
                .orElse(0);

        VideoRecord videoRecord = records.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n");
        sb.append("#EXT-X-TARGETDURATION:").append(max).append("\n");
        sb.append("#EXT-X-STREAM-INF:BANDWIDTH=" + (videoRecord.getBandwidth() * 1.2 / 1000 * 1000) + ",CODECS=\"" + FFprobeUtil.buildM3u8CodecString(videoRecord) + "\"\n");
        sb.append("#EXT-X-MEDIA-SEQUENCE:0\n");
        for (String ctn : strings) {
            sb.append(ctn);
        }
        sb.append("#EXT-X-ENDLIST");
        return Response.status(Response.Status.OK)
                .type("audio/mpegurl")
                .entity(sb)
                .build();
    }

    public void ts(Integer recordId, Long offset, Float startTime, String mergeFileName, String fileName, String rangeHeader, RoutingContext context) throws IOException, InterruptedException {
        Optional<VideoRecord> optById = getOptById(recordId);
        HttpServerResponse response = context.response();
        if (optById.isEmpty()) {
            response.setStatusCode(Response.Status.NOT_FOUND.getStatusCode());
            return;
        }

        VideoRecord videoRecord = optById.get();
        Path mp4File = Paths.get(videoRecord.getFolder(), videoRecord.getDate().format(DateTimeFormatter.ISO_DATE), fileName);
        File file = mp4File.toFile();
        if (!file.exists() || !file.isFile()) {
            response.setStatusCode(Response.Status.NOT_FOUND.getStatusCode());
            return;
        }
        Path tempFile = Files.createTempFile("ts_stream", ".ts");
        Path tempFile2 = Files.createTempFile("ts_stream", ".ts");
        Path listFile = Files.createTempFile("concat", ".txt");
        try {


            if (!"0".equals(mergeFileName)) {
                Path merge = Paths.get(videoRecord.getFolder(), videoRecord.getDate().format(DateTimeFormatter.ISO_DATE), mergeFileName);
                File mergeFile = merge.toFile();
                if (!mergeFile.exists() || !mergeFile.isFile()) {
                    response.setStatusCode(Response.Status.NOT_FOUND.getStatusCode());
                    return;
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile.toFile()))) {
                    writer.write("file '" + mp4File.toAbsolutePath().toString().replace("'", "'\\''") + "'");
                    writer.newLine();
                    writer.write("file '" + merge.toAbsolutePath().toString().replace("'", "'\\''") + "'");
                    writer.newLine();
                }

                FFmpegUtil.run(
                        60,
                        "-f", "mpegts",
                        "-f", "concat", "-safe", "0",
                        "-i", listFile.toAbsolutePath().toString(),
                        "-c", "copy",
                        "-y", tempFile.toAbsolutePath().toString()
                );
                mp4File = tempFile;
            }

            FFmpegUtil.run(
                    60,
                    "-i", mp4File.toAbsolutePath().toString(),
                    "-ss", BigDecimal.valueOf(startTime).divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                    "-output_ts_offset", BigDecimal.valueOf(offset).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                    "-c", "copy",
                    "-y", tempFile2.toAbsolutePath().toString()
            );


            long fileSize = tempFile2.toFile().length();
            if (rangeHeader != null && !rangeHeader.replace("bytes=", "").isEmpty()) {
                String header = rangeHeader.replace("bytes=", "");
                if (header.contains(",")) {
                    response.setStatusCode(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE.getStatusCode());
                    return;
                }
                String[] ranges = header.split("-");

                long start = ranges[0].isEmpty() ? 0 : Long.parseLong(ranges[0]);
                long end = fileSize - 1;
                if (ranges.length == 2) {
                    end = Long.parseLong(ranges[1]);
                }
                long length = end - start + 1;

                // 请求范围不合法
                if (start < 0 || end > fileSize - 1) {
                    response.setStatusCode(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE.getStatusCode());
                    return;
                }
                byte[] buffer = new byte[8192];
                response.putHeader("Content-Type", "video/MP2T");
                try (FileInputStream inputStream = new FileInputStream(tempFile2.toFile())) {
                    long remaining = length;
                    inputStream.skip(start);

                    while (remaining > 0) {
                        int read = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                        if (read == -1) break;
                        // 处理数据（写入网络 / 输出流）
                        remaining -= read;
                        response.send(Buffer.buffer(Arrays.copyOfRange(buffer, 0, read)));
                    }
                }
            } else {
                response.putHeader("Content-Type", "video/MP2T");
                response.send(Buffer.buffer(Files.readAllBytes(tempFile2)));
            }
        } finally {
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempFile2);
            Files.deleteIfExists(listFile);
        }
    }

    public boolean setSpeed(String deviceId, String channelId, String ssrc, float speed) {
        Device device = deviceMapper.selectByDeviceId(deviceId);
        if (device == null) {
            throw new BizException("设备不存在");
        }
        new InfoRequest(device.toGbDevice(channelId)).withStreamId(ssrc).withSpeed(speed).execute();
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean deleteByChannelId(String channelId) {
        List<VideoRecord> videoRecords = baseMapper.selectList(new LambdaQueryWrapper<VideoRecord>().eq(VideoRecord::getChannelId, channelId));
        videoRecords.stream().map(VideoRecord::getFolder).distinct().forEach(item -> {
            File directory = new File(item);
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return baseMapper.delete(new LambdaQueryWrapper<VideoRecord>().eq(VideoRecord::getChannelId, channelId)) > 0;
    }

    @Data
    @AllArgsConstructor
    private static class ConcatFileResult {
        private Path path;
        private Long start;
    }

    private ConcatFileResult concatFileList(List<VideoRecord> records) throws IOException {
        if (records.isEmpty()) {
            throw new BizException("选定时段内没有视频");
        }
        List<String> list = records
                .stream().flatMap(videoRecord -> {
                    List<String> paths = new ArrayList<>();
                    int fileId = 0;
                    while (true) {
                        Path ts = Paths.get(videoRecord.getFolder(), videoRecord.getDate().format(DateTimeFormatter.ISO_DATE), videoRecord.getFilePath().replace(".m3u8", String.format("_%03d.ts", fileId)));
                        if (!Files.exists(ts)) {
                            break;
                        }
                        paths.add(ts.toAbsolutePath().toString());
                        fileId++;
                    }
                    return paths.stream();
                }).toList();

        Path listFile = Files.createTempFile("concat", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile.toFile()))) {
            for (String filePath : list) {
                writer.write("file '" + filePath.replace("'", "'\\''") + "'");
                writer.newLine();
            }
        }
        return new ConcatFileResult(listFile, records.get(0).getStartTime());
    }

    public void download(String deviceId, String channelId, LocalDateTime startDate, LocalDateTime endDate, RoutingContext context) throws IOException, InterruptedException, TimeoutException {
        HttpServerResponse response = context.response();
        List<VideoRecord> records = list(new LambdaQueryWrapper<VideoRecord>()
                .eq(VideoRecord::getDeviceId, deviceId)
                .eq(VideoRecord::getChannelId, channelId)
                .ge(VideoRecord::getStartTime, DateUtil.toMill(startDate.withSecond(0)))
                .le(VideoRecord::getStartTime, DateUtil.toMill(endDate))
                .orderByAsc(VideoRecord::getStartTime));
        ConcatFileResult concatFileList = concatFileList(records);

        String fileName = String.format("%s-%s_%s.mp4", channelId, startDate.format(DateTimeFormatter.ISO_DATE_TIME), endDate.format(DateTimeFormatter.ISO_DATE_TIME));
        response.putHeader("Content-Type", "video/mp4");
        response.putHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        Path result = Files.createTempFile("concat", ".mp4");
        String[] cmd;
        if (Objects.equals(records.getFirst().getVideoCodec(), "hevc")) {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", concatFileList.getPath().toAbsolutePath().toString(),
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-tag:v", "hvc1",
                    "-movflags", "frag_keyframe+empty_moov",
                    "-f", "mp4"
            };
        } else {
            cmd = new String[]{
                    "-f", "concat",
                    "-safe", "0",
                    "-i", concatFileList.getPath().toAbsolutePath().toString(),
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-movflags", "frag_keyframe+empty_moov",
                    "-f", "mp4"
            };
        }

        try {
            FFmpegUtil.pipe(600,
                    (bytes) -> {
                        response.send(Buffer.buffer(bytes));
                    },
                    cmd
            );
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new BizException("视频下载失败");
        } finally {
            Files.deleteIfExists(result);
            Files.deleteIfExists(concatFileList.getPath());
        }


    }
}
