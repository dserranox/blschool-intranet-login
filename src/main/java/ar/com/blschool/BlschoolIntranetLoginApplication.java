package ar.com.blschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("ar.com.blschool.repository")
@EntityScan("ar.com.blschool.entity")
public class BlschoolIntranetLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlschoolIntranetLoginApplication.class, args);
	}

}
