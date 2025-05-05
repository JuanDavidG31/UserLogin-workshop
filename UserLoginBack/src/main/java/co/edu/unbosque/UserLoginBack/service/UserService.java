package co.edu.unbosque.UserLoginBack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.UserLoginBack.dto.UserDTO;
import co.edu.unbosque.UserLoginBack.model.User;
import co.edu.unbosque.UserLoginBack.repository.UserRepository;
import co.edu.unbosque.UserLoginBack.util.AESUtil;

@Service
public class UserService implements CRUDOperation<UserDTO, User> {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserService() {

	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id) ? true : false;
	}

	@Override
	@Transactional
	public int create(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		if (findUsernameAlreadyTaken(entity)) {
			return 1;
		} else {
			User tEntity = encrypt(data);
			entity.setName(tEntity.getName());
			entity.setCedula(tEntity.getCedula());
			entity.setCoutry(tEntity.getCoutry());
			entity.setAddress(tEntity.getAddress());
			entity.setUser(tEntity.getUser());
			entity.setPassword(passwordEncoder.encode(entity.getPassword()));

			userRepo.save(entity);
			return 0;
		}
	}

	@Override
	public User encrypt(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		entity.setName(AESUtil.encrypt(entity.getName()));
		entity.setCedula(AESUtil.encrypt(entity.getCedula()));
		entity.setCoutry(AESUtil.encrypt(entity.getCoutry()));
		entity.setAddress(AESUtil.encrypt(entity.getAddress()));
		entity.setUser(AESUtil.encrypt(entity.getUser()));
		return entity;
	}

	@Override
	public String decrypt(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		entity.setName(AESUtil.decrypt(entity.getName()));
		entity.setCedula(AESUtil.decrypt(entity.getCedula()));
		entity.setCoutry(AESUtil.decrypt(entity.getCoutry()));
		entity.setAddress(AESUtil.decrypt(entity.getAddress()));
		entity.setUser(AESUtil.decrypt(entity.getUser()));
		return null;
	}

	@Override
	public List<UserDTO> getAll() {
		List<User> entityList = userRepo.findAll();
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

	public int deleteByUsername(String username) {
		Optional<User> found = userRepo.findByUser(username);
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

	public UserDTO getById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), UserDTO.class);
		} else {
			return null;
		}
	}

	public UserDTO getByUser(String user) {
		Optional<User> found = userRepo.findByUser(user);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), UserDTO.class);
		} else {
			return null;
		}
	}

	public boolean findUsernameAlreadyTaken(User newUser) {
		Optional<User> found = userRepo.findByUser(newUser.getUsername());
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

}
