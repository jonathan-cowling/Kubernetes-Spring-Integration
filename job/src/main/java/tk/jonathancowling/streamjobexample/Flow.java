package tk.jonathancowling.streamjobexample;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Configuration
public class Flow {

    // sources
    // https://spring.io/guides/gs/messaging-rabbitmq/
    // https://docs.spring.io/spring-integration/reference/html/

    private final CountDownLatch lock;
    private final ConnectionFactory connectionFactory;
    private final AmqpTemplate template;
    private final Queue in;

    private static final String ERR_CHANNEL = "amqpErrorChannel";

    @Bean(ERR_CHANNEL)
    public MessageChannel amqpErrorChannel() {
        return new DirectChannel();
    }

    @Value("${job.exchange.name}")
    private String exchangeName;

    @Value("${job.exchange.key}")
    private String exchangeKey;

    @Autowired
    public Flow(CountDownLatch lock, ConnectionFactory connectionFactory, AmqpTemplate template, Queue in) {
        this.lock = lock;
        this.connectionFactory = connectionFactory;
        this.template = template;
        this.in = in;
    }

    @Bean
    IntegrationFlow mainFlow() {
        return IntegrationFlows.from(Amqp
                .inboundAdapter(connectionFactory, in)
                .errorChannel(ERR_CHANNEL)
        )
                .convert(String.class)
                .aggregate(a -> a
                        .correlationStrategy(obj -> 0)
                        .releaseStrategy(obj -> obj.size() >= 3)
                        .groupTimeout(1000)
                        .sendPartialResultOnExpiry(true)
                )
                .log(LoggingHandler.Level.INFO)
                .<List<String>>handle((payload, headers) -> {
                    lock.countDown();
                    // a handler returning null means stop message propagation,
                    // equivalent to `.channel("nullChannel")` or .nullChannel()
                    return null;
                })
                .get();
    }

    @Bean
    IntegrationFlow errorFlow() {
        return IntegrationFlows.from(ERR_CHANNEL)
                .transform(Throwable::getMessage)
                .log(LoggingHandler.Level.ERROR)
                .handle(Amqp
                        .outboundAdapter(template)
                        .exchangeName(exchangeName)
                        .routingKey(exchangeKey))
                .handle((p, h) -> {
                    lock.countDown();
                    return null;
                })
                .get();
    }
}
