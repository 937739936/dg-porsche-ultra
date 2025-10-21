package com.shdatalink.framework.mysql.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
        targets = {
                com.baomidou.mybatisplus.core.handlers.CompositeEnumTypeHandler.class,
        }
)
public class MyBatisPlusReflectionConfig {

}
