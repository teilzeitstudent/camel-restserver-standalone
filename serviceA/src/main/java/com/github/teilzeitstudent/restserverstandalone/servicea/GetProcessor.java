package com.github.teilzeitstudent.restserverstandalone.servicea;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.github.teilzeitstudent.restserverstandalone.model.GreetingResponse;

public class GetProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        GreetingResponse response = new GreetingResponse();
        response.setAnswer("Hello, "+exchange.getIn().getHeader("name", String.class));
        exchange.getIn().setBody(response);
    }

}
