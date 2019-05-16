package tk.jonathancowling.streamjobexample;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Configuration
public class Flow {

    private final CountDownLatch lock;
    private final ConnectionFactory connectionFactory;
    private final AmqpTemplate template;

    @Autowired
    public Flow(CountDownLatch lock, ConnectionFactory connectionFactory, AmqpTemplate template) {
        this.lock = lock;
        this.connectionFactory = connectionFactory;
        this.template = template;
    }

    @Bean
    IntegrationFlow mainFlow() {
        return IntegrationFlows.from("amqpInputChannel"
               /* Amqp.inboundAdapter(connectionFactory, new Queue("in"))
                        .errorChannel(Amqp.channel("err", connectionFactory).get())*/
        )
                .aggregate(/*a -> a.correlationStrategy(obj -> 0).releaseStrategy(obj -> obj.size() >= 3)*/)
                .convert(List.class)
                .log(LoggingHandler.Level.INFO)
                .<List<String>>handle((payload, headers) -> {
                    lock.countDown();
                    return null; // if null channel not needed
                })
                .channel("nullChannel")
                .get();
    }

//    @Bean
//    IntegrationFlow errorFlow() {
//        return IntegrationFlows.from("amqpErrorChannel")
//                .handle(Amqp
//                        .outboundAdapter(template)
//                        .exchangeName("errTopic")
//                        .routingKey("key"))
//                .get();
//    }
}
