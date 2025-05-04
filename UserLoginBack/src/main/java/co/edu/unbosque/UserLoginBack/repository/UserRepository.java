package co.edu.unbosque.UserLoginBack.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.UserLoginBack.model.User;

public interface UserRepository  extends CrudRepository<User, Long>{
	
	public Optional<User> findByUser(String user);

	public void deleteByUser(String user);
	
	Optional<User> findByCedula(String cedula);
}
