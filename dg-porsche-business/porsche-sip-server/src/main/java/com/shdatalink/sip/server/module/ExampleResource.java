package com.shdatalink.sip.server.module;

import com.shdatalink.framework.common.annotation.CheckPermission;
import com.shdatalink.framework.common.enums.CheckPermissionMode;
import com.shdatalink.sip.server.module.user.convert.UserConvert;
import com.shdatalink.sip.server.module.user.entity.User;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试
 */
//@Path("admin/test")
@Path("test")
public class ExampleResource {

    @Inject
    UserConvert userConvert;

    @Path("/testReturn")
    @GET
    public Integer testReturn() {
        return null;
    }


//    @Path("/getDict")
//    @GET
//    public List<Class<? extends IDict<?>>>  getDictEnums() {
//        return dictEnumRegistry.getDictEnums();
//    }

    @Path("/mapstruct")
    @GET
    public UserInfo mapstruct() {
        User user = new User();
        user.setUsername("test");
        return userConvert.toUserInfo(user);
    }

    /**
     * 权限校验注解
     */
    @CheckPermission(value = {"device:add", "test1"}, mode = CheckPermissionMode.AND)
//    @CheckPermission(value = {"device:add", "test1"}, mode = CheckPermissionMode.OR)
    @Path("/checkPermission")
    @GET
    public Boolean checkPermission() {
        return true;
    }

//    @Path("excel/import")
//    @POST
//    public List<ContractImportVo> importExcel(@RestForm File file) throws Exception {
//        FileInputStream fileInputStream = new FileInputStream(file);
//        return ExcelUtil.importExcel(fileInputStream, ContractImportVo.class);
//    }
//
//    @Path("excel/export")
//    @GET
//    public Response exportExcel() throws IOException, IllegalAccessException {
//        List<ContractImportVo> dataList = new ArrayList<>();
//        dataList.add(new ContractImportVo("001"));
//        dataList.add(new ContractImportVo("002"));
//        dataList.add(new ContractImportVo("003"));
//        return ExcelUtil.exportExcel(dataList, "测试", ContractImportVo.class);
//    }

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ContractImportVo {
//
//        @ExcelProperty("合同编号")
//        private String contractNo;
//    }


}
