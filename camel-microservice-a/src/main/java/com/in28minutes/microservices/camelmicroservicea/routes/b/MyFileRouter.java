package com.in28minutes.microservices.camelmicroservicea.routes.b;

import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyFileRouter extends RouteBuilder {

    @Autowired
    private DeciderBean deciderBean;

    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("Files-Input_Route")
                .transform().body(String.class)
                .choice()
                    .when(simple("${file:ext} ends with 'xml'"))
                        .log("XML FILE")
//                    .when(simple("${body} contains 'USD'"))
                    .when(method(deciderBean))
                        .log("Not a XML FILE but contains USD")
                    .otherwise()
                        .log("Not a XML FILE")
                .end()
//                .to("direct://log-file-values")
                .to("file:files/output");

        from("direct:log-file-values")
                .log("${body}")
                .log("${messageHistory} ${headers.CamelFileAbsolute}");
    }
}

@Component
class DeciderBean {

    Logger logger = LoggerFactory.getLogger(DeciderBean.class);

    public boolean isThisConditionMet(String body,
                                      @Headers Map<String, String> headers,
                                      @ExchangeProperties Map<String, String> exchangeProperties
    ){
        logger.info("DeciderBean {} {} {}", body, headers, exchangeProperties);
        return true;
    }
}