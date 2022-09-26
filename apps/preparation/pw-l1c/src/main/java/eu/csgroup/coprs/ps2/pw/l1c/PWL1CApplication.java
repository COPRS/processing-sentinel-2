package eu.csgroup.coprs.ps2.pw.l1c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class PWL1CApplication {

	public static void main(String[] args) {
		SpringApplication.run(PWL1CApplication.class, args);
	}

}
