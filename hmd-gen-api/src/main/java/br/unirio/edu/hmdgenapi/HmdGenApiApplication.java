package br.unirio.edu.hmdgenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {
		@Server(url = "/", description = "Default Server URL")
})
@SpringBootApplication
public class HmdGenApiApplication {

	@Bean
	@ConditionalOnProperty(value = "spring.cloud.gcp.firestore.emulator.enabled", havingValue = "true")
	public CredentialsProvider googleCredentials() {
		return NoCredentialsProvider.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(HmdGenApiApplication.class, args);
	}

}
