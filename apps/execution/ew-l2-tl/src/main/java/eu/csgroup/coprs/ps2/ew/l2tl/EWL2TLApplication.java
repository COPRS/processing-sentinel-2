package eu.csgroup.coprs.ps2.ew.l2tl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class EWL2TLApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWL2TLApplication.class, args);
	}

}
