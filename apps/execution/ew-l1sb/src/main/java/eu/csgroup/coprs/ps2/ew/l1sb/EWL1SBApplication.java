package eu.csgroup.coprs.ps2.ew.l1sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class EWL1SBApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWL1SBApplication.class, args);
	}

}
