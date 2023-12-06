package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;

import javax.jms.*;

public class TopicSubscriber implements Suscriber {

    private Connection connection;
    private Session session;
    private final EventStoreBuilder fileEventStoreBuilder;

    public TopicSubscriber(FileEventStoreBuilder fileEventStoreBuilder) {
        this.fileEventStoreBuilder = fileEventStoreBuilder;
    }

    @Override
    public void start() throws EventReceiverException {
        try {
            initializeConnection();
            initializeSession();
            initializeMessageListener();
        } catch (JMSException e) {
            throw new EventReceiverException("Error creating listener", e);
        }
    }

    private void initializeConnection() throws JMSException {
        String brokerUrl = "tcp://localhost:61616";
        String clientId = "EventStoreBuilder";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);
        connection.start();
    }

    private void initializeSession() throws JMSException {
        boolean transacted = false;
        int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
        session = connection.createSession(transacted, acknowledgeMode);
    }

    private void initializeMessageListener() throws JMSException {
        String topicName = "prediction.Weather";
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = createDurableSubscriber(topic, "EventStoreBuilder-prediction.Weather");
        consumer.setMessageListener(createMessageListener());
    }

    private MessageConsumer createDurableSubscriber(Topic topic, String subscriptionName) throws JMSException {
        return session.createDurableSubscriber(topic, subscriptionName);
    }

    private MessageListener createMessageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                processMessage(message);
            }
        };
    }

    private void processMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                saveAndPrintMessage((TextMessage) message);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAndPrintMessage(TextMessage textMessage) throws JMSException {
        String receivedMessage = textMessage.getText();
        fileEventStoreBuilder.save(receivedMessage);
        System.out.println("Received message '" + receivedMessage + "'");
    }
}
