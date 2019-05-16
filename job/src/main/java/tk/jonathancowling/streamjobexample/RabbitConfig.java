package tk.jonathancowling.streamjobexample;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.config.AmqpOutboundChannelAdapterParser;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Configuration
@EnableIntegration
public class RabbitConfig {

    // sources
    // https://spring.io/guides/gs/messaging-rabbitmq/
    // https://docs.spring.io/spring-integration/reference/html/

    @Bean
    SimpleMessageListenerContainer errContainer(ConnectionFactory connectionFactory,
                                                AmqpOutboundEndpoint outboundEndpoint) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("err");
        container.setMessageListener(new MessageListenerAdapter());
        return container;
    }

    @Bean
    public MessageChannel amqpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel amqpErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundChannelAdapter inbound(@Qualifier("inContainer") SimpleMessageListenerContainer listenerContainer,
                                      @Qualifier("amqpInputChannel") MessageChannel channel,
                                      @Qualifier("amqpErrorChannel") MessageChannel errChannel) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
        adapter.setOutputChannel(channel);
        adapter.setErrorChannel(errChannel);
        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer inContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("in");
        container.setConcurrentConsumers(1);
        return container;
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpErrorChannel")
    public AmqpOutboundEndpoint outbound(AmqpTemplate template, TopicExchange topicExchange) {

        AmqpOutboundEndpoint endpoint = new AmqpOutboundEndpoint(template);
        endpoint.setExchangeName(topicExchange.getName());
        endpoint.setRoutingKey("key");
        return endpoint;
    }

    @Bean
    Queue queue() {
        return new Queue("err");
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange("errTopic");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange topic) {
        return BindingBuilder.bind(queue).to(topic).with("key");
    }
}
