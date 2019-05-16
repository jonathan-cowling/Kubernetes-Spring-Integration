package tk.jonathancowling.streamjobexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class Runner {

    private final CountDownLatch lock;

    private final ApplicationContext ctx;

    @Autowired
    public Runner(CountDownLatch lock, ApplicationContext ctx) {
        this.lock = lock;
        this.ctx = ctx;
    }

    void run(String... args) throws Exception {
        Logger.getGlobal().info(String.format("Hello World (%d)", lock.getCount()));
        lock.await(1, TimeUnit.MINUTES);
        Logger.getGlobal().info("FINISHED");
        System.exit(SpringApplication.exit(ctx, () -> 0));
    }
}
