package co.edu.unbosque.UserLoginBack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.UserLoginBack.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByUser(String user);

	public void deleteByUser(String user);

	public Optional<User> findByCedula(String cedula);

	public Optional<User> findByImage(String image);
}
