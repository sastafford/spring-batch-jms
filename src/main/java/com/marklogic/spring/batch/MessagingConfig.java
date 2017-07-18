package com.marklogic.spring.batch;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class MessagingConfig {

    String BROKER_URL = "tcp://oscar:61616";
    String BROKER_USERNAME = "admin";
    String BROKER_PASSWORD = "admin";


    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_USERNAME);
        connectionFactory.setUserName(BROKER_PASSWORD);

        SingleConnectionFactory connFactory = new SingleConnectionFactory(connectionFactory);
        connFactory.setReconnectOnException(true);

        CachingConnectionFactory cacheConnectionFactory = new CachingConnectionFactory(connFactory);
        return cacheConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory){
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName("home");
        template.setReceiveTimeout(5000);
        template.setSessionTransacted(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionTransacted(true);
        return factory;
    }
}
