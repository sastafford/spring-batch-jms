package com.marklogic.spring.batch;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
/*
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
*/
@Component
public class Listener {

/*
   @JmsListener(destination = "inbound.queue")
    public void receiveMessage(final Message jsonMessage) throws JMSException {
        String messageData = null;
        System.out.println("Received message " + jsonMessage);
        if(jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
            System.out.println(messageData.toString());
        }
    }
*/
}