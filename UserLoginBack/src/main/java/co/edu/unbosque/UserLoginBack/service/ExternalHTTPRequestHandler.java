
package co.edu.unbosque.UserLoginBack.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ExternalHTTPRequestHandler {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	public static String doGetAndParse(String url) {
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

	public static String prettyPrintUsingGson(String uglyJson) {
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(uglyJson);
		String prettyJsonString = gson.toJson(jsonElement);
		return prettyJsonString;

	}

	public static String toPostFileAndConvertToDTOVirus(String url, String apiKey, File file) {
		String boundary = "----JavaMultipartBoundary" + System.currentTimeMillis();
		HttpResponse<String> response = null;

		try {
			var byteStream = new ByteArrayOutputStream();
			var writer = new PrintWriter(new OutputStreamWriter(byteStream, StandardCharsets.UTF_8), true);

			writer.append("--").append(boundary).append("\r\n");
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName())
					.append("\"\r\n");
			writer.append("Content-Type: application/octet-stream\r\n\r\n");
			writer.flush();

			Files.copy(file.toPath(), byteStream);
			byteStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
			writer.append("--").append(boundary).append("--\r\n");
			writer.flush();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("x-apikey", apiKey)
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.POST(HttpRequest.BodyPublishers.ofByteArray(byteStream.toByteArray())).build();

			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
		return json.getAsJsonObject("data").get("id").getAsString();
	}


}
