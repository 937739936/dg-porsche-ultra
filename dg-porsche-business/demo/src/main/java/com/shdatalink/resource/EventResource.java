package com.shdatalink.resource;

import com.shdatalink.event.ContractEvent;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/event")
public class EventResource {

    @Inject
    Event<ContractEvent> contractEvent;

    @GET
    @Path("/fireEvent")
    public Boolean fireEvent() {
        // 发布事件
        contractEvent.fire(new ContractEvent(1L, "CREATE"));
        contractEvent.fireAsync(new ContractEvent(2L, "DELETE"));
        return true;
    }
}
