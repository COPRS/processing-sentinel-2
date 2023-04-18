package eu.csgroup.coprs.ps2.ew.l2ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class EWL2DSApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWL2DSApplication.class, args);
	}

}
