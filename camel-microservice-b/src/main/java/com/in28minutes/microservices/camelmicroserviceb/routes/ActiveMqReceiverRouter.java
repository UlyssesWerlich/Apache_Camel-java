package com.in28minutes.microservices.camelmicroserviceb.routes;

import com.in28minutes.microservices.camelmicroserviceb.domain.dto.CurrencyExchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class ActiveMqReceiverRouter  extends RouteBuilder {

    @Autowired
    MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;

    @Autowired
    MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {

        //JSON
        //CurrencyExchange
        from("activemq:my-activemq-queue")
                .unmarshal(createEncryptor())
//                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
//                .bean(myCurrencyExchangeProcessor)
//                .bean(myCurrencyExchangeTransformer)
                .to("log:received-message-from-active-mq");

        //XML
        //CurrencyExchange
//        from("activemq:my-activemq-xml-queue")
//                .unmarshal()
//                .jacksonxml(CurrencyExchange.class)
//                .to("log:received-message-from-active-mq");

//        from("activemq:split-queue")
//                .to("log:received-message-from-active-mq");
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

@Component
class MyCurrencyExchangeProcessor{

    Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeProcessor.class);

    public void processMessage(CurrencyExchange currencyExchange){
        logger.info("Do some processing with currencyExchange.getConversionMultiple() value witch {}",
                currencyExchange.getConversionMultiple());
    }
}

@Component
class MyCurrencyExchangeTransformer{

    Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeTransformer.class);

    public CurrencyExchange processMessage(CurrencyExchange currencyExchange){

        currencyExchange.setConversionMultiple(
            currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN)
        );
        logger.info("Do some processing with currencyExchange.getConversionMultiple() value witch {}",
                currencyExchange.getConversionMultiple());
        return currencyExchange;
    }
}

