package tk.jonathancowling.streamjobexample;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageChannel amqpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel amqpErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer,
                                      @Qualifier("amqpInputChannel") MessageChannel channel//,
                                      // @Qualifier("amqpErrorChannel") MessageChannel errChannel
    ) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
        adapter.setOutputChannel(channel);
        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("testQ");
        container.setConcurrentConsumers(1);
        return container;
    }

//    old (non dsl) way of getting input from the channel
//    @Bean
//    @ServiceActivator(inputChannel = "amqpInputChannel")
//    public MessageHandler handler() {
//        return new AbstractReplyProducingMessageHandler() {
//
//            @Override
//            protected Object handleRequestMessage(Message<?> requestMessage) {
//                Logger.getGlobal().info("reply to " + requestMessage.getPayload());
//                lock.countDown();
//                return "reply to " + requestMessage.getPayload();
//            }
//
//        };
//    }
}
