package co.edu.unbosque.UserLoginBack.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import co.edu.unbosque.UserLoginBack.service.ExternalHTTPRequestHandler;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/map")
@Transactional
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:8082" })

public class GoogleMapsController {

	@Autowired
	private ExternalHTTPRequestHandler googleMapsService;

	@GetMapping("/map")
	public ResponseEntity<?> getMapByAddress(@RequestParam String address) {
		String mapImageUrl = googleMapsService.getMapForAddress(address);
		System.out.println(address);
		return ResponseEntity.ok(Map.of("mapa", mapImageUrl, "message", "Url correcta.",
				"success", true));
	}
	
	 @GetMapping("/geocode")
	    public ResponseEntity<String> getCoordinates(@RequestParam String address) {
	        JsonObject coordinates = googleMapsService.getCoordinatesFromAddress(address);
	        return ResponseEntity.ok(coordinates.toString());
	    }
}