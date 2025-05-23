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
import co.edu.unbosque.UserLoginBack.model.User.Role;
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
	public int create(UserDTO data, String rol) {
		User entity = modelMapper.map(data, User.class);

		if (entity.getAddress().equals("") || entity.getUser().equals("") || entity.getPassword().equals("")
				|| entity.getName().equals("") || entity.getCedula().equals("") || entity.getCoutry() == null) {

			return 1;
		}

		if (!isValidCedula(entity.getCedula())) {
			throw new IllegalArgumentException("La cédula debe tener exacta y únicamente 10 caracteres numéricos.");
		}

		if (!isValidPassword(entity.getPassword())) {
			throw new IllegalArgumentException("La contraseña no cumple con el estándar "
					+ "(mínimo 8 caracteres, al menos una letra minúscula, al menos una letra mayúscula, "
					+ "al menos un número y al menos un símbolo que no sea < > :).");
		}

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
			if (rol.equals("ADMIN")) {
				entity.setRole(Role.ADMIN);
			} else if (rol.equals("USER")) {

				entity.setRole(Role.USER);
			}
			userRepo.save(entity);
			return 0;
		}
	}

	private boolean isValidCedula(String cedula) {
		if (cedula.length() != 10) {
			return false;
		}

		for (char ch : cedula.toCharArray()) {
			if (!Character.isDigit(ch)) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidPassword(String password) {
		if (password.length() < 8) {
			return false;
		}

		boolean hasLower = false;
		boolean hasUpper = false;
		boolean hasDigit = false;
		boolean hasValidSymbol = false;

		String disallowedSymbols = "<>&\"'/= ";

		for (char ch : password.toCharArray()) {
			if (Character.isLowerCase(ch)) {
				hasLower = true;
			} else if (Character.isUpperCase(ch)) {
				hasUpper = true;
			} else if (Character.isDigit(ch)) {
				hasDigit = true;
			} else if (disallowedSymbols.indexOf(ch) != -1) {
				return false;
			} else {
				hasValidSymbol = true;
			}
		}

		return hasLower && hasUpper && hasDigit && hasValidSymbol;
	}

	@Override
	public User encrypt(UserDTO data) {

		User entity = modelMapper.map(data, User.class);

		if (entity.getUser() != null) {
			entity.setUser(AESUtil.encrypt(entity.getUser()));

		}
		if (entity.getName() != null) {
			entity.setName(AESUtil.encrypt(entity.getName()));

		}
		if (entity.getCedula() != null) {
			entity.setCedula(AESUtil.encrypt(entity.getCedula()));

		}
		if (entity.getCoutry() != null) {
			entity.setCoutry(AESUtil.encrypt(entity.getCoutry()));

		}
		if (entity.getAddress() != null) {
			entity.setAddress(AESUtil.encrypt(entity.getAddress()));

		}

		return entity;
	}

	@Override
	public String decrypt(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		if (entity.getUser() != null) {
			entity.setUser(AESUtil.decrypt(entity.getUser()));

		}
		if (entity.getName() != null) {
			entity.setName(AESUtil.decrypt(entity.getName()));

		}
		if (entity.getCedula() != null) {
			entity.setCedula(AESUtil.decrypt(entity.getCedula()));

		}
		if (entity.getCoutry() != null) {
			entity.setCoutry(AESUtil.decrypt(entity.getCoutry()));

		}
		if (entity.getAddress() != null) {
			entity.setAddress(AESUtil.decrypt(entity.getAddress()));

		}
		return null;
	}

	@Override
	public List<UserDTO> getAll() {
		List<User> entityList = userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			UserDTO dto = modelMapper.map(entity, UserDTO.class);

			if (dto.getAddress() != null) {
				dto.setAddress(AESUtil.decrypt(dto.getAddress()));
			}

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
	public int updateUserAndPassword(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		Optional<User> newFound = userRepo.findByUser(newData.getUser());

		if (found.isPresent() && !newFound.isPresent()) {
			User temp = found.get();
			if (temp.getUser() != null) {
				temp.setUser(AESUtil.encrypt(newData.getUser()));
			}
			if (temp.getPassword() != null) {
				temp.setPassword(passwordEncoder.encode(newData.getPassword()));
			}
			/*
			 * if (temp.getAddress() != null) { temp.setAddress(newData.getAddress()); } if
			 * (temp.getCedula() != null) { temp.setCedula(newData.getCedula()); } if
			 * (temp.getCoutry() != null) { temp.setCoutry(newData.getCoutry()); } if
			 * (temp.getImage() != null) { temp.setImage(newData.getImage()); } if
			 * (temp.getName() != null) { temp.setName(newData.getName()); }
			 */
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
	public int updateImage(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		Optional<User> newFound = userRepo.findByImage(newData.getImage());

		if (found.isPresent() && !newFound.isPresent()) {
			User temp = found.get();

			if (temp.getImage() != null) {
				temp.setImage(newData.getImage());
			}

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

	@Override
	public int updateById(Long id, UserDTO newData) {
// TODO Auto-generated method stub
		return 0;
	}

}
