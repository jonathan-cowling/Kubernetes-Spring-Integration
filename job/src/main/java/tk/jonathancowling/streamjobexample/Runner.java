package tk.jonathancowling.streamjobexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@Component
public class Runner {

    private final CountDownLatch lock;

    private final ApplicationContext ctx;
    private int messagesToProcess;

    @Autowired
    public Runner(CountDownLatch lock, ApplicationContext ctx, int messagesToProcess) {
        this.lock = lock;
        this.ctx = ctx;
        this.messagesToProcess = messagesToProcess;
    }

    void run(String... args) throws Exception {
        Logger.getGlobal().info(String.format("Reading %d messages", messagesToProcess));
        lock.await();
        Logger.getGlobal().info("Finished reading messages");
        System.exit(SpringApplication.exit(ctx, () -> 0));
    }
}
