package com.shdatalink.resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.entity.Contract;
import com.shdatalink.service.ContractService;
import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@Path("/tenant")
public class TenantResource {

    @Inject
    ContractService contractService;

    @GET
    @Path("/pageQuery")
    public IPage<Contract> pageQuery() {
        return contractService.pageQuery();
    }



}
