package co.edu.unbosque.UserLoginBack.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class ExternalHTTPRequestHandler {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)).build();

    
    private String googleMapsApiKey="AIzaSyDqVEBOuW5eMA2WSUHPFBqOD5rPR9hFDhs";

    private static final String GEOCODING_API_URL_BASE = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String STATIC_MAPS_API_URL_BASE = "https://maps.googleapis.com/maps/api/staticmap";

    public JsonObject getCoordinatesFromAddress(String text) {
    	try {
            String encodedAddress = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String geocodingUrl = GEOCODING_API_URL_BASE + "?address=" + encodedAddress + "&key=" + googleMapsApiKey;
            String geocodingResponse = doGetAndParse(geocodingUrl);
            return JsonParser.parseString(geocodingResponse).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar la excepción apropiadamente
            return null;
        }
    }

    public String getStaticMapImageUrl(double latitude, double longitude) {
        String staticMapUrl = STATIC_MAPS_API_URL_BASE + "?center=" + String.format("%f,%f", latitude, longitude) +
                              "&zoom=15" +
                              "&size=600x400" +
                              "&markers=color:red|label:A|" + String.format("%f,%f", latitude, longitude) +
                              "&key=" + googleMapsApiKey;
        return staticMapUrl;
    }

    public String getMapForAddress(String address) {
        JsonObject geocodingResponseJson = getCoordinatesFromAddress(address);

        if (geocodingResponseJson != null && geocodingResponseJson.has("results") && geocodingResponseJson.getAsJsonArray("results").size() > 0) {
            JsonObject firstResult = geocodingResponseJson.getAsJsonArray("results").get(0).getAsJsonObject();
            JsonObject location = firstResult.getAsJsonObject("geometry").getAsJsonObject("location");
            double latitude = location.get("lat").getAsDouble();
            double longitude = location.get("lng").getAsDouble();

            return getStaticMapImageUrl(latitude, longitude);
        } else {
            return "Error: No se pudieron obtener las coordenadas para la dirección proporcionada.";
        }
    }

    public String doGetAndParse(String url) { // Ahora no es estático
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                .header("Content-type", "application/json").build();

        HttpResponse<String> response = null;

        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("status code -> " + response.statusCode());
        String uglyJson = response.body();
        return prettyPrintUsingGson(uglyJson);
    }

    public String prettyPrintUsingGson(String uglyJson) { // Tampoco es estático
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJson);
        String prettyJsonString = gson.toJson(jsonElement);
        return prettyJsonString;
    }
}