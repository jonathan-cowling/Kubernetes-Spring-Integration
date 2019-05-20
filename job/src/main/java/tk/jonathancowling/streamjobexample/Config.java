package tk.jonathancowling.streamjobexample;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import java.util.Objects;

@Configuration
public class Config {

    private Queue in;
    private int messagesToProcess;

    @Value("${job.queue.in}")
    private String inQueueName;

    @Value("${job.queue.err}")
    private String errQueueName;

    @Value("${job.exchange.name}")
    private String outExchangeName;

    @Value("${job.exchange.key}")
    private String outExchangeKey;

    private final RabbitTemplate template;


    @Autowired
    Config(RabbitTemplate template) {
        this.template = template;
    }

    @PostConstruct
    void init() {

        // source: https://stackoverflow.com/questions/11446443/queue-size-in-spring-amqp-java-client

        DeclareOk declare = Objects
                .requireNonNull(template.execute(channel -> channel.queueDeclarePassive(inQueueName)));

        Objects.requireNonNull(template.execute(channel -> channel.queueDeclarePassive(errQueueName)));

        Objects.requireNonNull(template.execute(channel -> channel.exchangeDeclarePassive(outExchangeName)));

        messagesToProcess = declare.getMessageCount();
        in = new Queue(inQueueName);
    }

    @Bean
    int getMessagesToProcess() {
        return messagesToProcess;
    }

    @Bean
    Queue getIn() {
        return in;
    }
}
