package eu.csgroup.coprs.ps2.ew.l0u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class EWL0UApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWL0UApplication.class, args);
	}

}
