package eu.csgroup.coprs.ps2.pw.l0u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class PWL0UApplication {

	public static void main(String[] args) {
		SpringApplication.run(PWL0UApplication.class, args);
	}

}
