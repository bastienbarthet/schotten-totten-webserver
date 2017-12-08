package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RestGameServer {
	
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(RestGameServer.class, args);
	}
	
    public static boolean isActive() {
        return (context != null && context.isActive());
    }

    public static void stop() {
        SpringApplication.exit(context);
    }

}
