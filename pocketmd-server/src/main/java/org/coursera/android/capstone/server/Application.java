package org.coursera.android.capstone.server;

import org.coursera.android.capstone.server.impl.data.PatientRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = PatientRepository.class)
@EnableTransactionManagement
@ComponentScan
@PropertySources({
	@PropertySource("classpath:config/app.config"),
	@PropertySource("classpath:config/datasource.config"),
	@PropertySource("classpath:config/hibernate.config")
})
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
