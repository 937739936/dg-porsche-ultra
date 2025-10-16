package com.shdatalink.sip.server.gb28181.core.builder;

import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import javax.sip.Dialog;
import java.util.Map;

public class DialogHolder {
    private static final Map<String, Dialog> dialogMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> seqNumberMap = new ConcurrentHashMap<>();
    private static final Map<String, String> dialogIdSsrcMap = new ConcurrentHashMap<>();

    public static Dialog getDialog(String key) {
        return dialogMap.get(key);
    }

    public static Long generateSeqNumber(String key) {
        if (!seqNumberMap.containsKey(key)) {
            return null;
        }
        long seq = seqNumberMap.get(key) + 1;
        seqNumberMap.put(key, seq);
        return seq;
    }

    public static void putDialog(String ssrc, String dialogId, Dialog request) {
        dialogMap.put(ssrc, request);
        dialogIdSsrcMap.put(dialogId, ssrc);
        seqNumberMap.put(ssrc, request.getLocalSeqNumber());
    }

    public static void removeDialogId(String dialogId) {
        if (dialogIdSsrcMap.containsKey(dialogId)) {
            dialogMap.remove(dialogIdSsrcMap.get(dialogId));
            dialogIdSsrcMap.remove(dialogId);
            seqNumberMap.remove(dialogId);
        }
    }
}
