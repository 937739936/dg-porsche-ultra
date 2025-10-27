package com.shdatalink.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import jakarta.enterprise.context.ApplicationScoped;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

@ApplicationScoped
public class CustomTenantHandler implements TenantLineHandler {


    @Override
    public Expression getTenantId() {
        // 假设有一个租户上下文，能够从中获取当前用户的租户
        Long tenantId = 101L;
        // 返回租户ID的表达式，LongValue 是 JSQLParser 中表示 bigint 类型的 class
        return new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 根据需要返回是否忽略该表
        return "t_contract_extend".equals(tableName);
    }

}
