package com.shdatalink.mysql.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.shdatalink.mysql.entity.BaseEntity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MP注入处理器
 */
@Slf4j
@ApplicationScoped
public class CustomMetaObjectHandler implements MetaObjectHandler {


    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                // 获取当前时间作为创建时间和更新时间，如果创建时间不为空，则使用创建时间，否则使用当前时间
                LocalDateTime current = Objects.isNull(baseEntity.getCreatedTime()) ? LocalDateTime.now() : baseEntity.getCreatedTime();
                baseEntity.setCreatedTime(current);
                baseEntity.setLastModifiedTime(current);

                // 如果创建人为空，则填充当前登录用户的信息 TODO
                if (Objects.isNull(baseEntity.getCreatedBy())) {
//                    UserLoginInfo loginUser = getLoginUser();
//                    if (Objects.nonNull(loginUser)) {
//                        Long userId = loginUser.getId();
//                        // 填充创建人、更新人
//                        baseEntity.setCreateBy(userId);
//                        baseEntity.setUpdateBy(userId);
//                    } else {
                    // 填充创建人、更新人
//                    baseEntity.setCreatedBy(DEFAULT_USER_ID);
//                    baseEntity.setLastModifiedBy(DEFAULT_USER_ID);
//                    }
                }
            } else {
                LocalDateTime date = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, date);
                this.strictInsertFill(metaObject, "lastModifiedTime", LocalDateTime.class, date);
            }
        } catch (Exception e) {
            throw new RuntimeException("自动注入异常 => " + e.getMessage());
        }
    }

    /**
     * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // 获取当前时间作为更新时间，无论原始对象中的更新时间是否为空都填充
            LocalDateTime current = LocalDateTime.now();
            if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                baseEntity.setLastModifiedTime(current);

                // 获取当前登录用户的ID，并填充更新人信息 TODO
//                UserLoginInfo loginUser = getLoginUser();
//                if (Objects.nonNull(loginUser)) {
//                    baseEntity.setUpdateBy(loginUser.getId());
//                } else {
//                    baseEntity.setUpdateBy(DEFAULT_USER_ID);
//                }
//                baseEntity.setUpdateBy(DEFAULT_USER_ID);
            } else {
                this.strictUpdateFill(metaObject, "lastModifiedTime", LocalDateTime.class, current);
            }
        } catch (Exception e) {
            throw new RuntimeException("自动注入异常 => " + e.getMessage());
        }
    }


}
