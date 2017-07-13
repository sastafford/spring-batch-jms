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
import java.security.SecureClassLoader;
import java.util.Stack;
import java.util.UUID;

@Configuration
public class MockMessagingConfig {

    @Mock
    private JmsTemplate mockJmsTemplate;

    @Mock
    private Session mockSession;

    @Mock
    private TextMessage mockTextMessage;

    private Stack<Message> messages = new Stack<Message>();

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
        //need to push null onto the stack to let ItemReader know it's empty
        messages.push(null);
        for (int i = 0; i < 5; i++) {
            TextMessage msg = mockSession.createTextMessage();
            messages.push(msg);
        }



    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
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
