package com.shdatalink.sip.service.module.user;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.ResultWrapper;
import com.shdatalink.framework.common.utils.PasswordUtil;
import com.shdatalink.sip.service.common.dto.PageParam;
import com.shdatalink.sip.service.module.user.entity.User;
import com.shdatalink.sip.service.module.user.entity.UserAccessKey;
import com.shdatalink.sip.service.module.user.service.UserAccessKeyService;
import com.shdatalink.sip.service.module.user.service.UserService;
import com.shdatalink.sip.service.module.user.vo.*;
import com.shdatalink.sip.service.utils.UserInfoUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;

import java.util.Objects;


/**
 * 用户管理
 */
@Path("admin/user")
@RequiredArgsConstructor
public class UserController {

    @Inject
    UserService userService;
    @Inject
    UserAccessKeyService userAccessKeyService;

    /**
     * 用户列表
     * @param param
     * @return
     */
    @GET
    @Path("page")
    public IPage<UserPage> getPage(UserPageParam param) {
        return userService.getPage(param);
    }

    /**
     * 获取用户信息
     */
    @GET
    @Path("detail")
    public UserDetailVO detail() {
        return userService.detail(UserInfoUtil.getUserInfoWithThrow().getId());
    }


    /**
     * 修改用户密码
     * @param req 请求参数
     */
    @POST
    @Path(value = "updatePassword")
    public ResultWrapper<?> modifyPassword(@Valid ModifyPasswordReq req) {
        UserInfo userInfo = UserInfoUtil.getUserInfoWithThrow();
        User user = userService.getById(userInfo.getId());
        if (user == null) {
            return ResultWrapper.fail(BaseResultCodeEnum.SYSTEM_ERROR, "未找到用户!");
        }
        String oldPassword = req.getOldPassword();
        String newPassword = req.getNewPassword();
        String passwordEncode = PasswordUtil.encrypt(user.getUsername(), oldPassword, user.getSalt());
        if (!user.getPassword().equals(passwordEncode)) {
            return ResultWrapper.fail(BaseResultCodeEnum.SYSTEM_ERROR, "旧密码输入错误!");
        }
        user.setPassword(PasswordUtil.encrypt(user.getUsername(), newPassword, user.getSalt()));
        userService.updateById(user);
        return ResultWrapper.success();
    }

    /**
     * 创建/修改用户
     * @param param
     */
    @POST
    @Path("save")
    public void save(@Valid UserSaveParam param) {
        userService.saveUser(param);
    }

    /**
     * 用户详情
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    public UserDetailVO get(@PathParam("id") Integer id) {
        return userService.detail(id);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DELETE
    @Path("delete")
    public boolean delete(@QueryParam("id") Integer id) {
        userService.delete(id);
        return true;
    }

    /**
     * 添加access key
     */
    @POST
    @Path("accessKey/generate")
    public UserAccessKey generateAccessKey() {
        return userAccessKeyService.generateAccessKey();
    }

    /**
     * 删除access key
     */
    @DELETE
    @Path("accessKey/delete")
    public boolean deleteAccessKey(@QueryParam("id") Integer id) {
        UserAccessKey userAccessKey = userAccessKeyService.getOptById(id).orElseThrow(() -> new BizException("key 不存在"));
        if (!Objects.equals(userAccessKey.getUserId(), UserInfoUtil.getUserId())) {
            throw new BizException("无权限删除");
        }
        return userAccessKeyService.removeById(id);
    }
    /**
     * access key 列表
     */
    @GET
    @Path("accessKey/page")
    public IPage<UserAccessKey> accessKeyPage(PageParam param) {
        return userAccessKeyService.page(param.toPage(), new LambdaQueryWrapper<UserAccessKey>().eq(UserAccessKey::getUserId, UserInfoUtil.getUserId()));
    }
}
