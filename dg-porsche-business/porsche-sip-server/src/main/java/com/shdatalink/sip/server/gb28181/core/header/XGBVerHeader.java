package com.shdatalink.sip.server.gb28181.core.header;

import javax.sip.header.Header;

public interface XGBVerHeader extends Header {
    String NAME = "X-GB-Ver";

    void setVersion(int m, int n);

    String getVersion();
}
