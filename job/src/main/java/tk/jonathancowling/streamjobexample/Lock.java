package tk.jonathancowling.streamjobexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class Lock {

    private final int messagesToProcess;

    @Autowired
    public Lock(int messagesToProcess) {
        this.messagesToProcess = messagesToProcess;
    }

    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(messagesToProcess);
    }
}
