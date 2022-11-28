package eu.csgroup.coprs.ps2.pw.l2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("eu.csgroup.coprs.ps2")
@SpringBootApplication
public class PWL2Application {

	public static void main(String[] args) {
		SpringApplication.run(PWL2Application.class, args);
	}

}
