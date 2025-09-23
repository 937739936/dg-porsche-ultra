package org.acme.cdi.aop;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)  // 确保拦截器生效
public class LoggingInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    // 环绕拦截：在目标方法执行前后添加逻辑
    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();

        // 方法执行前：打印日志
        logger.info("Calling method: " + className + "." + methodName);

        long startTime = System.currentTimeMillis();
        try {
            // 执行目标方法（继续调用链）
            return context.proceed();
        } finally {
            // 方法执行后：打印耗时
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Method " + className + "." + methodName + " executed in " + duration + "ms");
        }
    }
}
