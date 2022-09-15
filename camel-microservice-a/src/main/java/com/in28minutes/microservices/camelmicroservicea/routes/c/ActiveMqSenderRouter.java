package com.in28minutes.microservices.camelmicroservicea.routes.c;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class ActiveMqSenderRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // timer
        from("timer:active-mq-timer?period=10000")
                .transform().constant("My message for Active MQ")
                .marshal(createEncryptor())
                .log("${body}")
                .to("activemq:my-activemq-queue");
        //queue

//        from("file:files/json")
//                .log("${body}")
//                .to("activemq:my-activemq-queue");

//        from("file:files/xml")
//                .log("${body}")
//                .to("activemq:my-activemq-xml-queue");

    }

    private CryptoDataFormat createEncryptor()
            throws KeyStoreException, CertificateException, IOException,
            NoSuchAlgorithmException, UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        ClassLoader classLoader = getClass().getClassLoader();
        keyStore.load(classLoader.getResourceAsStream("myDesKey.jceks"), "someKeystorePassword".toCharArray());
        Key sharedKey = keyStore.getKey("myDesKey", "someKeyPassword".toCharArray());

        CryptoDataFormat sharedKeyCrypto = new CryptoDataFormat("DES", sharedKey);
        return sharedKeyCrypto;
    }
}
