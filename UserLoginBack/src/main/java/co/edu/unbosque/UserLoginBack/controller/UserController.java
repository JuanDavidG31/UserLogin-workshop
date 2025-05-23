package co.edu.unbosque.UserLoginBack.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.UserLoginBack.dto.UserDTO;
import co.edu.unbosque.UserLoginBack.service.UserService;
import co.edu.unbosque.UserLoginBack.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.swagger.v3.oas.annotations.media.Content;

/**
 * Controlador REST para la gestión de usuarios. Proporciona endpoints para
 * crear, leer, actualizar y eliminar usuarios. Requiere autenticación JWT para
 * todos los endpoints. Los endpoints /getall, /count, /exists/*, /getbyid/* son
 * accesibles para usuarios con ROLE_USER o ROLE_ADMIN. Todos los demás
 * endpoints requieren ROLE_ADMIN.
 *
 * @author Universidad El Bosque
 * @version 0.1
 */
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

	@PutMapping(value = "/actualizar-foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> actualizarFotoPerfil(@RequestParam long id,
			@Parameter(description = "Nueva foto de perfil", required = true, name = "archivo", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestParam("archivo") MultipartFile archivo) {

		System.out.println(id);
		System.out.println(archivo);

		if (archivo.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("message", "Por favor, selecciona una foto de perfil para subir.", "success", false));
		}

		try {
			String home = System.getProperty("user.home");
			Path carpetaDestinoPath = Paths.get(home, "archivos-subidos");
			if (!Files.exists(carpetaDestinoPath)) {
				Files.createDirectories(carpetaDestinoPath);
			}

			String nombreOriginal = archivo.getOriginalFilename();
			String extension = "";
			int puntoIndex = nombreOriginal.lastIndexOf('.');
			if (puntoIndex > 0 && puntoIndex < nombreOriginal.length() - 1) {
				extension = nombreOriginal.substring(puntoIndex);
			}
			String nombreAleatorio = UUID.randomUUID().toString() + extension;
			Path archivoDestinoPath = carpetaDestinoPath.resolve(nombreAleatorio);

			Files.copy(archivo.getInputStream(), archivoDestinoPath);

			UserDTO userDTO = new UserDTO();
			userDTO.setImage(nombreAleatorio);
			int resultado = userServ.updateImage(id, userDTO);

			if (resultado == 0) {
				return ResponseEntity.ok(Map.of("nombreArchivo", nombreAleatorio, "message",
						"Foto de perfil actualizada exitosamente.", "success", true));
			} else if (resultado == 2) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("message", "No se encontró el usuario con ID: " + id, "success", false));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
						Map.of("message", "Error al actualizar la foto de perfil del usuario.", "success", false));
			}

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error al subir la foto de perfil: " + e.getMessage(), "success", false));
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
	ResponseEntity<?> updateNew(@RequestParam long id, @RequestParam String newUsername,
			@RequestParam String newPassword) {
		UserDTO newUser = new UserDTO(newUsername, newPassword, null, null, null, null, null);
		int status = userServ.updateUserAndPassword(id, newUser);

		if (status == 0) {

			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message", "User registered successfully", "success", true));
		} else if (status == 1) {
			return ResponseEntity.status(HttpStatus.IM_USED)
					.body(Map.of("message", "New username already taken", "success", false));
		} else if (status == 2) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "User not found", "success", false));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "Error on update", "success", false));
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