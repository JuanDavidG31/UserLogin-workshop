package co.edu.unbosque.UserLoginBack;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserLoginBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserLoginBackApplication.class, args);
	}
	
	@Bean
    ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
