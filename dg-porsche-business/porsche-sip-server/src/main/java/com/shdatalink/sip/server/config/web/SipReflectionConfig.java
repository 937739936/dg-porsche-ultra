package com.shdatalink.sip.server.config.web;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
        targets = {
                java.util.Properties.class,
                javax.sip.SipStack.class,
                gov.nist.javax.sip.SipStackImpl.class,
                gov.nist.javax.sip.UtilsExt.class,
                gov.nist.core.LogWriter.class,
                gov.nist.javax.sip.stack.ServerLog.class,
                gov.nist.javax.sip.stack.DefaultTlsSecurityPolicy.class,
                gov.nist.javax.sip.stack.SIPTransactionStack.class,
        }, registerFullHierarchy = true
)
public class SipReflectionConfig {

}
