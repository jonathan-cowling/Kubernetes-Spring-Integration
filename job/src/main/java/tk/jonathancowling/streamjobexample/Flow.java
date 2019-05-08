package tk.jonathancowling.streamjobexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;

import java.util.concurrent.CountDownLatch;

@Configuration
public class Flow {

    private final CountDownLatch lock;

    @Autowired
    public Flow(CountDownLatch lock) {
        this.lock = lock;
    }

    @Bean
    IntegrationFlow integrationFlow() {
        return IntegrationFlows.from("amqpInputChannel")
                .convert(String.class)
                //.aggregate() not working currently
                .log(LoggingHandler.Level.INFO)
                .<String>handle((payload, headers) -> {
                    lock.countDown();
                    return true; // if null channel not needed
                })
                .channel("nullChannel")
                .get();
    }
}
