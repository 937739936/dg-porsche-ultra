package com.shdatalink.sip.server.gb28181.core.bean.annotations;


import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;

import java.lang.annotation.*;

/**
 * sip事件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SipEvent {

    /**
     * 事件
     *
     * @return {@link SipEnum.Method}
     */
    SipEnum.Method value();

    /**
     * cmd
     */
    SipEnum.Cmd cmd() default SipEnum.Cmd.NONE;
}
