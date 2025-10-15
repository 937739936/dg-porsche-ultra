package com.shdatalink.sip.server.module.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class PermissionParam {
    private String name;
    private String permission;
    private List<PermissionParam> children;
}
