package com.shdatalink.sip.server.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Query 参数解析工具类
 * 支持解析 URL 查询字符串、构建查询字符串、参数编解码等操作
 */
public class QueryParamParser {
    
    /**
     * 解析查询字符串为 Map
     *
     * @param queryString 查询字符串（可以包含或不包含 ?）
     * @return 参数 Map
     */
    public static Map<String, String> parseToMap(String queryString) {
        return parseToMap(queryString, StandardCharsets.UTF_8);
    }
    
    /**
     * 解析查询字符串为 Map
     *
     * @param queryString 查询字符串
     * @param charset     字符集
     * @return 参数 Map
     */
    public static Map<String, String> parseToMap(String queryString, Charset charset) {
        Map<String, String> params = new LinkedHashMap<>();
        
        if (queryString == null || queryString.trim().isEmpty()) {
            return params;
        }
        
        // 移除开头的 ?
        String cleanQuery = queryString.startsWith("?") ? queryString.substring(1) : queryString;
        
        if (cleanQuery.isEmpty()) {
            return params;
        }
        
        String[] pairs = cleanQuery.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            
            String[] keyValue = pair.split("=", 2);
            String key = urlDecode(keyValue[0], charset);
            String value = keyValue.length == 2 ? urlDecode(keyValue[1], charset) : "";
            
            params.put(key, value);
        }
        
        return params;
    }
    
    /**
     * 解析查询字符串为 MultiValueMap（支持同名参数）
     *
     * @param queryString 查询字符串
     * @return MultiValueMap
     */
    public static MultiValueMap parseToMultiValueMap(String queryString) {
        return parseToMultiValueMap(queryString, StandardCharsets.UTF_8);
    }
    
    /**
     * 解析查询字符串为 MultiValueMap（支持同名参数）
     *
     * @param queryString 查询字符串
     * @param charset     字符集
     * @return MultiValueMap
     */
    public static MultiValueMap parseToMultiValueMap(String queryString, Charset charset) {
        MultiValueMap params = new MultiValueMap();
        
        if (queryString == null || queryString.trim().isEmpty()) {
            return params;
        }
        
        // 移除开头的 ?
        String cleanQuery = queryString.startsWith("?") ? queryString.substring(1) : queryString;
        
        if (cleanQuery.isEmpty()) {
            return params;
        }
        
        String[] pairs = cleanQuery.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            
            String[] keyValue = pair.split("=", 2);
            String key = urlDecode(keyValue[0], charset);
            String value = keyValue.length == 2 ? urlDecode(keyValue[1], charset) : "";
            
            params.add(key, value);
        }
        
        return params;
    }
    
    /**
     * 从完整 URL 中解析查询参数
     *
     * @param url 完整 URL
     * @return 参数 Map
     */
    public static Map<String, String> parseFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        int queryStart = url.indexOf('?');
        if (queryStart == -1 || queryStart == url.length() - 1) {
            return new LinkedHashMap<>();
        }
        
        String queryString = url.substring(queryStart + 1);
        // 处理 # 片段
        int fragmentStart = queryString.indexOf('#');
        if (fragmentStart != -1) {
            queryString = queryString.substring(0, fragmentStart);
        }
        
        return parseToMap(queryString);
    }
    
    /**
     * 构建查询字符串
     *
     * @param params 参数 Map
     * @return 查询字符串（不包含 ?）
     */
    public static String buildQueryString(Map<String, Object> params) {
        return buildQueryString(params, StandardCharsets.UTF_8);
    }
    
    /**
     * 构建查询字符串
     *
     * @param params  参数 Map
     * @param charset 字符集
     * @return 查询字符串（不包含 ?）
     */
    public static String buildQueryString(Map<String, Object> params, java.nio.charset.Charset charset) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        
        return params.entrySet().stream()
                .map(entry -> {
                    String encodedKey = urlEncode(entry.getKey(), charset);
                    String encodedValue = urlEncode(entry.getValue().toString(), charset);
                    return encodedKey + "=" + encodedValue;
                })
                .collect(Collectors.joining("&"));
    }
    
    /**
     * 构建查询字符串（支持同名参数）
     *
     * @param params MultiValueMap
     * @return 查询字符串
     */
    public static String buildQueryString(MultiValueMap params) {
        return buildQueryString(params, StandardCharsets.UTF_8);
    }
    
    /**
     * 构建查询字符串（支持同名参数）
     *
     * @param params  MultiValueMap
     * @param charset 字符集
     * @return 查询字符串
     */
    public static String buildQueryString(MultiValueMap params, java.nio.charset.Charset charset) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        
        List<String> pairs = new ArrayList<>();
        for (String key : params.keySet()) {
            for (String value : params.getValues(key)) {
                String encodedKey = urlEncode(key, charset);
                String encodedValue = urlEncode(value, charset);
                pairs.add(encodedKey + "=" + encodedValue);
            }
        }
        
        return String.join("&", pairs);
    }
    
    /**
     * URL 解码
     *
     * @param encoded 编码的字符串
     * @return 解码后的字符串
     */
    public static String urlDecode(String encoded) {
        return urlDecode(encoded, StandardCharsets.UTF_8);
    }
    
    /**
     * URL 解码
     *
     * @param encoded 编码的字符串
     * @param charset 字符集
     * @return 解码后的字符串
     */
    public static String urlDecode(String encoded, Charset charset) {
        if (encoded == null) return null;
        try {
            return URLDecoder.decode(encoded, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Charset not supported: " + charset.name(), e);
        }
    }
    
    /**
     * URL 编码
     *
     * @param decoded 解码的字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String decoded) {
        return urlEncode(decoded, StandardCharsets.UTF_8);
    }
    
    /**
     * URL 编码
     *
     * @param decoded 解码的字符串
     * @param charset 字符集
     * @return 编码后的字符串
     */
    public static String urlEncode(String decoded, java.nio.charset.Charset charset) {
        if (decoded == null) return null;
        try {
            return URLEncoder.encode(decoded, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Charset not supported: " + charset.name(), e);
        }
    }
    
    /**
     * 获取参数值，如果不存在返回默认值
     *
     * @param params       参数 Map
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static String getValue(Map<String, String> params, String key, String defaultValue) {
        return params != null && params.containsKey(key) ? params.get(key) : defaultValue;
    }
    
    /**
     * 获取整数参数值
     *
     * @param params       参数 Map
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 整数值
     */
    public static int getIntValue(Map<String, String> params, String key, int defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(params.get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔参数值
     *
     * @param params       参数 Map
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public static boolean getBooleanValue(Map<String, String> params, String key, boolean defaultValue) {
        if (params == null || !params.containsKey(key)) {
            return defaultValue;
        }
        String value = params.get(key).toLowerCase();
        return "true".equals(value) || "1".equals(value) || "yes".equals(value) || "on".equals(value);
    }
    
    /**
     * 支持多值的参数 Map
     */
    public static class MultiValueMap {
        private final Map<String, List<String>> map = new LinkedHashMap<>();
        
        public void add(String key, String value) {
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        
        public String getFirst(String key) {
            List<String> values = map.get(key);
            return values != null && !values.isEmpty() ? values.get(0) : null;
        }
        
        public List<String> getValues(String key) {
            return map.getOrDefault(key, Collections.emptyList());
        }
        
        public Set<String> keySet() {
            return map.keySet();
        }
        
        public boolean isEmpty() {
            return map.isEmpty();
        }
        
        public int size() {
            return map.size();
        }
        
        public boolean containsKey(String key) {
            return map.containsKey(key);
        }
    }
}