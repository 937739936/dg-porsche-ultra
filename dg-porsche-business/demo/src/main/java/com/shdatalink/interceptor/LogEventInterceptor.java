package com.shdatalink.interceptor;

import com.shdatalink.annotation.LogEvent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LogEvent
@Interceptor
public class LogEventInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogEventInterceptor.class);

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        LogEvent interceptorBinding = ctx.getInterceptorBinding(LogEvent.class);
        log.info("invoked {}, value {}", ctx.getMethod().getName(), ctx.getInterceptorBinding(LogEvent.class).value());
        return ctx.proceed();
    }
}
