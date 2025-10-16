package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * This class represents a WebRTC session with various properties.
 */
@Data
public class MediaPlayerResult {

    /**
     * The unique identifier for the session, e.g., "3-309".
     */
    @JsonProperty("identifier")
    private String identifier;

    /**
     * The local IP address. "::" is a shorthand in IPv6 for representing multiple groups of zeros.
     */
    @JsonProperty("local_ip")
    private String localIp;

    /**
     * The local port number.
     */
    @JsonProperty("local_port")
    private int    localPort;

    /**
     * The IP address of the peer in the session.
     */
    @JsonProperty("peer_ip")
    private String peerIp;

    /**
     * The port number of the peer.
     */
    @JsonProperty("peer_port")
    private int    peerPort;

    /**
     * The type identifier for the session, indicating it's a WebRTC session from MediaKit.
     */
    @JsonProperty("typeid")
    private String typeId;

    // Getters and setters...
}
