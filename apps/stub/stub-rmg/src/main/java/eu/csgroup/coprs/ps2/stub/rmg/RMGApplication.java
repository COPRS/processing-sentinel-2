package eu.csgroup.coprs.ps2.stub.rmg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class RMGApplication {

	public static void main(String[] args) {
		SpringApplication.run(RMGApplication.class, args);
	}

}
