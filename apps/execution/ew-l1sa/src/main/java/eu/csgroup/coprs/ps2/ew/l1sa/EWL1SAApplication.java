package eu.csgroup.coprs.ps2.ew.l1sa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class EWL1SAApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWL1SAApplication.class, args);
	}

}
