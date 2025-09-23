package org.acme.cdi;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/cdi")
public class FileStorageResource {

    @Inject
    private FileStorageService fileStorageService;

    @GET
    public String hello() {
        return  fileStorageService.getStoragePath();
    }
}