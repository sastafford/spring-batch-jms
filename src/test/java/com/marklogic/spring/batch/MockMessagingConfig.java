package com.marklogic.spring.batch;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.LinkedList;
import java.util.UUID;

@Configuration
public class MockMessagingConfig {

    @Mock
    private JmsTemplate mockJmsTemplate;

    @Mock
    private Session mockSession;

    @Mock
    private TextMessage mockTextMessage;

    public MockMessagingConfig() throws JMSException {
        MockitoAnnotations.initMocks(this);
        when(mockSession.createTextMessage()).thenReturn(mockTextMessage);
        when(mockTextMessage.getText()).thenAnswer(
                new Answer() {

                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return UUID.randomUUID().toString();
                    }
                });
    }

    @Bean
    public LinkedList<Message> messages() throws JMSException {
        LinkedList<Message> messages = new LinkedList<Message>();
        for (int i = 0; i < 5; i++) {
            TextMessage msg = mockSession.createTextMessage();
            messages.push(msg);
        }
        return messages;
    }

    @Bean
    public JmsTemplate jmsTemplate(LinkedList<Message> messages) throws JMSException {
        //Make sure last message is null so that JmsItemReader knows where to stop
        if (messages.peekLast() != null) {
            messages.addLast(null);
        };

        when(mockJmsTemplate.getMessageConverter()).thenReturn(new SimpleMessageConverter());
        when(mockJmsTemplate.receiveSelected(anyString())).thenAnswer(new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocation) throws Throwable {
                return messages.pop();
            }
        });
        when(mockJmsTemplate.receive()).thenAnswer(new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocation) throws Throwable {
                return messages.pop();
            }
        });
        when(mockJmsTemplate.receiveAndConvert()).thenAnswer(new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocation) throws Throwable {
                return messages.pop();
            }
        });
        when(mockJmsTemplate.getReceiveTimeout()).thenReturn(5L);
        when(mockJmsTemplate.getDefaultDestinationName()).thenReturn("mock-jms");
        return mockJmsTemplate;
    }

}
