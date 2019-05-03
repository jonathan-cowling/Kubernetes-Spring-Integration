package tk.jonathancowling.streamjobexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class Lock {
    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(1);
    }
}
