package com.shdatalink.sip.server.module.user;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.module.user.entity.Role;
import com.shdatalink.sip.server.module.user.entity.RolePermission;
import com.shdatalink.sip.server.module.user.service.PermissionService;
import com.shdatalink.sip.server.module.user.service.RoleDeviceService;
import com.shdatalink.sip.server.module.user.service.RolePermissionService;
import com.shdatalink.sip.server.module.user.service.RoleService;
import com.shdatalink.sip.server.module.user.vo.PermissionAddParam;
import com.shdatalink.sip.server.module.user.vo.RoleDeviceSaveParam;
import com.shdatalink.sip.server.module.user.vo.RolePermissionSaveParam;
import com.shdatalink.sip.server.module.user.vo.RoleSaveParam;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;

import java.util.List;

/**
 * 用户管理/角色管理
 */
@Path("admin/role")
public class RoleController {

    @Inject
    RoleService roleService;
    @Inject
    RolePermissionService rolePermissionService;
    @Inject
    RoleDeviceService roleDeviceService;
    @Inject
    PermissionService permissionService;

    /**
     * 保存角色
     */
    @Path("save")
    @POST
    public boolean saveRole(@Valid RoleSaveParam param) {
        return roleService.save(param);
    }

    /**
     * 删除角色
     */
    @DELETE
    @Path("delete")
    public boolean deleteRole(@QueryParam("id") Integer id) {
        Role role = roleService.getOptById(id).orElseThrow(() -> new BizException("角色不存在"));
        if (role.getProtect()) {
            throw new BizException("角色不可删除");
        }
        return roleService.removeById(id);
    }

    /**
     * 保存权限
     */
    @POST
    @Path("permission/save")
    public boolean saveRolePermission(@Valid RolePermissionSaveParam param) {
        return rolePermissionService.save(param);
    }

    /**
     * 保存关联设备
     */
    @POST
    @Path("device/save")
    public boolean saveRoleDevice(@Valid RoleDeviceSaveParam param) {
        return roleDeviceService.save(param);
    }

    /**
     * 查询角色的权限id
     */
    @GET
    @Path("permission")
    public List<Integer> getRolePermission(@QueryParam("roleId") Integer roleId) {
        return rolePermissionService.getBaseMapper()
                .selectByRoleId(roleId)
                .stream()
                .map(RolePermission::getPermissionId).toList();
    }

    /**
     * 查询角色的关联设备id
     */
    @GET
    @Path("device")
    public List<String> getRoleDevice(@QueryParam("roleId") Integer roleId) {
        return roleDeviceService.getBaseMapper().selectByRoleId(roleId);
    }

    /**
     * 添加权限
     */
    @POST
    @Path("addPermission")
    public boolean addPermission(@Valid PermissionAddParam param) {
        return permissionService.add(param);
    }
}
