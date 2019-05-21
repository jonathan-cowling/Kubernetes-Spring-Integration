package tk.jonathancowling.streamjobexample;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Configuration
public class Flow {

    // sources
    // https://spring.io/guides/gs/messaging-rabbitmq/
    // https://docs.spring.io/spring-integration/reference/html/

    private final CountDownLatch lock;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate template;
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
    public Flow(CountDownLatch lock, ConnectionFactory connectionFactory, RabbitTemplate template, Queue in) {
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
                // simple transform using spring's auto converting
                .convert(String.class)
                // generic handler returns new payload (or message) from given payload and headers
                .<String>handle((str, headers) -> {

                    // demonstrating error handling
                    if (str.equalsIgnoreCase("error")) {
                        throw new RuntimeException("received error string");
                    }
                    return str;
                })
                // groups messages based on correlation strategy, then processes groups (default processing puts
                // groups in a collection)
                .aggregate(a -> a
                        .correlationStrategy(obj -> 0)
                        .releaseStrategy(obj -> obj.size() >= 3)
                        /*
                         * Aggregation Expiry:
                         *
                         * expiry results in a group being completely removed from the aggregators state,
                         * this means new messages with the same correlation strategy result start a new group (as
                         * opposed to being discarded), internally the aggregator expires groups after
                         * some time. Discarded messages may be sent to an alternate channel rather than
                         * just forgotten.
                         * Since correlation strategy always results in the same thing, we need to expire on completion
                         * and on timeout.
                         */
                        .groupTimeout(1000)
                        .expireGroupsUponTimeout(true)
                        .expireGroupsUponCompletion(true)
                        .discardChannel("nullChannel")
                        .sendPartialResultOnExpiry(true)
                )
                .<List<String>>log(LoggingHandler.Level.INFO, m -> m.getPayload().toString())
                .<List<String>>handle((payload, headers) -> {
                    payload.forEach(str -> {
                        Logger.getAnonymousLogger().info(String.format("%d Messages left ", lock.getCount() - 1));
                        lock.countDown();
                    });

                    // a handler returning null means stop message propagation,
                    // equivalent to `.channel("nullChannel")` or .nullChannel()
                    return null;
                })
                .get();
    }

    @Bean
    IntegrationFlow errorFlow() {
        return IntegrationFlows.from(ERR_CHANNEL)
                // new payload from old payload
                .transform(Throwable::getMessage)
                // copies message into new channel
                .wireTap(IntegrationFlows.from(new DirectChannel()).handle(Amqp
                        .outboundAdapter(template)
                        .exchangeName(exchangeName)
                        .routingKey(exchangeKey))
                        .get())
                .<String>log(LoggingHandler.Level.ERROR, Message::getPayload)
                .handle((p, h) -> {
                    Logger.getAnonymousLogger().info(String.format("%d Messages left ", lock.getCount() - 1));
                    lock.countDown();
                    return null;
                })
                .get();
    }
}
