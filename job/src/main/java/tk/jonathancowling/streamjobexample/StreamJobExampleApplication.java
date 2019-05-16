package tk.jonathancowling.streamjobexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class StreamJobExampleApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(StreamJobExampleApplication.class, args);
		ctx.getBean(Runner.class).run(args);
	}

}
