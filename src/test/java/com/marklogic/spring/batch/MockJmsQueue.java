package com.marklogic.spring.batch;

import org.mockito.stubbing.Answer;

import javax.jms.Message;
import java.util.Stack;

public class MockJmsQueue extends Stack<Message> {

    private Stack<Answer<Message>> messages;

    public MockJmsQueue() {
        messages = new Stack<Answer<Message>>();
    }
}

