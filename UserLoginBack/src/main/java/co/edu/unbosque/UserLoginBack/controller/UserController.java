package co.edu.unbosque.UserLoginBack.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.UserLoginBack.dto.UserDTO;
import co.edu.unbosque.UserLoginBack.service.UserService;
import co.edu.unbosque.UserLoginBack.util.AESUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081" })
@Transactional
@SecurityRequirement(name = "bearerAuth")
public class UserController {
	@Autowired
	private UserService userServ;

	public UserController() {
		// TODO Auto-generated constructor stub
	}

	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(@RequestParam Long id, @RequestBody UserDTO newUser) {

		int status = userServ.updateById(id, newUser);

		if (status == 0) {
			return new ResponseEntity<>("User updated successfully", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("New username already taken", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Error on update", HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(path = "/update")
	ResponseEntity<String> updateNew(@RequestParam long id, @RequestParam String newUsername,
			@RequestParam String newPassword) {
		UserDTO newUser = new UserDTO(newUsername, newPassword, null, null, null, null);

		int status = userServ.updateById(id, newUser);

		if (status == 0) {
			return new ResponseEntity<>("User updated successfully", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("New username already taken", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Error on update", HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = userServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(@PathVariable Long id) {
		boolean found = userServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/getbyid/{id}")
	ResponseEntity<UserDTO> getById(@PathVariable Long id) {
		UserDTO found = userServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> crearConJson(@RequestBody UserDTO nuevo) {

		if (!nuevo.getUser().matches("^[a-zA-Z0-9_.]+$")) {
			return new ResponseEntity<>(
					"Nombre de usuario inválido. Solo se permiten letras, números, guión bajo y punto.",
					HttpStatus.BAD_REQUEST);
		}
		if (!isPasswordSecure(nuevo.getPassword())) {
			return new ResponseEntity<>(
					"La contraseña no es segura. Debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.",
					HttpStatus.BAD_REQUEST);
		}

		UserDTO newUser = nuevo;

		String name = newUser.getName();
		String address = newUser.getAddress();

		if (!newUser.getUser().isBlank()) {

			newUser.setUser(AESUtil.hashingToSHA256(newUser.getUser()));

		}

		if (!newUser.getPassword().isBlank()) {
			newUser.setPassword(AESUtil.hashingToSHA256(newUser.getPassword()));
		}

		if (!newUser.getCedula().isBlank()) {

			newUser.setCedula(AESUtil.encrypt(newUser.getCedula()));
		}

		if (!name.isBlank()) {
			name = AESUtil.encrypt(name);
			newUser.setName(name);

		}
		if (!address.isBlank()) {

			address = AESUtil.encrypt(address);
			newUser.setAddress(address);
		}
		if (!newUser.getCoutry().isBlank()) {
			
			newUser.setCoutry(AESUtil.encrypt(newUser.getCoutry()));

		}

		int status = userServ.create(newUser);

		if (status == 0) {
			return new ResponseEntity<>("Usuario creado con éxito", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Error al crear el Usuario", HttpStatus.NOT_ACCEPTABLE);
		}

	}

	private boolean isPasswordSecure(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}
		boolean hasUpper = password.matches(".*[A-Z].*");
		boolean hasLower = password.matches(".*[a-z].*");
		boolean hasDigit = password.matches(".*\\d.*");
		boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|_].*");
		return hasUpper && hasLower && hasDigit && hasSpecial;
	}

	@GetMapping("/verifyPassword")
	public ResponseEntity<Boolean> verifyPassword(@RequestParam String password, @RequestParam String user) {
		String tUser = AESUtil.hashingToSHA256(user);
		String password1 = AESUtil.hashingToSHA256(password);
		ArrayList<UserDTO> users = userServ.findAll();

		for (UserDTO u : users) {

			if (password1.equals(u.getPassword())) {

				if (tUser.equals(u.getUser())) {

					return new ResponseEntity<>(true, HttpStatus.FOUND);

				}

			}
		}

		return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);

	}

	/*
	 * @GetMapping("/showAllEncrypted") public ResponseEntity<ArrayList<UserDTO>>
	 * showAllEncrypted() { ArrayList<UserDTO> users = userServ.findAll();
	 * 
	 * if (users.isEmpty()) { return new ResponseEntity<>(users,
	 * HttpStatus.NO_CONTENT); } else { return new ResponseEntity<>(users,
	 * HttpStatus.ACCEPTED); } }
	 */

	// @Hidden
	@GetMapping("/showAll")
	public ResponseEntity<ArrayList<UserDTO>> showAll() {
		ArrayList<UserDTO> users = userServ.findAll();

		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			ArrayList<UserDTO> decryptedUsers = new ArrayList<>();

			for (UserDTO user : users) {
				UserDTO decryptedUser = new UserDTO();
				decryptedUser.setId(user.getId());
				decryptedUser.setUser(user.getUser());
				decryptedUser.setPassword(user.getPassword());
				
				decryptedUser.setCedula(user.getCedula());

//				try {
//					decryptedUser.setName(AESUtil.decrypt(user.getName()));
//				} catch (Exception e) {
//					decryptedUser.setName(user.getName());
//				}
//				try {
//					decryptedUser.setGmail(AESUtil.decrypt(user.getGmail()));
//				} catch (Exception e) {
//					decryptedUser.setGmail(user.getGmail());
//				}
//				try {
//					decryptedUser.setCode(AESUtil.decrypt(user.getCode()));
//				} catch (Exception e) {
//					decryptedUser.setCode(user.getCode());
//				}

				decryptedUsers.add(decryptedUser);
			}

			return new ResponseEntity<>(decryptedUsers, HttpStatus.ACCEPTED);
		}
	}

	@DeleteMapping("/eliminarId/{id}")

	public ResponseEntity<String> deleteById(@PathVariable Long id) {

		int status = userServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado con exito", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar el usuario", HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/eliminar/user/{user}")
	public ResponseEntity<String> eliminarPorUser(@PathVariable String user) {

		if (!user.matches("^[a-zA-Z0-9_.]+$")) {
			return new ResponseEntity<>(
					"Nombre de usuario inválido. Solo se permiten letras, números, guión bajo y punto.",
					HttpStatus.BAD_REQUEST);
		}

		int estado = userServ.deleteByUser(user);
		return estado == 0 ? new ResponseEntity<>("Usuario eliminado con éxito", HttpStatus.OK)
				: new ResponseEntity<>("No encontrado", HttpStatus.NOT_FOUND);
	}

	@PutMapping("/actualizarjson")
	public ResponseEntity<String> actualizar(@RequestBody UserDTO nuevo) {

		UserDTO userUpdate = nuevo;

		String name = userUpdate.getName();
		String user = userUpdate.getUser();

		if (!userUpdate.getUser().isEmpty()) {
			if (!nuevo.getUser().matches("^[a-zA-Z0-9_.]+$")) {
				return new ResponseEntity<>(
						"Nombre de usuario inválido. Solo se permiten letras, números, guión bajo y punto.",
						HttpStatus.BAD_REQUEST);
			}
			userUpdate.setUser(AESUtil.hashingToSHA256(userUpdate.getUser()));

		}

		if (!userUpdate.getPassword().isEmpty()) {
			if (!isPasswordSecure(nuevo.getPassword())) {
				return new ResponseEntity<>(
						"La contraseña no es segura. Debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.",
						HttpStatus.BAD_REQUEST);
			}
			userUpdate.setPassword(AESUtil.hashingToSHA256(userUpdate.getPassword()));
		}
		if (!name.isEmpty()) {
			name = AESUtil.encrypt(name);
			userUpdate.setName(name);

		}

		if (!userUpdate.getCedula().isEmpty()) {

			userUpdate.setCedula(AESUtil.encrypt(userUpdate.getCedula()));
		}

		if (!user.isEmpty()) {

			user = AESUtil.encrypt(user);
			userUpdate.setUser(user);
		}

		int estado = userServ.update(userUpdate);
		if (estado == 0) {
			return new ResponseEntity<>("Usuario actualizado con éxito", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Error al actualizar el usuario", HttpStatus.NOT_FOUND);
		}
	}

}