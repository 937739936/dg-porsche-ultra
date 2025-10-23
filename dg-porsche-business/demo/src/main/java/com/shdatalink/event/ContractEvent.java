// src/main/java/com/example/demo/event/UserEvent.java
package com.shdatalink.event;

import lombok.Data;

/**
 * 合同相关事件
 */
@Data
public class ContractEvent {
    /**
     * 合同ID
     */
    private final Long contractId;

    /**
     * 事件类型
     */
    private final String action;


}
