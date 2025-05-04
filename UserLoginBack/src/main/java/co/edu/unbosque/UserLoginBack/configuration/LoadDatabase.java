package co.edu.unbosque.UserLoginBack.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.UserLoginBack.model.User;
import co.edu.unbosque.UserLoginBack.model.User.Role;
import co.edu.unbosque.UserLoginBack.repository.UserRepository;

@Configuration
public class LoadDatabase {
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEndcoder) {

		return args -> {
			Optional<User> found = userRepo.findByUser("admin");
			if (found.isPresent()) {
				log.info("Admin already exists,  skipping admin creating  ...");
			} else {

				User user = new User("admin", passwordEndcoder.encode("1234567890"), null, null, null, null);
				user.setRole(Role.ADMIN);

				userRepo.save(user);
				log.info("Preloading admin user");
			}
		};
	}

}