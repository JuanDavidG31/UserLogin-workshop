package co.edu.unbosque.UserLoginBack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.UserLoginBack.dto.UserDTO;
import co.edu.unbosque.UserLoginBack.model.User;
import co.edu.unbosque.UserLoginBack.repository.CRUDOperation;
import co.edu.unbosque.UserLoginBack.repository.UserRepository;

public class UserService implements CRUDOperation<UserDTO> {
@Autowired
private UserRepository userRepo;

@Autowired
private ModelMapper modelMapper;

@Autowired
private PasswordEncoder passwordEncoder;
	
	
	@Override
	public int create(UserDTO data) {
	User entity = modelMapper.map(data, User.class);
	if (findUsernameAlreadyTaken(entity)) {
		return 1;
	} else {
		// Hash the password before saving
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		entity.setUser(passwordEncoder.encode(entity.getUser()));
		userRepo.save(entity);
		return 0;
	}
}

	@Override
	public List<UserDTO> getAll() {
		List<User> entityList = (List<User>) userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {

			UserDTO dto = modelMapper.map(entity, UserDTO.class);
			dtoList.add(dto);
		});

		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {

			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		Optional<User> newFound = userRepo.findByUser(newData.getUser());

		if (found.isPresent() && !newFound.isPresent()) {
			User temp = found.get();
			temp.setUser(newData.getUser());
			// Hash the password before saving
			temp.setPassword(passwordEncoder.encode(newData.getPassword()));
			userRepo.save(temp);
			return 0;
		}
		if (found.isPresent() && newFound.isPresent()) {
			return 1;
		}
		if (!found.isPresent()) {
			return 2;
		} else {
			return 3;
		}
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id) ? true : false;
	}
	public boolean findUsernameAlreadyTaken(User newUser) {
		Optional<User> found = userRepo.findByUser(newUser.getUser());
		if (found.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean findUsernameAlreadyTaken(String username) {
		Optional<User> found = userRepo.findByUser(username);
		return found.isPresent();
	}

	public int validateCredentials(String username, String password) {
		// Find user by username
		Optional<User> userOpt = userRepo.findByUser(username);

		// Check if user exists and password matches
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword())) {
				return 0; // Success
			}
		}

		return 1; // Invalid credentials
	}
	public int deleteByUser(String user) {
		Optional<User> found = userRepo.findByUser(user);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}
	public UserDTO getById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), UserDTO.class);
		} else {
			return null;
		}
	}
	public int update(UserDTO data) {
		Optional<User> existingUser = userRepo.findByCedula(data.getCedula());

		if (existingUser.isPresent()) {
			User entity = existingUser.get();

			if (data.getName() != null && !data.getName().isEmpty()) {
				entity.setName(data.getName());
			}

			if (data.getPassword() != null && !data.getPassword().isEmpty()) {
				entity.setPassword(data.getPassword());
			}
			if (data.getUser() != null && !data.getUser().isEmpty()) {
				entity.setUser(data.getUser());
			}
			if (data.getCedula()!= null && !data.getUser().isEmpty()) {
				entity.setCedula(data.getCedula());
			}
			if (data.getCoutry() != null && !data.getCoutry().isEmpty()) {
				entity.setCoutry(data.getCoutry());
			}
			if (data.getAddress() != null && !data.getAddress().isEmpty()) {
				entity.setAddress(data.getAddress());
			}
			
			try {
				userRepo.save(entity);
				return 0;
			} catch (Exception e) {
				return 1;
			}
		} else {
			return 1;
		}
	}

	public UserRepository getUserRepo() {
		return userRepo;
	}

	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	public ModelMapper getModelMapper() {
		return modelMapper;
	}

	public void setModelMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

		public ArrayList<UserDTO> findAll() {
		ArrayList<User> entityList = (ArrayList<User>) userRepo.findAll();
		ArrayList<UserDTO> dtoList = new ArrayList<>();

		entityList.forEach((entity) -> {

			UserDTO dto = modelMapper.map(entity, UserDTO.class);
			dtoList.add(dto);

		});

		return dtoList;
	}

}
