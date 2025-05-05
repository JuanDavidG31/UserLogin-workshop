package co.edu.unbosque.UserLoginBack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.UserLoginBack.dto.UserDTO;
import co.edu.unbosque.UserLoginBack.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:8082" })
@Transactional
@Tag(name = "User Management", description = "Endpoints for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
	@Autowired
	private UserService userServ;

	public UserController() {
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

	@GetMapping("/getbyuser/{user}")
	ResponseEntity<UserDTO> getByUser(@PathVariable String user) {
		UserDTO found = userServ.getByUser(user);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/showAllEncrypted")
	public ResponseEntity<List<UserDTO>> showAllEncrypted() {
		List<UserDTO> users = userServ.getAll();

		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
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

	@DeleteMapping("/deletebyuser")
	ResponseEntity<String> deleteById(@RequestParam String name) {
		int status = userServ.deleteByUsername(name);
		if (status == 0) {
			return new ResponseEntity<>("User deleted successfully", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error on delete", HttpStatus.NOT_FOUND);
		}
	}

}