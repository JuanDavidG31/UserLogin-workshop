package co.edu.unbosque.UserLoginBack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import co.edu.unbosque.UserLoginBack.service.ExternalHTTPRequestHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/map")
@Transactional
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:8082" })
@SecurityRequirement(name = "bearerAuth")
public class GoogleMapsController {

	@Autowired
	private ExternalHTTPRequestHandler googleMapsService;

	@GetMapping("/map")
	public ResponseEntity<String> getMapByAddress(@RequestParam String address) {
		String mapImageUrl = googleMapsService.getMapForAddress(address);
		return ResponseEntity.ok(mapImageUrl);
	}
	
	 @GetMapping("/geocode")
	    public ResponseEntity<String> getCoordinates(@RequestParam String address) {
	        JsonObject coordinates = googleMapsService.getCoordinatesFromAddress(address);
	        return ResponseEntity.ok(coordinates.toString());
	    }
}