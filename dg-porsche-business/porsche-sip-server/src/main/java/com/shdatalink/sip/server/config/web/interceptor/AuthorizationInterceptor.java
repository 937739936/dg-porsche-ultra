package com.shdatalink.sip.server.config.web.interceptor;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.annotation.CheckPermission;
import com.shdatalink.framework.common.enums.CheckPermissionMode;
import com.shdatalink.framework.common.exception.UnAuthorizedException;
import com.shdatalink.sip.server.module.user.service.LoginService;
import com.shdatalink.sip.server.module.user.service.UserService;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import com.shdatalink.sip.server.config.web.UserInfoThreadHolder;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 授权拦截器，用于检查请求的合法性。
 */
public class AuthorizationInterceptor implements ContainerRequestFilter {


    // 注入ResourceInfo，用于获取当前请求的资源类和方法
    @Context
    ResourceInfo resourceInfo;
    @Inject
    LoginService loginService;
    @Inject
    UserService userService;

    /**
     * 存放与请求头中的用户令牌标识
     */
    public final static String HEAD_PARAM_AUTHORIZATION = "authorization";

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        // 检查是否标记为匿名访问
        if (resourceInfo.getResourceClass().isAnnotationPresent(Anonymous.class)
                || resourceInfo.getResourceMethod().isAnnotationPresent(Anonymous.class)) {
            return;
        }

        // 获取请求头中的令牌
        String token = request.getHeaderString(HEAD_PARAM_AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            MultivaluedMap<String, String> queryParameters = request.getUriInfo().getQueryParameters();
            if (MapUtils.isNotEmpty(queryParameters)) {
                token = queryParameters.getFirst(HEAD_PARAM_AUTHORIZATION);
            }
        }

        if (StringUtils.isBlank(token)) {
            throw new UnAuthorizedException();
        }

        UserInfo userInfo = loginService.userInfoByToken(token);
        if (Objects.isNull(userInfo)) {
            throw new UnAuthorizedException("无效的用户令牌");
        }
        UserInfo userInfoEntity = userService.getUserInfo(userInfo.getId());
        if (userInfoEntity == null) {
            throw new UnAuthorizedException();
        }
        if (userInfoEntity.getEnabled() == null || !userInfoEntity.getEnabled()) {
            throw new UnAuthorizedException();
        }

        // 接口权限校验
        this.checkPermission(userInfoEntity);

        // 添加当前登录信息
        UserInfoThreadHolder.addUserInfo(userInfoEntity);
    }

    /**
     * 检查接口权限
     */
    private void checkPermission(UserInfo userInfoEntity){
        if (resourceInfo.getResourceMethod().isAnnotationPresent(CheckPermission.class)) {
            CheckPermission annotation = resourceInfo.getResourceMethod().getAnnotation(CheckPermission.class);
            String[] value = annotation.value();
            CheckPermissionMode mode = annotation.mode();
            if (CheckPermissionMode.AND.equals(mode)) {
                if (CollectionUtils.isEmpty(userInfoEntity.getPermissionTokens())
                        || !new HashSet<>(userInfoEntity.getPermissionTokens()).containsAll(List.of(value))) {
                    throw new UnAuthorizedException();
                }
            } else {
                if (CollectionUtils.isEmpty(userInfoEntity.getPermissionTokens())
                        || Stream.of(value).noneMatch(permission -> userInfoEntity.getPermissionTokens().contains(permission))) {
                    throw new UnAuthorizedException();
                }
            }
        }
    }


}

