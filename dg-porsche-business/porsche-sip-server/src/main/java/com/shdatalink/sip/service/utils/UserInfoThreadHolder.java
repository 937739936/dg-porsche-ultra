package com.shdatalink.sip.service.utils;


import com.shdatalink.sip.service.module.user.vo.UserInfo;

/**
 * 存储当前登录用户信息
 */
public class UserInfoThreadHolder {

    /**
     * 保存当前登录用户信息的ThreadLocal
     * 在拦截器操作 添加、删除相关用户数据
     */
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加当前登录用户信息方法
     * 在拦截器方法执行前调用设置获取用户
     */
    public static void addUserInfo(UserInfo user) {
        THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前登录用户信息方法
     */
    public static UserInfo getCurrentUser() {
        return THREAD_LOCAL.get();
    }

    /**
     * 删除当前登录用户信息方法
     * 在拦截器方法执行后 移除当前登录信息
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
